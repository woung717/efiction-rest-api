package com.springshiit.efiction.post;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class PostVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String author;
	
	@Column(nullable = false, length=1000)
	private String subject;
	
	@Column(length=100000)
	private String contents;
	
	@Temporal(TemporalType.DATE)
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastEditedAt;
	
	private final Integer showDays = 14;
	
	private final Integer purgeDays = 30;
}
