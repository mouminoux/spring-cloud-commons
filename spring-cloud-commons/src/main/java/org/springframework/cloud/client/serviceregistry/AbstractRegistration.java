package org.springframework.cloud.client.serviceregistry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author Spencer Gibb
 */
public class AbstractRegistration implements Registration, HasDynamicPort, ApplicationEventPublisherAware {
	protected Integer port;
	protected ApplicationEventPublisher publisher;

	@Override
	public boolean isInitialized() {
		return this.port != null && this.port > 0;
	}

	@Override
	public int getPort() {
		return this.port == null? 0 : this.port;
	}

	@Override
	public void setPort(int port) {
		if (this.port == null) {
			this.port = port;
			if (this.publisher != null) {
				this.publisher.publishEvent(new RegistrationInitializedEvent(this, this));
			}
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
}
