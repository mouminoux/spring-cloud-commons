package org.springframework.cloud.client.serviceregistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractAutoServiceRegistrationTests.Config.class,
		properties = {"SPRING_APPLICATION_NAME=myautoservregtest1", "management.port=0"},
		webEnvironment = RANDOM_PORT)
public class AbstractAutoServiceRegistrationTests {

	@Autowired
	protected TestAutoServiceRegistration autoRegistration;

	@Autowired
	protected TestRegistration registration;

	@Value("${local.server.port}")
	private int port;

	@Value("${local.management.port}")
	protected int managementPort;

	protected int getPort() {
		return port;
	}

	@Test
	public void portsWork() {
		assertNotEquals("Registration port is zero", 0, registration.getPort());
		assertNotEquals("Registration port is management port", managementPort, registration.getPort());
		assertEquals("Registration port is wrong", getPort(), registration.getPort());

		assertTrue("AutoServiceRegistration not running", autoRegistration.isRunning());
		assertEquals("AutoServiceRegistration appName is wrong", getApplicationName(), autoRegistration.getAppName());

		assertThat("ServiceRegistry is wrong type", autoRegistration.getServiceRegistry(), is(instanceOf(TestServiceRegistry.class)));
		TestServiceRegistry serviceRegistry = (TestServiceRegistry) autoRegistration.getServiceRegistry();
		assertTrue("ServiceRegistry not registered", serviceRegistry.isRegistered());
	}

	protected String getApplicationName() {
		return "myautoservregtest1";
	}

	@EnableAutoConfiguration
	@Configuration
	@Import({AutoServiceRegistrationConfiguration.class,
			AutoServiceRegistrationAutoConfiguration.class})
	public static class Config {
		@Bean
		@ConditionalOnMissingBean
		public TestRegistration registration() {
			return new TestRegistration();
		}

		@Bean
		public TestAutoServiceRegistration testAutoServiceRegistration() {
			return new TestAutoServiceRegistration(registration());
		}
	}

	public static class TestRegistration extends AbstractRegistration {}

	public static class TestServiceRegistry implements ServiceRegistry<TestRegistration> {
		private boolean registered = false;
		private boolean deregistered = false;

		@Override
		public void register(TestRegistration registration) {
			this.registered = true;
		}

		@Override
		public void deregister(TestRegistration registration) {
			this.deregistered = true;
		}

		@Override
		public void close() { }

		@Override
		public void setStatus(TestRegistration registration, String status) {
			//TODO: test setStatus
		}

		@Override
		public Object getStatus(TestRegistration registration) {
			//TODO: test getStatus
			return null;
		}

		boolean isRegistered() {
			return registered;
		}

		boolean isDeregistered() {
			return deregistered;
		}
	}

	public static class TestAutoServiceRegistration extends AbstractAutoServiceRegistration<TestRegistration> {

		private final TestRegistration registration;
		private final TestRegistration mgmtRegistration = new TestRegistration();

		@Override
		protected String getAppName() {
			return super.getAppName();
		}

		protected TestAutoServiceRegistration(TestRegistration registration) {
			super(new TestServiceRegistry());
			this.registration = registration;
		}

		@Override
		protected TestRegistration getRegistration() {
			return this.registration;
		}

		@Override
		protected TestRegistration getManagementRegistration() {
			return this.mgmtRegistration;
		}

		@Override
		protected boolean isEnabled() {
			return true;
		}


	}
}
