package vn.axonactive.authentication.sso;


import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import vn.axonactive.authentication.sso.LDAPService;

public class LDAPServiceTest {
	private static final String DEFAULT_USERNAME = "mrbs";
	private static final String DEFAULT_PW = "mrbs1234";
	
	LDAPService service;
	
	@Before
	public void init() {
		service = new LDAPService();
	}
	// valid username and password
	@Test
	public void authenticate_ValidAccount_ReturnTrue() {
		boolean isAuthenticated = service.authenticate(DEFAULT_USERNAME, DEFAULT_PW);
		Assert.assertEquals(true, isAuthenticated);
	}

	// invalid password
	@Test
	public void authenticate_InvalidPassword_ReturnFalse() {
		boolean isAuthenticated = service.authenticate("hqkhai", "Axon");
		Assert.assertEquals(false, isAuthenticated);
	}

	// invalid username
	@Test
	public void authenticate_InvalidUsername_ReturnFalse() {
		boolean isAuthenticated = service.authenticate("kljngodgo", DEFAULT_PW);
		Assert.assertEquals(false, isAuthenticated);
	}

	// invalid username and password
	@Test
	public void authenticate_InvalidAccount_ReturnFalse() {
		boolean isAuthenticated = service.authenticate("asdad", "hjbgfj");
		Assert.assertEquals(false, isAuthenticated);
	}

	// username blank
	@Test
	public void authenticate_BlankUsername_ReturnFalse() {
		boolean isAuthenticated = service.authenticate("", DEFAULT_PW);
		Assert.assertEquals(false, isAuthenticated);
	}

	// password blank
	@Test
	public void authenticate_BlankPassword_ReturnFalse() {
		boolean isAuthenticated = service.authenticate(DEFAULT_USERNAME, "");
		Assert.assertEquals(false, isAuthenticated);
	}

	// username null
	@Test
	public void authenticate_NullUsername_ReturnFalse() {
		boolean isAuthenticated = service.authenticate(null, DEFAULT_PW);
		Assert.assertEquals(false, isAuthenticated);
	}

	// password null
	@Test
	public void authenticate_NullPassword_ReturnFalse() {
		boolean isAuthenticated = service.authenticate(DEFAULT_USERNAME, null);
		Assert.assertEquals(false, isAuthenticated);
	}
	
	// username null password null
	@Test
	public void authenticate_NullPasswordNullUsername_ReturnFalse() {
		boolean isAuthenticated = service.authenticate(null, null);
		Assert.assertEquals(false, isAuthenticated);
	}

	
	@Test
	public void getEmployeeName_Should_ReturnCorrectName_ValidUsername() {
		Optional<String> employeeName = service.getEmployeeName("nmdang");
        Assert.assertTrue(employeeName.isPresent());
		Assert.assertEquals("Nguyen Manh Dang", employeeName.get());
	}

	@Test
	public void getEmployeeName_Should_ReturnFalse_When_InvalidUsername() {
	    Optional<String> employeeName = service.getEmployeeName("kjabdkjb");
        Assert.assertFalse(employeeName.isPresent());
	}

}
