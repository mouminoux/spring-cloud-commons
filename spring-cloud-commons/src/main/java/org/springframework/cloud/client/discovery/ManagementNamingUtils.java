package org.springframework.cloud.client.discovery;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContext;

/**
 * @author Spencer Gibb
 */
public class ManagementNamingUtils {

	private static RelaxedPropertyResolver getResolver(ApplicationContext context) {
		return new RelaxedPropertyResolver(context.getEnvironment());
	}

	/**
	 * @return the serviceId of the Management Service
	 */
	public static String getManagementServiceId(ApplicationContext context) {
		// TODO: configurable management suffix
		return context.getId() + ":management";
	}

	/**
	 * @return the service name of the Management Service
	 */
	public static String getManagementServiceName(ApplicationContext context) {
		// TODO: configurable management suffix
		return getAppName(context) + ":management";
	}

	/**
	 * @return the app name, currently the spring.application.name property
	 */
	public static String getAppName(ApplicationContext context) {
		return getResolver(context).getProperty("spring.application.name", "application");
	}
}
