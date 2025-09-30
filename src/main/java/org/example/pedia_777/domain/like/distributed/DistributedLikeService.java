package org.example.pedia_777.domain.like.distributed;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.dto.response.LikedReviewResponse;
import org.example.pedia_777.domain.like.error.LikeErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedLikeService {
    private final DistributedLockManager lockManager;
    private final LikeProvider likeProvider;

    public LikeResponse addLike(Long memberId, Long reviewId) {
        String lockKey = "lock:like:review:" + reviewId;

        try {
            return lockManager.executeWithLock(lockKey, () ->

                    likeProvider.addLike(memberId, reviewId)
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(LikeErrorCode.LIKE_REQUEST_CONFLICT);
        } catch (IllegalStateException e) {
            throw new BusinessException(LikeErrorCode.LIKE_REQUEST_CONFLICT);
        }
    }

    public LikeResponse cancelLike(Long memberId, Long reviewId) {
        String lockKey = "lock:like:review:" + reviewId;

        try {
            return lockManager.executeWithLock(lockKey, () ->

                    likeProvider.cancelLike(memberId, reviewId));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(LikeErrorCode.LIKE_REQUEST_CONFLICT);
        } catch (IllegalStateException e) {
            throw new BusinessException(LikeErrorCode.LIKE_REQUEST_CONFLICT);
        }
    }

    public PageResponse<LikedReviewResponse> getLikedReviews(Long memberId, int page, int size) {
        return likeProvider.getLikedReviews(memberId, page, size);
    }
}
