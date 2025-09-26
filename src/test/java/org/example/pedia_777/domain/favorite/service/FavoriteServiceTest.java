package org.example.pedia_777.domain.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteAddResponse;
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

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

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
    private Long memberId = 1L;
    private Long movieId = 1L;

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
                LocalDateTime.now(),
                120L,
                "한국",
                "영화 줄거리",
                "http://test.url"
        );
    }


    @Test
    @DisplayName("영화 찜 추가 시 성공적으로 추가된다.")
    void addFavoriteSuccess() {

        // given
        given(memberServiceApi.findMemberById(memberId)).willReturn(testMember);
        given(movieServiceApi.findMovieById(movieId)).willReturn(testMovie);
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(Optional.empty());

        // when
        FavoriteAddResponse response = favoriteService.addHeart(memberId, movieId);

        // then
        assertThat(response.movieId()).isEqualTo(movieId);
        assertThat(response.title()).isEqualTo(testMovie.getTitle());
        assertThat(response.isHeart()).isTrue();

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("영화 찜 추가 시 이미 찜한 경우 예외 처리가 발생한다.")
    void addFavoriteFail_AlreadyExists() {

        // given
        given(memberServiceApi.findMemberById(memberId)).willReturn(testMember);
        given(movieServiceApi.findMovieById(movieId)).willReturn(testMovie);
        given(favoriteRepository.findByMemberIdAndMovieId(memberId, movieId)).willReturn(
                Optional.of(Favorite.create(testMember, testMovie)));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.addHeart(memberId, movieId);
        });

        assertEquals(ErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

}
