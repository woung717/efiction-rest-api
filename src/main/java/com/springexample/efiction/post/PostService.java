package com.springexample.efiction.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {
	@Autowired
	private PostRepository postRepository;
	
	@Transactional
	public Boolean deletePost(String userId, Integer postId) {
		if (postRepository.findByAuthorAndId(userId, postId) == null) 
			return false;
		
		postRepository.deleteByAuthorAndId(userId, postId);
		
		return true;
	}
}
