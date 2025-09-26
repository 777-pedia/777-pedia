package org.example.pedia_777.domain.favorite.repository;

import java.util.Optional;
import org.example.pedia_777.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByMemberIdAndMovieId(Long memberId, Long movieId);
}
