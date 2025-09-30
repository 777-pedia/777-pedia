package org.example.pedia_777.domain.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.favorite.code.FavoriteErrorCode;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteAddResponse;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteMovieResponse;
import org.example.pedia_777.domain.favorite.entity.Favorite;
import org.example.pedia_777.domain.favorite.repository.FavoriteRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    private final Long memberId = 1L;
    private final Long movieId = 1L;
    @InjectMocks
    private FavoriteService favoriteService;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private MemberServiceApi memberServiceApi;
    @Mock
    private MovieServiceApi movieServiceApi;
    // 테스트 데이터
    private Member testMember;
    private Movie testMovie;
    private Movie testMovie2;

    @BeforeEach
    void setUp() {
        testMember = Member.signUp(
                "test@test.com",
                "password",
                "testUser"
        );

        testMovie = Movie.of(
                "감독",
                "테스트 영화",
                "배우1, 배우2",
                "코미디",
                LocalDate.now(),
                120,
                "한국",
                "영화 줄거리",
                "http://test.url"
        );

        testMovie2 = Movie.of(
                "감독2",
                "테스트 영화2",
                "배우3, 배우4",
                "스릴러",
                LocalDate.now(),
                120,
                "한국",
                "영화 줄거리2",
                "http://test.url2"
        );
    }


    @Test
    @DisplayName("영화 찜 추가 시 성공적으로 추가된다.")
    void addFavoriteSuccess() {

        // given
        given(memberServiceApi.getMemberById(memberId)).willReturn(testMember);
        given(movieServiceApi.getMovieEntity(movieId)).willReturn(testMovie);
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(Optional.empty());

        // when
        FavoriteAddResponse response = favoriteService.addFavorite(memberId, movieId);

        // then
        assertThat(response.movieId()).isEqualTo(movieId);
        assertThat(response.title()).isEqualTo(testMovie.getTitle());
        assertThat(response.isFavorite()).isTrue();

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("영화 찜 추가 시 이미 찜한 경우 예외 처리가 발생한다.")
    void addFavoriteFail_AlreadyExists() {

        // given
        given(memberServiceApi.getMemberById(memberId)).willReturn(testMember);
        given(movieServiceApi.getMovieEntity(movieId)).willReturn(testMovie);
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(
                Optional.of(Favorite.create(testMember, testMovie)));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.addFavorite(memberId, movieId);
        });

        assertEquals(FavoriteErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    @DisplayName("영화 찜 해제 시 성공적으로 삭제된다.")
    void removeFavoriteSuccess() {

        // given
        Favorite favorite = Favorite.create(testMember, testMovie);
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(Optional.of(favorite));

        // when
        favoriteService.removeFavorite(memberId, movieId);

        // then
        verify(favoriteRepository).delete(favorite);
    }

    @Test
    @DisplayName("영화 찜 해제 시 찜하지 않은 영화인 경우 예외 발생한다.")
    void removeFavoriteFail_NotFound() {

        // given
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.removeFavorite(memberId, movieId);
        });

        assertEquals(FavoriteErrorCode.FAVORITE_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @DisplayName("찜한 영화 목록 조회가 성공적으로 수행된다.")
    void getMyFavoritesSuccess() {

        // given
        Favorite favorite1 = Favorite.create(testMember, testMovie);
        Favorite favorite2 = Favorite.create(testMember, testMovie2);
        Pageable pageable = PageRequest.of(0, 10);

        given(favoriteRepository.findByMemberId(memberId, pageable))
                .willReturn(new PageImpl<>(List.of(favorite1, favorite2), pageable, 2));

        // when
        PageResponse<FavoriteMovieResponse> myFavorites = favoriteService.getMyFavorites(memberId, pageable);

        // then
        assertThat(myFavorites.content()).hasSize(2);
        assertThat(myFavorites.content()).extracting(FavoriteMovieResponse::title)
                .containsExactly("테스트 영화", "테스트 영화2");
        verify(favoriteRepository).findByMemberId(memberId, pageable);

    }
}
