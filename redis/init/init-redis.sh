#!/bin/sh

echo "Waiting for Redis to start..."
sleep 10

# 1. Python 스크립트 실행
echo "Generating dummy data..."
python /data/generate_popular_keywords.py
python /data/generate_movie_rankings.py

# ✅ 2. 데이터 주입 전, DB를 한 번만 초기화
echo "Flushing all data from Redis..."
redis-cli -h redis-db FLUSHALL

# 3. 생성된 파일들을 순서대로 주입
echo "Seeding data into Redis..."
redis-cli -h redis-db < /data/popular-keywords.redis
redis-cli -h redis-db < /data/movie-rankings.redis

echo "Redis seeding completed."