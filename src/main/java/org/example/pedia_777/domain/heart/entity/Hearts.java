package org.example.pedia_777.domain.heart.entity;

import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.movie.entity.Movies;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Hearts extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Members members;

	@ManyToOne(fetch = FetchType.LAZY)
	private Movies movies;
}
