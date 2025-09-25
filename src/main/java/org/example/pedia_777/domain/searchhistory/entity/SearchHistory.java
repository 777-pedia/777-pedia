package org.example.pedia_777.domain.searchhistory.entity;

import java.time.LocalDateTime;

import org.example.pedia_777.common.entity.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SearchHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String keyword;
	private LocalDateTime searchdAt;
}
