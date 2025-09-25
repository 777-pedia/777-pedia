package org.example.pedia_777.domain.movie.entity;

import java.time.LocalDateTime;

import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Movies extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String director;
	private String actors;
	private String genres;
	private LocalDateTime releaseDate;
	private Long runtime;
	private String country;
	private String overview;
	private String posterUrl;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime updatedAt;
}
