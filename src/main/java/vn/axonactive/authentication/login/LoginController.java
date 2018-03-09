package vn.axonactive.authentication.login;

import java.net.URI;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.domain.utils.FacesContextUtils;
import vn.axonactive.authentication.domain.utils.IpUtils;
import vn.axonactive.authentication.domain.utils.SessionUtils;
import vn.axonactive.authentication.user.UserDTO;

@ManagedBean
@SessionScoped
public class LoginController {

	@Setter
	@Inject
	LoginService loginService;

	private static final String ERROR_MESSAGE = "errormessage";
	private static final String CONTEXT_PATH = SessionUtils.getContextPath();
	private static final String LOGIN = URI.create(CONTEXT_PATH + LoginEnum.LOGIN_PATH.getValue()).toString();
	private static final String USER_FROZEN_IN_MIN = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.frozen.in.min");
	private static final String ERROR_MESSAGE_FROZEN = ConfigPropertiesUtils.getProperty(
			ConfigurationEnum.MESSAGES_PROPERTIES.getValue(), "error.loginPage.frozen",
			new String[] { USER_FROZEN_IN_MIN });
	private static final String ERROR_MESSAGE_INVALID_USER = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.MESSAGES_PROPERTIES.getValue(), "error.invalidusernameorpass");
	private static final String ERROR_MESSAGE_BLANK_FIELD = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.MESSAGES_PROPERTIES.getValue(), "error.validateloginform");
	private static final String HOME_PATH = ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.homepage.url");

	@Getter
	@Setter
	private String redirectFrom;

	@Getter
	@Setter
	private LoginDTO loginDTO = new LoginDTO();

	private boolean isUsernameOrPasswordEmpty() {
		if (StringUtils.isBlank(this.loginDTO.getUsername()) || StringUtils.isBlank(this.loginDTO.getPassword())) {
			this.loginDTO = new LoginDTO();

			this.putErrorMessageToView(ERROR_MESSAGE_BLANK_FIELD);
			this.redirectTo(LOGIN, SessionUtils.getParamString());
			return true;
		} else {
			return false;
		}
	}

	private void clearUsernameAndPassword() {
		this.loginDTO.setUsername(StringUtils.EMPTY);
		this.loginDTO.setPassword(StringUtils.EMPTY);
	}

	private void putErrorMessageToView(String message) {
		FacesContextUtils.getFlash().put(ERROR_MESSAGE, message);
	}

	private void redirectTo(String toURL, String param) {
		if (StringUtils.isBlank(param)) {
			if (!StringUtils.isBlank(this.redirectFrom)) {
				FacesContextUtils.redirect(redirectFrom);
			} else {
				FacesContextUtils.redirect(toURL);
			}
		} else {
			FacesContextUtils.redirect(toURL + "?" + param);
		}
	}

	private void handleValidUser(UserDTO userInfo) {
		FacesContextUtils.putSessionMap(LoginEnum.EMPLOYEE_NAME.getValue(), userInfo.getFullName());
		FacesContextUtils.putSessionMap(LoginEnum.IP.getValue(), getClientIp());

		if (!StringUtils.isBlank(redirectFrom)) {
			redirectTo(redirectFrom, "");
			redirectFrom = "";
			return;
		}
		this.clearUsernameAndPassword();
		this.redirectTo(HOME_PATH, SessionUtils.getParamString());
	}

	private void handleInvalidUser() {
		if (IpStorage.isFrozenNow(this.loginDTO.getUsername())) {
			handleFrozen();
			return;
		}

		this.putErrorMessageToView(ERROR_MESSAGE_INVALID_USER);
		this.clearUsernameAndPassword();
		this.redirectTo(LOGIN, SessionUtils.getParamString());
	}

	private String getClientIp() {
		return IpUtils.getClientIPAddress(SessionUtils.getRequest());
	}

	private String getClientSessionId() {
		return SessionUtils.getSessionId();
	}

	public void keepSessionAlive() {
		// this is a function to keep session alive called by login page
		return;
	}

	public void autoLogin() {
		Optional<UserDTO> userInfo = loginService.autoLogin(getClientIp(), getClientSessionId());

		userInfo.ifPresent(this::handleValidUser);
	}

	public void validateUsernamePassword() {
		boolean isTimeout = IpStorage.getIpTimeoutMap().contains(getClientIp());
		if (isTimeout)
			IpStorage.getIpTimeoutMap().remove(getClientIp());

		if (this.isUsernameOrPasswordEmpty()) {
			this.clearUsernameAndPassword();
			return;
		}

		if (IpStorage.isFrozenNow(this.loginDTO.getUsername())) {
			handleFrozen();
			return;
		}
		
		Optional<UserDTO> userInfo;
		userInfo = loginService.ldapLogin(getClientIp(), getClientSessionId(), getLoginDTO());
		
		if (userInfo.isPresent()) {
			handleValidUser(userInfo.get());
		} else {
			handleInvalidUser();
		}
	}

	private void handleFrozen() {
		this.putErrorMessageToView(ERROR_MESSAGE_FROZEN);
		this.redirectTo(LOGIN, SessionUtils.getParamString());
	}

	public void logOut() {
		loginService.logoutByUser(getClientIp());
		FacesContextUtils.redirect(LOGIN);
	}

}
