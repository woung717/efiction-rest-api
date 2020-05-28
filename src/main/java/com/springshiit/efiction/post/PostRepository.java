package com.springshiit.efiction.post;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<PostVO, Integer> {
	public List<PostVO> findByAuthor(String author);
	public PostVO findByAuthorAndId(String author, Integer id);
	public void deleteByAuthorAndId(String author, Integer id);
}
