package org.example.pedia_777.common.config;

import java.io.IOException;

import org.example.pedia_777.common.code.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter implements Filter {
	private final JwtUtil jwtUtil;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;

		String bearerJwt = httpRequest.getHeader("Authorization");

		if (bearerJwt == null) {
			sendError(httpResponse, ErrorCode.NOT_FOUND_TOKEN);
			return;
		}

		String jwt = jwtUtil.substringToken(bearerJwt);

		try {
			Claims claims = jwtUtil.extractClaims(jwt);
			if (claims == null) {
				sendError(httpResponse, ErrorCode.INVALID_JWT);
				return;
			}

			httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
			httpRequest.setAttribute("email", claims.get("email"));
			httpRequest.setAttribute("nickname", claims.get("nickname"));

			chain.doFilter(request, response);
		} catch (SecurityException | MalformedJwtException e) {
			sendError(httpResponse, ErrorCode.INVALID_JWT);
		} catch (ExpiredJwtException e) {
			sendError(httpResponse, ErrorCode.EXPIRED_JWT);
		} catch (UnsupportedJwtException e) {
			sendError(httpResponse, ErrorCode.UNSUPPORTED_JWT);
		} catch (Exception e) {
			sendError(httpResponse, ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}

	private void sendError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.getWriter().write(errorCode.getMessage());
	}
}
