package emp.emp.auth.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import emp.emp.auth.custom.CustomUserDetails;
import emp.emp.auth.exception.AuthErrorCode;
import emp.emp.util.api_response.Response;
import emp.emp.util.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final List<String> WHITELIST = List.of(
		"/favicon.ico",
		"/default-ui.css",
		"/login",
		"/login-success",
		"/login-failed",
		"/api/register",
		"/api/login",
		"/api/token/exchange",
		"/api/token/refresh",
		"/api/emergency/room",
		"/api/emergency/aed",
		"/api/emergency/both"
	);
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		log.info("requestURI={}", request.getRequestURI());
		if (WHITELIST.contains(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}

		if (!processTokenAuthentication(request, response)) {
			return;
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * 인증 토큰을 처리하는 메서드
	 * @param request 클라이언트 요청 객체
	 * @param response 클라이언트 응답 객체
	 * @return 인증이 정상적으로 완료되면 true, 아니면 false
	 * @throws IOException
	 */
	private boolean processTokenAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accessToken = resolveToken(request);

		if (accessToken == null) {
			sendUnauthorizedResponse(response);
			return false;
		}

		try {
			if (jwtTokenProvider.validateToken(accessToken)) {
				Claims claims = jwtTokenProvider.getClaims(accessToken);
				CustomUserDetails userDetails = CustomUserDetails.createCustomUserDetailsFromClaims(claims);
				setUserAuthentication(userDetails);
			} else {
				sendTokenRefreshResponse(response);
				return false;
			}
		} catch (Exception e) {
			sendTokenRefreshResponse(response);
			return false;
		}
		return true;
	}

	private void setUserAuthentication(CustomUserDetails userDetails) {
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		Response<Void> errorResponse = Response.errorResponse(AuthErrorCode.UNAUTHORIZED);
		response.getWriter().write(errorResponse.convertToJson());
	}

	private void sendTokenRefreshResponse(HttpServletResponse response) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		Response<Void> errorResponse = Response.errorResponse(AuthErrorCode.INVALID_ACCESS_TOKEN);
		response.getWriter().write(errorResponse.convertToJson());
	}
}
