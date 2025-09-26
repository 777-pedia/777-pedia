package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
