package org.springframework.cloud.client.serviceregistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

/**
 * @author Spencer Gibb
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractAutoServiceRegistrationTests.Config.class,
		properties = {"SPRING_APPLICATION_NAME=myautoservregtest2", "management.port=0"},
		webEnvironment = DEFINED_PORT)
public class AbstractAutoServiceRegistrationNonRandomPortTests extends AbstractAutoServiceRegistrationTests {

	private static int definedPort;

	@BeforeClass
	public static void setup() {
		definedPort = SocketUtils.findAvailableTcpPort();
		System.setProperty("server.port", String.valueOf(definedPort));
	}

	@Override
	public int getPort() {
		return definedPort;
	}

	@Override
	protected String getApplicationName() {
		return "myautoservregtest2";
	}

	@AfterClass
	public static void teardown() {
		System.clearProperty("server.port");
	}

}
