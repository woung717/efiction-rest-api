package com.springexample.efiction.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserVO, Integer> {
	public UserVO findByUserId(String userId);
	public void deleteByUserId(String userId);
}
