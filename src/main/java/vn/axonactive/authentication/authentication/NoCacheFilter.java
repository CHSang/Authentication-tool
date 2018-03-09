package vn.axonactive.authentication.authentication;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
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


@WebFilter(urlPatterns="/*",dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class NoCacheFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		return;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!request.getRequestURI().startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
			response.setHeader("Cache-Control", "no-cache, must-revalidate"); 
			response.setHeader("Pragma", "none");
			response.setDateHeader("Expires", 0);
		}
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		return;
	}

}
