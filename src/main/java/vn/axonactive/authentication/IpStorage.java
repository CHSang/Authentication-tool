package vn.axonactive.authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;
import vn.axonactive.authentication.login.LoginService;
import vn.axonactive.authentication.user.UserDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)  
public class IpStorage {  //NOSONAR

	@Getter
	private static Map<String, UserDTO> ipUserMap = new HashMap<>();

	private static Map<String, Timer> ipTimerMap = new HashMap<>();

	@Getter
	private static Map<String, Integer> userCounterMap = new HashMap<>();

	private static Map<String, Timer> userFrozenMap = new HashMap<>();

	@Getter
	private static List<String> ipTimeoutMap = new ArrayList<>();

	@Getter
	private static Map<String, List<String>> ipApiCallBacks = new HashMap<>();

	public static final long IP_TIMEOUT_IN_MIN = Long.parseLong(ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.timeout.in.min"));
	public static final long USER_FROZEN_IN_MIN = Long.parseLong(ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.frozen.in.min"));
	public static final long USER_FROZEN_MAX_COUNTER = Long.parseLong(ConfigPropertiesUtils
			.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "authentication.max.login.counter"));

	public static void startIpDestroyer(LoginService loginService, String ip) {
		IpStorage.stopIpDestroyer(ip);
		Timer newTimer = new Timer(true);
		IpDestroyerTask task = new IpDestroyerTask(loginService, ip);
		newTimer.schedule(task, IP_TIMEOUT_IN_MIN * 60 * 1000);
		ipTimerMap.put(ip, newTimer);
	}

	public static boolean restartIpDestroyer(LoginService loginService, String ip) {
		if (ipTimerMap.containsKey(ip)) {
			IpStorage.stopIpDestroyer(ip);
			IpStorage.startIpDestroyer(loginService, ip);
			return true;
		}

		return false;
	}

	public static void stopIpDestroyer(String ip) {
		Timer timerGetFromMap = ipTimerMap.remove(ip);

		if (timerGetFromMap != null) {
			timerGetFromMap.cancel();
		}
	}

	public static Optional<List<String>> getCallBacks(String ip) {
		if (!ipApiCallBacks.containsKey(ip)) {
			return Optional.empty();
		}

		return Optional.of(new ArrayList<>(ipApiCallBacks.get(ip)));
	}

	public static void startFrozenDestroyer(String userName) {
		IpStorage.stopFrozenDestroyer(userName);
		Timer newTimer = new Timer(true);
		FrozenDestroyerTask task = new FrozenDestroyerTask(userName);
		newTimer.schedule(task, USER_FROZEN_IN_MIN * 60 * 1000);
		userFrozenMap.put(userName, newTimer);
	}

	public static void stopFrozenDestroyer(String userName) {
		Timer timerGetFromMap = userFrozenMap.remove(userName);

		if (timerGetFromMap != null) {
			timerGetFromMap.cancel();
		}

		userCounterMap.put(userName, 0);
	}

	public static boolean addCallBack(String ip, String apiCallBack) {
		if (!ipUserMap.containsKey(ip)) {
			return false;
		}

		if (ipApiCallBacks.containsKey(ip)) {
			List<String> temporaryList = new ArrayList<>();
			temporaryList.addAll(ipApiCallBacks.get(ip));
			temporaryList.add(apiCallBack);
			ipApiCallBacks.replace(ip, temporaryList);
			return true;
		}

		ipApiCallBacks.put(ip, Arrays.asList(apiCallBack));
		return true;
	}

	public static List<String> removeCallBacks(String ip) {
		return ipApiCallBacks.remove(ip);
	}

	public static boolean isFrozenNow(String username) {
		return userFrozenMap.containsKey(username);
	}

	public static void doCounterIncrease(String username) {
		int currentCounter = userCounterMap.get(username) == null ? 1 : userCounterMap.get(username) + 1;
		if (currentCounter < USER_FROZEN_MAX_COUNTER) {
			userCounterMap.put(username, currentCounter);
		} else {
			startFrozenDestroyer(username);
		}
	}

}
