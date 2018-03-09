package vn.axonactive.authentication.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;

import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.sso.LDAPConnectionException;
import vn.axonactive.authentication.sso.LDAPService;
import vn.axonactive.authentication.sso.SSOService;
import vn.axonactive.authentication.user.UserDTO;
import vn.axonactive.authentication.user.UserService;

@RunWith(PowerMockRunner.class)
public class LoginServiceTest {
	@Mock(name = "ldapService") // same name as private var
	private LDAPService ldapService;

	@Mock(name = "ssoService")
	private SSOService ssoService;

	@Mock(name = "userService")
	private UserService userService;

	@InjectMocks
	private LoginService loginService = new LoginService();

	private static final String API_TOKEN = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "api.token");

	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_IP = "192.168.111.111";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_SESSION = "fakesession_CAN_LOGIN_TO_LDAP_AND_SSO";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT = "ldapandssoaccount";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD = "ldapandssopassword";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_FULLNAME = "ldapandssoaccount fullname";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_POSITION = "ldapandssoaccount position";

	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_2_ACCOUNT = "ldapandsso2account";
	private static final String CAN_LOGIN_TO_LDAP_AND_SSO_2_PASSWORD = "ldapandsso2password";

	private static final String CAN_LOGIN_TO_LDAP_IP = "192.168.222.222";
	private static final String CAN_LOGIN_TO_LDAP_SESSION = "fakesession_CAN_LOGIN_TO_LDAP";
	private static final String CAN_LOGIN_TO_LDAP_ACCOUNT = "ldapaccount";
	private static final String CAN_LOGIN_TO_LDAP_PASSWORD = "ldappassword";

	private static final String CAN_LOGIN_TO_SSO_IP = "192.168.333.333";
	private static final String CAN_LOGIN_TO_SSO_SESSION = "fakesession_CAN_LOGIN_TO_SSO";
	private static final String CAN_LOGIN_TO_SSO_ACCOUNT = "ssoaccount";
	private static final String CAN_LOGIN_TO_SSO_PASSWORD = "ssopassword";
	private static final String CAN_LOGIN_TO_SSO_FULLNAME = "ssoaccount fullname";
	private static final String CAN_LOGIN_TO_SSO_POSITION = "ssoaccount position";

	private static final String CAN_NOT_LOGIN_IP = "192.168.444.444";
	private static final String CAN_NOT_LOGIN_SESSION = "fakesession_CAN_NOT_LOGIN";
	private static final String CAN_NOT_LOGIN_ACCOUNT = "cannotloginaccount";
	private static final String CAN_NOT_LOGIN_PASSWORD = "cannotloginpassword";

	private void mockSSOService(String ip, String session, String account) {
		Mockito.when(ssoService.addSession(ip, account, session, API_TOKEN)).then(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				Mockito.when(ssoService.getUserNameLoggedInByThisIp(ip, API_TOKEN)).thenReturn(Optional.of(account));
				return null;
			}
		});

		Mockito.when(ssoService.logout(account, API_TOKEN)).then(new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				Mockito.when(ssoService.getUserNameLoggedInByThisIp(ip, API_TOKEN)).thenReturn(Optional.empty());

				return null;
			}
		});
	}

	@Before
	public void init()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Field field = IpStorage.class.getDeclaredField("ipUserMap");
		field.setAccessible(true);
		field.set(null, new HashMap<String, UserDTO>());

		/* CAN_LOGIN_TO_LDAP_AND_SSO */
		{
			UserDTO userInfo = UserDTO.builder().fullName(CAN_LOGIN_TO_LDAP_AND_SSO_FULLNAME)
					.position(CAN_LOGIN_TO_LDAP_AND_SSO_POSITION).userName(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT)
					.sessionSet(new HashSet<>()).build();

			Mockito.when(
					ldapService.authenticate(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD))
					.thenReturn(true);

			Mockito.when(userService.getUserInfo(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT))
					.thenReturn(Optional.of(new UserDTO(userInfo)));

			mockSSOService(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
					CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT);

			Mockito.when(ssoService.getUserNameLoggedInByThisIp(CAN_LOGIN_TO_LDAP_AND_SSO_IP, API_TOKEN))
					.thenReturn(Optional.empty());
		}

		/* CAN_LOGIN_TO_LDAP_AND_SSO_2 */
		{
			Mockito.when(
					ldapService.authenticate(CAN_LOGIN_TO_LDAP_AND_SSO_2_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_2_PASSWORD))
					.thenReturn(true);
		}

		/* CAN_LOGIN_TO_LDAP */
		{
			Mockito.when(ldapService.authenticate(CAN_LOGIN_TO_LDAP_ACCOUNT, CAN_LOGIN_TO_LDAP_PASSWORD))
					.thenReturn(true);

			Mockito.when(userService.getUserInfo(CAN_LOGIN_TO_LDAP_ACCOUNT)).thenReturn(Optional.empty());

			Mockito.when(ssoService.getUserNameLoggedInByThisIp(CAN_LOGIN_TO_LDAP_IP, API_TOKEN))
					.thenReturn(Optional.empty());

		}

		/* ACCOUNT_CAN_LOGIN_TO_SSO */
		{
			UserDTO userInfo = UserDTO.builder().fullName(CAN_LOGIN_TO_SSO_FULLNAME).position(CAN_LOGIN_TO_SSO_POSITION)
					.userName(CAN_LOGIN_TO_SSO_ACCOUNT).sessionSet(new HashSet<>()).build();

			Mockito.when(ldapService.authenticate(CAN_LOGIN_TO_SSO_ACCOUNT, CAN_LOGIN_TO_SSO_PASSWORD))
					.thenReturn(false);

			Mockito.when(userService.getUserInfo(CAN_LOGIN_TO_SSO_ACCOUNT))
					.thenReturn(Optional.of(new UserDTO(userInfo)));

			mockSSOService(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION, CAN_LOGIN_TO_SSO_ACCOUNT);

			Mockito.when(ssoService.getUserNameLoggedInByThisIp(CAN_LOGIN_TO_SSO_IP, API_TOKEN))
					.thenReturn(Optional.empty());
		}

		/* ACCOUNT_CAN_NOT_LOGIN */
		{
			Mockito.when(ldapService.authenticate(CAN_NOT_LOGIN_ACCOUNT, CAN_NOT_LOGIN_PASSWORD)).thenReturn(false);

			Mockito.when(userService.getUserInfo(CAN_NOT_LOGIN_ACCOUNT)).thenReturn(Optional.empty());

			Mockito.when(ssoService.getUserNameLoggedInByThisIp(CAN_NOT_LOGIN_IP, API_TOKEN))
					.thenReturn(Optional.empty());
		}
	}

	@Test
	public void getUser_Should_ReturnNullJSON_When_UserNotLoggedIn_Case_CAN_LOGIN_TO_LDAP_AND_SSO() {
		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);
		Assert.assertFalse(userInfo.isPresent());
	}

	@Test
	public void getUser_Should_ReturnNullJSON_When_UserNotLoggedIn_Case_CAN_LOGIN_TO_LDAP_IP() {
		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_IP);
		Assert.assertFalse(userInfo.isPresent());
	}

	@Test
	public void getUser_Should_ReturnNullJSON_When_UserNotLoggedIn_Case_CAN_LOGIN_TO_SSO() {
		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_SSO_IP);
		Assert.assertFalse(userInfo.isPresent());
	}

	@Test
	public void getUser_Should_ReturnNullJSON_When_UserNotLoggedIn_Case_CAN_NOT_LOGIN() {
		Optional<UserDTO> userInfo = loginService.getUser(CAN_NOT_LOGIN_IP);
		Assert.assertFalse(userInfo.isPresent());
	}

	@Test
	public void getUser_Should_ReturnNullJSON_When_UserLoggedInThenLogoutBySSOAPIThenRemoveItOnStorage_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);
		Assert.assertFalse(userInfo.isPresent());
	}

	@Test
	public void getUser_Should_ReturnEmptyJSONWithInfo_When_UserLoggedInSSOButNotLoginInServer_Case_CAN_LOGIN_TO_LDAP_AND_SSO() {
		ssoService.addSession(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void getUser_Should_ReturnFalse_When_UserLoggedIn_Case_CAN_LOGIN_TO_LDAP() {
		ssoService.addSession(CAN_LOGIN_TO_LDAP_IP, CAN_LOGIN_TO_LDAP_ACCOUNT, CAN_LOGIN_TO_LDAP_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_IP);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_LOGIN_TO_LDAP_ACCOUNT, API_TOKEN);
	}

	@Test
	public void getUser_Should_ReturnJSONWithInfo_When_UserLoggedInSSOButNotLoginInServer_Case_CAN_LOGIN_TO_SSO() {
		ssoService.addSession(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_ACCOUNT, CAN_LOGIN_TO_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_SSO_IP);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void getUser_Should_ReturnFalse_When_UserLoggedIn_Case_CAN_NOT_LOGIN() {
		ssoService.addSession(CAN_NOT_LOGIN_IP, CAN_NOT_LOGIN_ACCOUNT, CAN_NOT_LOGIN_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.getUser(CAN_NOT_LOGIN_IP);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_NOT_LOGIN_ACCOUNT, API_TOKEN);
	}

	@Test
	public void getUser_Should_ReturnJSONWithInfo_When_UserLoggedInByLoginService_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		IpStorage.getIpTimeoutMap().add(CAN_LOGIN_TO_LDAP_AND_SSO_IP);

		loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Optional<UserDTO> userInfo = loginService.getUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getUserName());
		Assert.assertNotNull(userInfo.get().getPosition());
		Assert.assertNotNull(userInfo.get().getSessionSet());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_NotLoginedBefore_Case_CAN_LOGIN_TO_LDAP_AND_SSO() {
		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);

		Assert.assertFalse(loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, API_TOKEN).isPresent());
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_NotLoginedBefore_CAN_LOGIN_TO_SSO() {
		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);

		Assert.assertFalse(loginService.autoLogin(CAN_LOGIN_TO_SSO_IP, API_TOKEN).isPresent());
	}

	@Test
	public void autoLogin_Should_ReturnTrue_When_LoginedBefore_Case_CAN_LOGIN_TO_LDAP_AND_SSO() {
		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnTrue_When_LoginedBefore_Case_CAN_LOGIN_TO_SSO() {
		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_ACCOUNT, CAN_LOGIN_TO_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_LoginedOnSystemBefore_Case_CAN_LOGIN_TO_LDAP_AND_SSO() {
		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION + "anotherSession");

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(2, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_LoginedOnSystemBefore_Case_CAN_LOGIN_TO_SSO() {
		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_ACCOUNT, CAN_LOGIN_TO_SSO_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.autoLogin(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION);

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.autoLogin(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION + "anotherSession");

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(2, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_ldapLogin_Should_ReturnTrue_When_LoginedBeforeByLDAP_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION + "anotherSessionID");

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(2, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_AnyCase_Case_CAN_LOGIN_TO_LDAP() {
		ssoService.logout(CAN_LOGIN_TO_LDAP_ACCOUNT, API_TOKEN);

		Assert.assertFalse(loginService.autoLogin(CAN_LOGIN_TO_LDAP_IP, API_TOKEN).isPresent());

		ssoService.logout(CAN_LOGIN_TO_LDAP_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_LOGIN_TO_LDAP_IP, CAN_LOGIN_TO_LDAP_ACCOUNT, CAN_LOGIN_TO_LDAP_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_LOGIN_TO_LDAP_IP, CAN_LOGIN_TO_LDAP_SESSION);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_LOGIN_TO_LDAP_ACCOUNT, API_TOKEN);
	}

	@Test
	public void autoLogin_Should_ReturnFalse_When_AnyCase_Case_CAN_NOT_LOGIN() {
		ssoService.logout(CAN_NOT_LOGIN_ACCOUNT, API_TOKEN);

		Assert.assertFalse(loginService.autoLogin(CAN_NOT_LOGIN_IP, API_TOKEN).isPresent());

		ssoService.logout(CAN_NOT_LOGIN_ACCOUNT, API_TOKEN);
		ssoService.addSession(CAN_NOT_LOGIN_IP, CAN_NOT_LOGIN_ACCOUNT, CAN_NOT_LOGIN_SESSION, API_TOKEN);

		Optional<UserDTO> userInfo = loginService.autoLogin(CAN_NOT_LOGIN_IP, CAN_NOT_LOGIN_SESSION);

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_NOT_LOGIN_ACCOUNT, API_TOKEN);
	}

	@Test
	public void ldapLogin_Should_ReturnTrue_When_NotLoginedBefore_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void ldapLogin_Should_ReturnNull_When_AnyCase_Case_CAN_LOGIN_TO_LDAP() throws LoginAlreadyException {
		UserDTO userdto = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_IP, CAN_LOGIN_TO_LDAP_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_ACCOUNT, CAN_LOGIN_TO_LDAP_PASSWORD)).orElse(new UserDTO());

		assertTrue(Objects.isNull(userdto.getUserName()));

		ssoService.logout(CAN_LOGIN_TO_LDAP_ACCOUNT, API_TOKEN);
	}

	@Test
	public void ldapLogin_Should_ReturnFalse_When_AnyCase_Case_CAN_LOGIN_TO_SSO() throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_SSO_IP, CAN_LOGIN_TO_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_SSO_ACCOUNT, CAN_LOGIN_TO_SSO_PASSWORD));

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_LOGIN_TO_SSO_ACCOUNT, API_TOKEN);
	}

	@Test
	public void ldapLogin_Should_ReturnFalse_When_AnyCase_CAN_NOT_LOGIN() throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_NOT_LOGIN_IP, CAN_NOT_LOGIN_SESSION,
				new LoginDTO(CAN_NOT_LOGIN_ACCOUNT, CAN_NOT_LOGIN_PASSWORD));

		Assert.assertFalse(userInfo.isPresent());

		ssoService.logout(CAN_NOT_LOGIN_ACCOUNT, API_TOKEN);
	}

	@Ignore
	@Test(expected = LoginAlreadyException.class)
	public void ldapLogin_Should_ThrowException_When_LoginedBeforeBySSO_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		ssoService.addSession(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION, API_TOKEN);

		loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION + "anotherSessionID",
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));
	}

	@Ignore
	@Test(expected = LoginAlreadyException.class)
	public void ldapLogin_Should_ThrowException_When_LoginedBeforeByLDAP_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION + "anotherSessionID",
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Ignore
	@Test(expected = LoginAlreadyException.class)
	public void ldapLogin_Should_ThrowException_When_LoginedBy2LDAPAccount_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION + "anotherSessionID",
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_2_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_2_PASSWORD));

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);
	}

	@Test(expected = LDAPConnectionException.class)
	public void ldapLogin_Should_ThrowLDAPConnection_When_LDAPConnectionIsError_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Mockito.when(ldapService.authenticate(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD))
				.thenThrow(LDAPConnectionException.class);

		loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));
	}

	@Test
	public void logout_Should_RemoveInStorage_When_Logout_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		loginService.logoutByUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);

		Assert.assertFalse(IpStorage.getIpUserMap().containsKey(CAN_LOGIN_TO_LDAP_AND_SSO_IP));
	}

	@Test
	public void logout_Should_RemoveInStorageAndSSOReturnFalse_When_SSOLogoutBefore_Case_CAN_LOGIN_TO_LDAP_AND_SSO()
			throws LoginAlreadyException {
		Optional<UserDTO> userInfo = loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP,
				CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, CAN_LOGIN_TO_LDAP_AND_SSO_PASSWORD));

		Assert.assertTrue(userInfo.isPresent());
		Assert.assertTrue(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT.equals(userInfo.get().getUserName()));
		Assert.assertEquals(1, userInfo.get().getSessionSet().size());
		Assert.assertNotNull(userInfo.get().getFullName());
		Assert.assertNotNull(userInfo.get().getPosition());

		ssoService.logout(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, API_TOKEN);

		loginService.logoutByUser(CAN_LOGIN_TO_LDAP_AND_SSO_IP);

		Assert.assertFalse(IpStorage.getIpUserMap().containsKey(CAN_LOGIN_TO_LDAP_AND_SSO_IP));
	}

	@Test
	public void login_Should_StartFrozenAccount_When_OverMaxCounter() {
		IpStorage.getUserCounterMap().put(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, 2);
		loginService.ldapLogin(CAN_LOGIN_TO_LDAP_AND_SSO_IP, CAN_LOGIN_TO_LDAP_AND_SSO_SESSION,
				new LoginDTO(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT, "abc"));
		assertTrue(IpStorage.isFrozenNow(CAN_LOGIN_TO_LDAP_AND_SSO_ACCOUNT));
	}

}
