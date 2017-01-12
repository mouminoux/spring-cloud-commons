package org.springframework.cloud.client.serviceregistry;

/**
 * @author Spencer Gibb
 */
public interface HasDynamicPort {
	int getPort();
	void setPort(int port);
}
