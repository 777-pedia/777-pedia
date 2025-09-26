package org.example.pedia_777.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.favorite.code.FavoriteErrorCode;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteAddResponse;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteItemResponse;
import org.example.pedia_777.domain.favorite.entity.Favorite;
import org.example.pedia_777.domain.favorite.repository.FavoriteRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieServiceApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MovieServiceApi movieServiceApi;
    private final MemberServiceApi memberServiceApi;

    @Transactional
    public FavoriteAddResponse addFavorite(Long memberId, Long movieId) {

        Member member = memberServiceApi.findMemberById(memberId);
        Movie movie = movieServiceApi.findMovieById((movieId));

        if (favoriteRepository.findByMemberIdAndMovieId(memberId, movieId).isPresent()) {
            throw new BusinessException(FavoriteErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite favorite = Favorite.create(member, movie);
        favoriteRepository.save(favorite);

        return FavoriteAddResponse.of(movieId, movie.getTitle(), true);
    }

    @Transactional
    public void removeFavorite(Long memberId, Long movieId) {

        Favorite favorite = favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)
                .orElseThrow(() -> new BusinessException(FavoriteErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);
    }

    public PageResponse<FavoriteItemResponse> getMyFavorites(Long memberId, Pageable pageable) {

        Page<Favorite> favoritePage = favoriteRepository.findByMemberId(memberId, pageable);

        Page<FavoriteItemResponse> response = favoritePage.map(FavoriteItemResponse::from);

        return PageResponse.from(response);
    }
}
