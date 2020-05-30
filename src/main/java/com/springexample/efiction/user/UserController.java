package com.springexample.efiction.user;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springexample.efiction.user.authentication.AuthenticationRequest;
import com.springexample.efiction.user.authentication.AuthenticationToken;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
		String userId = authenticationRequest.getUserId();
		String userPassword = authenticationRequest.getUserPassword();

		UsernamePasswordAuthenticationToken token = getAuthenticationToken(userId, userPassword);
		
		try {
			Authentication authentication = processAuthentication(token);
			setAuthenticatedSession(authentication, session);
		} catch(AuthenticationException e) {
			return new AuthenticationToken(null, null, null);
		}
		
		UserVO user = userService.retrieveUser(userId);

		return new AuthenticationToken(user.getUserId(), user.getAuthorities(), session.getId());
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public AuthenticationToken signup(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
		String userId = authenticationRequest.getUserId();
		String userPassword = authenticationRequest.getUserPassword();
		
		if(userService.retrieveUser(userId) != null) {
			return new AuthenticationToken(userId, null, null);
		}
		
		UserVO user = userService.createUser(new UserVO(userId, userPassword, new Date()));
		
		UsernamePasswordAuthenticationToken token = getAuthenticationToken(userId, userPassword);
		Authentication authentication = processAuthentication(token);
		setAuthenticatedSession(authentication, session);
		
		return new AuthenticationToken(user.getUserId(), user.getAuthorities(), session.getId());
	}
	
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST)
	public Result withdraw(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
		String userId = authenticationRequest.getUserId();
		String userPassword = authenticationRequest.getUserPassword();
		
		session.invalidate();
		
		return userService.deleteUser(userId, userPassword) ? new Result("success") : new Result("failed");
	}

	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public Result logout(HttpSession session) {
		session.invalidate();
		
		return new Result("success");
	}
	
	private UsernamePasswordAuthenticationToken getAuthenticationToken(String userId, String userPassword) {
		return new UsernamePasswordAuthenticationToken(userId, userPassword);
	}

	private Authentication processAuthentication(UsernamePasswordAuthenticationToken token)
			throws AuthenticationException {
		return authenticationManager.authenticate(token);
	}

	private void setAuthenticatedSession(Authentication authentication, HttpSession session) {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());
	}
	
	@Data
	@AllArgsConstructor
	class Result { String result; }
}