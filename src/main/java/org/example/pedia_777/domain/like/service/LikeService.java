package org.example.pedia_777.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.like.code.LikeErrorCode;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.dto.response.LikedReviewResponse;
import org.example.pedia_777.domain.like.entity.Like;
import org.example.pedia_777.domain.like.repository.LikeRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.service.ReviewServiceApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService implements LikeServiceApi {

    private final LikeRepository likeRepository;
    private final ReviewServiceApi reviewServiceApi;
    private final MemberServiceApi memberServiceApi;

    @Transactional
    public LikeResponse addLike(Long memberId, Long reviewId) {

        //동시성 이슈 발생 가능1
        if (likeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
            throw new BusinessException(LikeErrorCode.LIKE_ALREADY_EXISTS);
        }

        Member currentMember = memberServiceApi.findMemberById(memberId);
        Review currentReview = reviewServiceApi.getReviewById(reviewId);

        likeRepository.save(Like.of(currentMember, currentReview));

        //동시성 이슈 발생 가능2
        currentReview.incrementLikeCount();

        return LikeResponse.of(reviewId, currentReview.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse cancelLike(Long memberId, Long reviewId) {

        Like foundLike = likeRepository.findByMemberIdAndReviewId(memberId, reviewId)
                .orElseThrow(() -> new BusinessException(LikeErrorCode.LIKE_NOT_FOUND));

        Review currentReview = reviewServiceApi.getReviewById(reviewId);
        likeRepository.delete(foundLike);

        //동시성 이슈 발생 가능 3
        currentReview.decrementLikeCount();

        return LikeResponse.of(reviewId, currentReview.getLikeCount(), false);
    }

    public PageResponse<LikedReviewResponse> getLikedReviews(Long memberId, int page, int size) {

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Like> likedReviewPage = likeRepository.findByMemberId(memberId, pageable);

        Page<LikedReviewResponse> response = likedReviewPage.map(LikedReviewResponse::from);

        return PageResponse.from(response);
    }
}
