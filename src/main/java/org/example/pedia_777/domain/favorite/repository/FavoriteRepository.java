package org.example.pedia_777.domain.favorite.repository;

import java.util.Optional;
import org.example.pedia_777.domain.favorite.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByMemberIdAndMovieId(Long memberId, Long movieId);

    @EntityGraph(attributePaths = "movie")
    Page<Favorite> findAllByMemberId(Long memberId, Pageable pageable);
}
