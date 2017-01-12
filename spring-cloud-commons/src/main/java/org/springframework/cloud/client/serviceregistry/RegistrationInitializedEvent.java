package org.springframework.cloud.client.serviceregistry;

import org.springframework.context.ApplicationEvent;

/**
 * @author Spencer Gibb
 */
public class RegistrationInitializedEvent extends ApplicationEvent {

	private final Registration registration;

	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public RegistrationInitializedEvent(Object source, Registration registration) {
		super(source);
		this.registration = registration;
	}

	public Registration getRegistration() {
		return registration;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("RegistrationInitializedEvent{");
		sb.append("registration=").append(registration);
		sb.append('}');
		return sb.toString();
	}
}
