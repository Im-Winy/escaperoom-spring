package com.escaperoom.escaperoom.constant.filter;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Forbidden;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escaperoom.escaperoom.constant.SecurityConstant;
import com.escaperoom.escaperoom.entity.HttpResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint{
	
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {
		
		HttpResponse httpResponse = new HttpResponse(
				
				HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(), SecurityConstant.FORBIDDEN_MESSAGE
				);
		
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setStatus(FORBIDDEN.value());
		
		OutputStream outputStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
		
	}

}
