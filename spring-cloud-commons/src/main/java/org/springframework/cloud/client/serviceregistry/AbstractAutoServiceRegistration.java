package org.springframework.cloud.client.serviceregistry;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.client.discovery.ManagementServerPortUtils;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;

/**
 * Lifecycle methods that may be useful and common to {@link ServiceRegistry} implementations.
 *
 * TODO: document the lifecycle
 *
 * @param <R> registration type passed to the {@link ServiceRegistry}.
 *
 * @author Spencer Gibb
 */
@SuppressWarnings("deprecation")
public abstract class AbstractAutoServiceRegistration<R extends Registration> implements AutoServiceRegistration,
		SmartLifecycle, Ordered, ApplicationContextAware, ApplicationListener<RegistrationInitializedEvent> {

	private ServiceRegistry<R> serviceRegistry;

	private static final Log logger = LogFactory.getLog(AbstractAutoServiceRegistration.class);

	private boolean autoStartup = true;

	private AtomicBoolean running = new AtomicBoolean(false);

	private int order = 0;

	private ApplicationContext context;

	private RelaxedPropertyResolver propertyResolver;


	protected AbstractAutoServiceRegistration(ServiceRegistry<R> serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	protected ServiceRegistry<R> getServiceRegistry() {
		return this.serviceRegistry;
	}

	protected abstract R getRegistration();

	protected abstract R getManagementRegistration();

	/**
	 * Register the local service with the {@link ServiceRegistry}
	 */
	protected void register() {
		this.serviceRegistry.register(getRegistration());
	}

	/**
	 * Register the local management service with the {@link ServiceRegistry}
	 */
	protected void registerManagement() {
		this.serviceRegistry.register(getManagementRegistration());
	}

	/**
	 * De-register the local service with the {@link ServiceRegistry}
	 */
	protected void deregister() {
		this.serviceRegistry.deregister(getRegistration());
	}

	/**
	 * De-register the local management service with the {@link ServiceRegistry}
	 */
	protected void deregisterManagement() {
		this.serviceRegistry.deregister(getManagementRegistration());
	}

	protected ApplicationContext getContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
		this.propertyResolver = new RelaxedPropertyResolver(this.context.getEnvironment());
	}

	@Override
	public void onApplicationEvent(RegistrationInitializedEvent event) {
		this.start();
	}

	@Override
	public void start() {
		if (!isEnabled()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Discovery Lifecycle disabled. Not starting");
			}
			return;
		}

		if (!this.running.get() && this.getRegistration().isInitialized()) {
			register();
			if (shouldRegisterManagement()) {
				registerManagement();
			}
			this.context.publishEvent(new InstanceRegisteredEvent<>(this,
					getRegistration()));
			this.running.compareAndSet(false, true);
		}
	}

	@Override
	public void stop() {
		if (this.getRunning().compareAndSet(true, false) && isEnabled()) {
			deregister();
			if (shouldRegisterManagement()) {
				deregisterManagement();
			}
			this.serviceRegistry.close();
		}
	}

	@Override
	public void stop(Runnable callback) {
		try {
			stop();
		} catch (Exception e) {
			logger.error("A problem occurred attempting to stop discovery lifecycle", e);
		}
		callback.run();
	}

	/**
	 * @return if the management service should be registered with the {@link ServiceRegistry}
	 */
	protected boolean shouldRegisterManagement() {
		return ManagementServerPortUtils.getPort(this.context) != null && ManagementServerPortUtils.isDifferent(this.context);
	}

	/**
	 * @return true, if the {@link AutoServiceRegistration} is enabled
	 */
	protected abstract boolean isEnabled();

	@PreDestroy
	public void destroy() {
		stop();
	}

	@Override
	public boolean isRunning() {
		return this.running.get();
	}

	protected AtomicBoolean getRunning() {
		return running;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return this.autoStartup;
	}

}
