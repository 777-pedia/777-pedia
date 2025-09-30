package org.example.pedia_777.domain.like.lock.distributed;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.dto.response.LikedReviewResponse;
import org.example.pedia_777.domain.like.entity.Like;
import org.example.pedia_777.domain.like.error.LikeErrorCode;
import org.example.pedia_777.domain.like.repository.LikeRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberService;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeProvider {

    private final LikeRepository likeRepository;
    private final MemberService memberServiceApi;
    private final ReviewService reviewServiceApi;

    @Transactional
    public void save(Like like) {
        likeRepository.save(like);
    }

    @Transactional
    public LikeResponse addLike(Long memberId, Long reviewId) {

        if (likeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
            throw new BusinessException(LikeErrorCode.LIKE_ALREADY_EXISTS);
        }

        Member currentMember = memberServiceApi.getMemberById(memberId);
        Review currentReview = reviewServiceApi.getReviewById(reviewId);

        likeRepository.save(Like.of(currentMember, currentReview));

        currentReview.incrementLikeCount();

        return LikeResponse.of(reviewId, currentReview.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse cancelLike(Long memberId, Long reviewId) {

        Like foundLike = likeRepository.findByMemberIdAndReviewId(memberId, reviewId)
                .orElseThrow(() -> new BusinessException(LikeErrorCode.LIKE_NOT_FOUND));

        Review currentReview = reviewServiceApi.getReviewById(reviewId);

        likeRepository.delete(foundLike);

        currentReview.decrementLikeCount();

        return LikeResponse.of(reviewId, currentReview.getLikeCount(), false);
    }

    @Transactional(readOnly = true)
    public PageResponse<LikedReviewResponse> getLikedReviews(Long memberId, int page, int size) {

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Like> likedReviewPage = likeRepository.findByMemberId(memberId, pageable);

        Page<LikedReviewResponse> response = likedReviewPage.map(LikedReviewResponse::from);

        return PageResponse.from(response);
    }

    public Like getLike(Long memberId, Long reviewId) {
        return likeRepository.findByMemberIdAndReviewId(memberId, reviewId)
                .orElseThrow(() -> new BusinessException(LikeErrorCode.LIKE_NOT_FOUND));
    }
}
