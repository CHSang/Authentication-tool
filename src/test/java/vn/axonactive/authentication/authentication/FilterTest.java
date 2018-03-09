package vn.axonactive.authentication.authentication;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import vn.axonactive.authentication.domain.utils.IpUtils;
import vn.axonactive.authentication.login.LoginService;
import vn.axonactive.authentication.user.UserDTO;

@RunWith(PowerMockRunner.class)
public class FilterTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private FilterConfig filterConfig;

	@Mock(name = "loginService")
	LoginService loginService;

	@InjectMocks
	LoginFilter loginFilter = new LoginFilter();

	UserDTO validUserDTO;

	@Before
	public void init() {
		Set<String> sessionSet = new HashSet<String>();
		sessionSet.add("fakeSession");

		validUserDTO = UserDTO.builder().fullName("Ho Quang Khai").position("Java Developer").userName("hqkhai")
				.sessionSet(sessionSet).build();
	}

	@Ignore
	@Test
	public void doFilter_Should_RunThroughChainDoFilter_When_UserExistAndSessionSetContainsClientSessionId()
			throws IOException, ServletException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HttpSession mockSession = Mockito.mock(HttpSession.class);
		Method method = IpUtils.class.getMethod("getClientIPAddress", HttpServletRequest.class);

		Optional<UserDTO> optionalUserDTO = Optional.of(validUserDTO);
		Mockito.when(method.invoke(null, request)).thenReturn("192.168.111.111");
		Mockito.when(request.getSession()).thenReturn(mockSession);
		Mockito.when(request.getSession().getId()).thenReturn("fakeSession");
		Mockito.when(loginService.getUser("192.168.111.111")).thenReturn(optionalUserDTO);

		loginFilter.init(filterConfig);
		loginFilter.doFilter(request, response, filterChain);
		loginFilter.destroy();

		Mockito.verify(filterChain).doFilter(request, response);
	}

	@Ignore
	@Test
	public void doFilter_Should_RunThroughResponseSendRedirect_When_AjaxCallingReturnFalse()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException, ServletException {
		HttpSession mockSession = Mockito.mock(HttpSession.class);
		Method method = IpUtils.class.getMethod("getClientIPAddress", HttpServletRequest.class);

		Optional<UserDTO> optionalUserDTO = Optional.of(validUserDTO);
		Mockito.when(method.invoke(null, request)).thenReturn("192.168.111.111");
		Mockito.when(request.getSession()).thenReturn(mockSession);
		Mockito.when(request.getSession().getId()).thenReturn("session");
		Mockito.when(loginService.getUser("192.168.111.111")).thenReturn(optionalUserDTO);
		Mockito.when(request.getHeader("X-Requested-With")).thenReturn("fake-XMLHttpRequest");
		Mockito.when(request.getContextPath()).thenReturn("http://localhost:8080");

		loginFilter.init(filterConfig);
		loginFilter.doFilter(request, response, filterChain);
		loginFilter.destroy();

		Mockito.verify(response).sendRedirect("http://localhost:8080/login");
	}

	@Ignore
	@Test
	public void doFilter_Should_RunThroughResponseSendErrorCode999_When_AjaxCallingReturnTrue()
			throws IOException, ServletException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HttpSession mockSession = Mockito.mock(HttpSession.class);
		Method method = IpUtils.class.getMethod("getClientIPAddress", HttpServletRequest.class);

		Optional<UserDTO> optionalUserDTO = Optional.of(validUserDTO);
		Mockito.when(method.invoke(null, request)).thenReturn("192.168.111.111");
		Mockito.when(request.getSession()).thenReturn(mockSession);
		Mockito.when(request.getSession().getId()).thenReturn("session");
		Mockito.when(loginService.getUser("192.168.111.111")).thenReturn(optionalUserDTO);
		Mockito.when(request.getHeader("X-Requested-With")).thenReturn("XMLHttpRequest");

		loginFilter.init(filterConfig);
		loginFilter.doFilter(request, response, filterChain);
		loginFilter.destroy();

		Mockito.verify(response).sendError(999, "Session Time Out");
	}

}
