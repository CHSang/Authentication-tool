package vn.axonactive.authentication.domain.utils;

import java.util.Optional;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class SessionUtils {

	/*
	 * getSession(boolean isCreate) - true: create session and return a session
	 * instance with current request. - false: return any existing session instance
	 * with current request.
	 */

	private SessionUtils() {

	}

	public static Optional<HttpSession> getSession() {
		return Optional.ofNullable(FacesContext.getCurrentInstance()).map(FacesContext::getExternalContext)
				.map(ec -> ec.getSession(false)).map(HttpSession.class::cast);
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	public static String getContextPath() {
		return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getContextPath();
	}

	public static String getParamString() {
		return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getQueryString();
	}

	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	public static String getSessionId() {
		return getSession().map(ss -> ss.getId()).orElse("");
	}

	public static boolean isSessionNull(HttpSession session) {
		return session == null;
	}

	public static boolean isObjectNull(Object obj) {
		return obj == null;
	}
}
