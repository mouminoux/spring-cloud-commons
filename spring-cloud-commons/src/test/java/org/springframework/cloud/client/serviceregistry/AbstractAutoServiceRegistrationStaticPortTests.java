package org.springframework.cloud.client.serviceregistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AbstractAutoServiceRegistrationStaticPortTests.Config3.class, AbstractAutoServiceRegistrationTests.Config.class},
		properties = {"SPRING_APPLICATION_NAME=myautoservregtest3", "management.port=0"},
		webEnvironment = RANDOM_PORT)
public class AbstractAutoServiceRegistrationStaticPortTests extends AbstractAutoServiceRegistrationTests {

	private static int definedPort;

	@BeforeClass
	public static void setup() {
		definedPort = SocketUtils.findAvailableTcpPort();
		System.setProperty("my.port", String.valueOf(definedPort));
	}

	@Override
	public int getPort() {
		return definedPort;
	}

	@Override
	protected String getApplicationName() {
		return "myautoservregtest3";
	}

	@AfterClass
	public static void teardown() {
		System.clearProperty("my.port");
	}

	@Configuration
	public static class Config3 {
		@Bean
		public TestRegistration registration() {
			TestRegistration registration = new TestRegistration();
			registration.setPort(definedPort);
			return registration;
		}
	}
}
