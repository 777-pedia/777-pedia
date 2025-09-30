#!/bin/sh
# redis/init/init-redis.sh

# 시작
echo "Waiting for Redis to start..."
sleep 10

# 1. Python 스크립트를 실행하여 최신 데이터 파일 생성
echo "Generating fresh dummy data..."
python /data/generate_redis_data.py

# 2. 생성된 파일을 redis-cli로 실행하여 데이터 주입
echo "Seeding data into Redis..."
redis-cli -h redis-db < /data/dummy-data.redis

echo "Redis seeding completed."