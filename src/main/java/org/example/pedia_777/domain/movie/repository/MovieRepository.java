package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.movie.entity.Movies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movies, Long> {
}
