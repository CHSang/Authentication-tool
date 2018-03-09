package vn.axonactive.authentication.authentication;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vn.axonactive.authentication.IpStorage;
import vn.axonactive.authentication.domain.utils.IpUtils;
import vn.axonactive.authentication.login.LoginEnum;
import vn.axonactive.authentication.login.LoginService;
import vn.axonactive.authentication.user.UserDTO;

/*
 * This class restrict user access to current page in views folder without logged in
 * It automatically redirect to login page if user not logged in and turn back to previous URL if user logged in success.
 * If there is not any previous page. It will redirect, by default, to home.
 */

@WebFilter(urlPatterns = "/views/*", dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD })
public class LoginFilter implements Filter {

	private static final String SESSION_ERROR_QUERY = "?errormessage=";
	private static final String SESSION_ERROR_MESAGE = "Your session has expired. Please login again.";

	@Inject
	private LoginService loginService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		return;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String clientIp = IpUtils.getClientIPAddress(request);
		String clientSessionId = request.getSession().getId();
		boolean isAjaxCalling = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
		if (IpStorage.getIpTimeoutMap().contains(clientIp)) {
			// return code 999 to client, specific use for session Time out
			if (isAjaxCalling) {
				handleSessionTimeoutForAjaxCall(response);
				return;
			} else {
				String url = request.getContextPath() + LoginEnum.LOGIN_PATH.getValue() + SESSION_ERROR_QUERY
						+ SESSION_ERROR_MESAGE;
				response.sendRedirect(url);
				return;
			}

		}

		Optional<UserDTO> userInfo = loginService.getUser(clientIp);

		if (userInfo.map(UserDTO::getSessionSet).map(set -> set.contains(clientSessionId)).orElse(false)) {
			if(isAjaxCalling) {
				response.sendError(888, "auto login");
				return;
			}
			loginService.restartIpDestroyer(clientIp);
			chain.doFilter(req, res);
			return;
		}

		if (!isAjaxCalling) {
			String url = request.getContextPath() + LoginEnum.LOGIN_PATH.getValue();
			response.sendRedirect(url);
		}else {
			handleSessionTimeoutForAjaxCall(response);
			return;
		}
	}

	@Override
	public void destroy() {
		return;
	}
	public void handleSessionTimeoutForAjaxCall(HttpServletResponse res) throws IOException {
		res.sendError(999, "session timeout");
	}

}
