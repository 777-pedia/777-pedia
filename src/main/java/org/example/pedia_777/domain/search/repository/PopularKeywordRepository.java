package org.example.pedia_777.domain.search.repository;

import org.example.pedia_777.domain.search.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, Long> {
}