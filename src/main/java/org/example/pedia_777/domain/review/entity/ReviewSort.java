package org.example.pedia_777.domain.review.entity;

import org.springframework.data.domain.Sort;

public enum ReviewSort {

    LIKES {
        @Override
        public Sort getSort() {
            return Sort.by(
                    Sort.Order.desc("likeCount"),
                    Sort.Order.desc("createdAt")
            );
        }
    },
    NEWEST {
        @Override
        public Sort getSort() {
            return Sort.by("createdAt").descending();
        }
    },
    OLDEST {
        @Override
        public Sort getSort() {
            return Sort.by("createdAt").ascending();
        }
    };

    public abstract Sort getSort();
}