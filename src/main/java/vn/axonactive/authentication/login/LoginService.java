package vn.axonactive.authentication.login;

import java.util.List;
import java.util.Optional;

import javax.ejb.Singleton;

import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ApiUtils;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.sso.LDAPService;
import vn.axonactive.authentication.sso.SSOService;
import vn.axonactive.authentication.user.UserDTO;
import vn.axonactive.authentication.user.UserService;

@Singleton
public class LoginService {
	private static final String API_TOKEN = "api.token";
	private String apiToken = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(),
			API_TOKEN);

	private LDAPService ldapService = new LDAPService();
	private SSOService ssoService = new SSOService();
	private UserService userService = new UserService();

	private boolean validateWithLDAP(LoginDTO loginInfo) {
		return ldapService.authenticate(loginInfo.getUsername(), loginInfo.getPassword());
	}

	private Optional<UserDTO> login(String ip, String username, String sessionId) {

		this.doRestartTheCounter(username);

		this.doRemoveIpTimeoutMap(ip);

		return Optional.of(this.doGetUserDTOFromStorage(ip, sessionId)
				.orElse(this.doGetUserFromSSO(ip, username, sessionId).orElse(new UserDTO())));
	}

	private Optional<UserDTO> doGetUserFromSSO(String ip, String username, String sessionId) {
		Optional<UserDTO> userInfo = userService.getUserInfo(username);

		userInfo.ifPresent(info -> {
			info.getSessionSet().add(sessionId);
			IpStorage.getIpUserMap().put(ip, info);
			ssoService.addSession(ip, username, sessionId, apiToken);
			IpStorage.startIpDestroyer(this, ip);
		});

		return userInfo;
	}

	private Optional<UserDTO> doGetUserDTOFromStorage(String ip, String sessionId) {

		Optional<UserDTO> userInfoFromStorage = Optional.ofNullable(IpStorage.getIpUserMap().get(ip));

		userInfoFromStorage.ifPresent(s -> {
			s.getSessionSet().add(sessionId);
			IpStorage.getIpUserMap().put(ip, s);
		});

		return userInfoFromStorage;
	}

	private void doRemoveIpTimeoutMap(String ip) {
		if (IpStorage.getIpTimeoutMap().contains(ip)) {
			IpStorage.getIpTimeoutMap().remove(ip);
		}
	}

	private void doRestartTheCounter(String username) {
		IpStorage.getUserCounterMap().put(username, 0);
	}

	/**
	 * Get User Info by IP. In case SSOService not logi, remove userInfo in server
	 * 
	 * @param ip
	 * @return
	 */
	public Optional<UserDTO> getUser(String ip) {
		Optional<String> username = ssoService.getUserNameLoggedInByThisIp(ip, apiToken);

		if (!username.isPresent()) {
			IpStorage.getIpUserMap().remove(ip);
			return Optional.empty();
		}

		return Optional.ofNullable(IpStorage.getIpUserMap().get(ip));
	}

	public Optional<UserDTO> ldapLogin(String ip, String sessionId, LoginDTO loginInfo) {
		if (!validateWithLDAP(loginInfo)) {
			IpStorage.doCounterIncrease(loginInfo.getUsername());
			return Optional.empty();
		}

		return this.login(ip, loginInfo.getUsername(), sessionId);
	}

	public Optional<UserDTO> autoLogin(String ip, String sessionId) {
		Optional<String> username = ssoService.getUserNameLoggedInByThisIp(ip, apiToken);

		if (!username.isPresent()) {
			return Optional.empty();
		}

		return this.login(ip, username.get(), sessionId);
	}

	public void logoutByUser(String ip) {
		this.doRemoveIpTimeoutMap(ip);
		this.logout(ip);
	}

	public void logout(String ip) {
		IpStorage.getIpUserMap().remove(ip);
		IpStorage.stopIpDestroyer(ip);

		Optional<String> username = ssoService.getUserNameLoggedInByThisIp(ip, apiToken);

		if (!username.isPresent()) {
			return;
		}

		ssoService.logout(username.get(), apiToken);

		Optional<List<String>> callbacks = IpStorage.getCallBacks(ip);

		if (callbacks.isPresent()) {
			for (String callback : callbacks.get()) {
				ApiUtils.callGetAsync(callback);
			}

			IpStorage.removeCallBacks(ip);
		}
	}

	public void logoutbySession(String ip) {
		IpStorage.getIpTimeoutMap().add(ip);

		this.logout(ip);
	}

	public boolean restartIpDestroyer(String ip) {
		return IpStorage.restartIpDestroyer(this, ip);
	}
}
