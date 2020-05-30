package com.springexample.efiction.user.authentication;

import java.util.Collection;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthenticationToken {
	private String userId;
    private Collection authorities;
    private String token;
}
