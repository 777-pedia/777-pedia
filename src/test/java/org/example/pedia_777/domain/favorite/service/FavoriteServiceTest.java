package org.example.pedia_777.domain.favorite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.example.pedia_777.common.dto.PageResponse;

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

        assertEquals(FavoriteErrorCode.FAVORITE_ALREADY_EXISTS, exception.getErrorCode());
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    @DisplayName("회원은 페이징을 사용해 찜한 영화 목록을 조회할 수 있다.")
    void getFavoriteMoviesSuccess() {

        // given
        Favorite favorite = Favorite.create(testMember, testMovie);
        LocalDateTime now = LocalDateTime.now();

        ReflectionTestUtils.setField(testMovie, "id", movieId);
        ReflectionTestUtils.setField(favorite, "createdAt", now);

        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<Favorite> favorites = new PageImpl<>(List.of(favorite), pageRequest, 1);

        given(favoriteRepository.findAllByMemberId(eq(memberId), any(Pageable.class))).willReturn(favorites);

        // when
        PageResponse<FavoriteMovieResponse> result = favoriteService.getFavoriteMovies(memberId, pageRequest);

        // then
        assertThat(result.content()).hasSize(1);
        FavoriteMovieResponse movieResponse = result.content().get(0);
        assertThat(movieResponse.movieId()).isEqualTo(movieId);
        assertThat(movieResponse.title()).isEqualTo(testMovie.getTitle());
        assertThat(movieResponse.director()).isEqualTo(testMovie.getDirector());
        assertThat(movieResponse.genres()).isEqualTo(testMovie.getGenres());
        assertThat(movieResponse.favoriteAddedAt()).isEqualTo(now);
        assertThat(result.totalElements()).isEqualTo(1);
    }

}
