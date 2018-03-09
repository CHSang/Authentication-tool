package vn.axonactive.authentication.login;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.domain.utils.FacesContextUtils;
import vn.axonactive.authentication.domain.utils.IpUtils;
import vn.axonactive.authentication.domain.utils.SessionUtils;
import vn.axonactive.authentication.user.UserDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ IpUtils.class, FacesContext.class, FacesContextUtils.class, IpStorage.class, SessionUtils.class })
public class LoginControllerTest {

	private final String IP = "192.168.85.49";
	private final String SESSION_ID = "SESSION_ID";
	private final String USER_NAME = "tshen";
	private final String PASSWORD = "PASSWORD";
	private final String FULL_NAME = "Trần Sầm Hên";
	private final String POSITION = "Senior QC";
	private final Set<String> SESSIONSET = new HashSet<>();
	private final String SAMPLE_APLICATION_CONTEXT_PATH = "http://localhost:8080";
	private final String ERROR_MESSAGE = "errormessage";
	private final String ERRPR_MESSAGE_USERNAME_PASSWORD = "Please enter your username and password.";
	private final String LOGIN = URI.create(SAMPLE_APLICATION_CONTEXT_PATH + LoginEnum.LOGIN_PATH.getValue()).toString();
	private final String USER_FROZEN_IN_MIN = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.frozen.in.min");
	private final String ERROR_MESSAGE_FROZEN = ConfigPropertiesUtils.getProperty(
			ConfigurationEnum.MESSAGES_PROPERTIES.getValue(), "error.loginPage.frozen",
			new String[] { USER_FROZEN_IN_MIN });
	private static final String ERROR_MESSAGE_INVALID_USER = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.MESSAGES_PROPERTIES.getValue(), "error.invalidusernameorpass");
	private static final String HOME_PATH = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.homepage.url");
	private final Optional<UserDTO> USER_DTO = Optional.of(new UserDTO(USER_NAME, FULL_NAME, POSITION, SESSIONSET));
	private final Optional<UserDTO> NULL_USER_DTO = Optional.of(new UserDTO());
	private final LoginDTO LOGIN_DTO = new LoginDTO(USER_NAME, PASSWORD);

	LoginController loginController;

	@Mock
	HttpServletRequest request;

	@Mock
	FacesContext mockFacesContext;

	@Mock
	ExternalContext mockExternalContext;

	@Mock
	HttpServletRequest mockHttpServletRequest;

	@Mock
	Flash mockFlash;

	@Mock
	LoginService loginService;

	@Before
	public void init() {
		PowerMockito.mockStatic(IpUtils.class);
		PowerMockito.mockStatic(FacesContext.class);
		PowerMockito.mockStatic(FacesContextUtils.class);
		PowerMockito.mockStatic(IpStorage.class);
		PowerMockito.mockStatic(SessionUtils.class);

		Mockito.when(FacesContext.getCurrentInstance()).thenReturn(this.mockFacesContext);
		Mockito.when(SessionUtils.getSessionId()).thenReturn(SESSION_ID);
		Mockito.when(SessionUtils.getContextPath()).thenReturn(SAMPLE_APLICATION_CONTEXT_PATH);
		Mockito.when(this.mockFacesContext.getExternalContext()).thenReturn(this.mockExternalContext);
		Mockito.when(this.mockExternalContext.getRequest()).thenReturn(mockHttpServletRequest);
		Mockito.when(this.mockExternalContext.getFlash()).thenReturn(mockFlash);
		Mockito.when(FacesContextUtils.getFlash()).thenReturn(mockFlash);
		Mockito.when(this.mockExternalContext.getApplicationContextPath()).thenReturn(SAMPLE_APLICATION_CONTEXT_PATH);
		Mockito.when(this.mockHttpServletRequest.getContextPath()).thenReturn(SAMPLE_APLICATION_CONTEXT_PATH);
		Mockito.when(IpUtils.getClientIPAddress(Mockito.any())).thenReturn(IP);

		loginController = new LoginController();
		loginController.setLoginDTO(LOGIN_DTO);
		loginController.setLoginService(loginService);
	}

	@Test
	public void testValidateUsernamePassword_GivenEmptyUserDTO_ShouldPutMessageAndRedirectToLoginPage() {
		loginController.setLoginDTO(new LoginDTO());

		loginController.validateUsernamePassword();

		Mockito.verify(mockFlash, Mockito.times(1)).put(ERROR_MESSAGE, ERRPR_MESSAGE_USERNAME_PASSWORD);

		PowerMockito.verifyStatic(VerificationModeFactory.times(1));
		FacesContextUtils.redirect(LOGIN);
	}

	@Test
	public void testValidateUsernamePassword_GivenFrozenUserName_ShouldPutMessageAndRedirectToLoginPage() {
		Mockito.when(IpStorage.isFrozenNow(USER_NAME)).thenReturn(true);

		loginController.validateUsernamePassword();

		Mockito.verify(mockFlash, Mockito.times(1)).put(ERROR_MESSAGE, ERROR_MESSAGE_FROZEN);

		PowerMockito.verifyStatic(VerificationModeFactory.times(1));
		FacesContextUtils.redirect(LOGIN);
	}

	@Test
	public void testValidateUsernamePassword_GivenValidUser_ShouldhandleValidUserAndRedirect() {
		Mockito.when(IpStorage.isFrozenNow(USER_NAME)).thenReturn(false);
		Mockito.when(loginService.ldapLogin(IP, SESSION_ID, LOGIN_DTO)).thenReturn(USER_DTO);

		loginController.validateUsernamePassword();

		PowerMockito.verifyStatic(VerificationModeFactory.times(1));
		FacesContextUtils.redirect(HOME_PATH);
	}

	@Test
	public void testValidateUsernamePassword_GivenInValidUser_ShouldhandleInValidUserAndRedirect() {
		Mockito.when(IpStorage.isFrozenNow(USER_NAME)).thenReturn(false);
		Mockito.when(loginService.ldapLogin(IP, SESSION_ID, LOGIN_DTO)).thenReturn(Optional.empty());

		loginController.validateUsernamePassword();

		Mockito.verify(mockFlash, Mockito.times(1)).put(ERROR_MESSAGE, ERROR_MESSAGE_INVALID_USER);

		PowerMockito.verifyStatic(VerificationModeFactory.times(1));
		FacesContextUtils.redirect(LOGIN);
	}
	
	@Test
	public void testLogout_GivenIP_ShouldRedirectToLoginPage() {
		Mockito.when(IpStorage.isFrozenNow(USER_NAME)).thenReturn(false);
		Mockito.when(loginService.ldapLogin(IP, SESSION_ID, LOGIN_DTO)).thenReturn(Optional.empty());

		loginController.logOut();

		PowerMockito.verifyStatic(VerificationModeFactory.times(1));
		FacesContextUtils.redirect(LOGIN);
	}
}
