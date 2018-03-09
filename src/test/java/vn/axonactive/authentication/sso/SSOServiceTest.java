package vn.axonactive.authentication.sso;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;

public class SSOServiceTest {

	SSOService service;
	private String token;
	private String ip;
	private String sessionId;
	private String username;

	@Before
	public void before() {
		service = new SSOService();
		token = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "api.token");
		ip = "192.168.85.49";
		sessionId = "henID";
		Optional<String> userName = service.getUserNameLoggedInByThisIp(ip, token);
		if (userName.isPresent()) {
			service.logout(userName.get(), token);
		}
		username = "tshen";
	}

	@Test
	public void test_addSession_success() {
		Assert.assertTrue(service.addSession(ip, username, sessionId, token));
		
		String actual = service.getUserNameLoggedInByThisIp(ip, token).orElse(StringUtils.EMPTY);
		Assert.assertEquals(username, actual);
		
	}
	
	@Test
	public void test_addSession_when_already_login() {
		Assert.assertTrue(service.addSession(ip, username, sessionId, token));
		
		// same ip and username
		Assert.assertFalse(service.addSession(ip, username, sessionId, token));
		
		// same ip + different username
		Assert.assertFalse(service.addSession(ip, username + "test", sessionId, token));
		
		// different ip + same username
		Assert.assertFalse(service.addSession("192.168.73.167", username, sessionId, token));
	}
	
	@Test
	public void getUserNameLoggedInByThisIp_Should_Return_OptionalError_When_NotLogin() {
		Optional<String> expected = Optional.empty();
		Optional<String> actual = service.getUserNameLoggedInByThisIp(ip, token);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void getUserNameLoggedInByThisIp_Should_Return_OptionalError_When_Already_Login() {
		String expected = username;
		Assert.assertTrue(service.addSession(ip, username, sessionId, token));
		Optional<String> actual = service.getUserNameLoggedInByThisIp(ip, token);
		Assert.assertEquals(expected, actual.get());
	}
	
	@After
	public void after() {
		service.logout(username, token);
	}
}
