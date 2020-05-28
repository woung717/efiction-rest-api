package com.springshiit.efiction.post;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springshiit.efiction.user.UserVO;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/post")
public class PostController {
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private PostService postService;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<PostMeta> list(@AuthenticationPrincipal UserVO user) {
		List<PostVO> posts = postRepository.findByAuthor(user.getUserId());
		
		return posts.stream().map(p->new PostMeta(p.getId(), p.getSubject())).collect(Collectors.toList());
	}
	
	@RequestMapping(value = "/{postId}", method = RequestMethod.GET)
	public PostVO getPost(@PathVariable("postId") Integer postId, @AuthenticationPrincipal UserVO user) {
		PostVO post = postRepository.findByAuthorAndId(user.getUserId(), postId);
		
		return post != null ? post : new PostVO();
	}
	
	@RequestMapping(value = "/{postId}", method = RequestMethod.DELETE)
	public Result deletePost(@PathVariable("postId") Integer postId, @AuthenticationPrincipal UserVO user) {
		return postService.deletePost(user.getUserId(), postId) ? new Result("success") : new Result("fail");
	}
	
	@RequestMapping(value = "/commit", method = RequestMethod.POST)
	public PostVO commitPost(@RequestBody PostVO post, @AuthenticationPrincipal UserVO user) {
		PostVO existPost = postRepository.findByAuthorAndId(user.getUserId(), post.getId()); 
		
		if (existPost == null) {
			existPost = new PostVO();
			
			existPost.setAuthor(user.getUserId());
			existPost.setCreatedAt(new Date());
		}

		existPost.setSubject(post.getSubject());
		existPost.setContents(post.getContents());
		existPost.setLastEditedAt(new Date());
		
		postRepository.save(existPost);
		
		return existPost;
	}
	
	@Data
	@AllArgsConstructor
	class PostMeta {
		Integer id;
		String subject;
	}
	
	@Data
	@AllArgsConstructor
	class Result { String result; }
}
