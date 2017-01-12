package org.springframework.cloud.client.serviceregistry;

import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;

/**
 * @author Spencer Gibb
 */
public class DynamicPortApplicationListener implements ApplicationEventPublisherAware {

	private HasDynamicPort hasDynamicPort;
	private ApplicationEventPublisher publisher;

	public DynamicPortApplicationListener(HasDynamicPort hasDynamicPort) {
		this.hasDynamicPort = hasDynamicPort;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@EventListener(EmbeddedServletContainerInitializedEvent.class)
	public void onEvent(EmbeddedServletContainerInitializedEvent event) {
		// TODO: take SSL into account
		// Don't register the management port as THE port
		if (!"management".equals(event.getApplicationContext().getNamespace())) {
			int port = event.getEmbeddedServletContainer().getPort();
			this.hasDynamicPort.setPort(port);
		}
	}
}
