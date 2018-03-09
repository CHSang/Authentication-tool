package vn.axonactive.authentication.domain.utils;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * Class use for implement often use function which many class call
 * 
 * @author pknguyen
 *
 */
public final class IpUtils {
	
	private IpUtils() {

	}

	/**
	 * Get Local Address
	 * 
	 * @return fixed format IP Address
	 */
	public static String getClientIPAddress(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");
		if (xForwardedForHeader == null) {
			return request.getRemoteAddr();
		} else {
			return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
		}
	}

	/**
	 * Convert IP to URL fixed that can use as parameter on URL Example:
	 * 192.168.73.133 -> 192d168d73d133
	 * 
	 * @param ip
	 *            that need to convert
	 * @return IP have URL fixed format
	 */
	public static String toIpUrlFixedFormat(String ip) {
		return ip.replace(".", "d");
	}
}
