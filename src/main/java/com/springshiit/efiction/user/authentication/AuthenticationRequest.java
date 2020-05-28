package com.springshiit.efiction.user.authentication;

import lombok.Data;

@Data
public class AuthenticationRequest {
	private String userId;
    private String userPassword;
}
