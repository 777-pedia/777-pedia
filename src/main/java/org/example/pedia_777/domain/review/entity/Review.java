package org.example.pedia_777.domain.review.entity;

import java.time.LocalDateTime;

import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.movie.entity.Movies;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Review extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String comment;
	private double star;
	private Long likeCount;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	private Movies movies;

	@ManyToOne(fetch = FetchType.LAZY)
	private Members members;
}
