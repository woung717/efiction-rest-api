package com.springexample.efiction.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserVO loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserVO user = retrieveUser(userId);
		
		if (user == null) throw new UsernameNotFoundException(userId);
		
		return user;
	}

	public UserVO retrieveUser(String userId) {
		UserVO user = userRepository.findByUserId(userId);
		
		return user;
	}
	
	@Transactional
	public UserVO createUser(UserVO user) {
		String encodedPassword = new BCryptPasswordEncoder().encode(user.getUserPassword());
		
		user.setUserPassword(encodedPassword);
		
		userRepository.save(user);
		
		return user;
	}
	
	@Transactional
	public Boolean deleteUser(String userId, String userPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserVO user = userRepository.findByUserId(userId);
		
		if (user == null || passwordEncoder.matches(userPassword, user.getPassword()) == false)
			return false;
		
		userRepository.deleteByUserId(user.getUserId());
		
		return true;
	}
}
