# 1. 가벼운 Python 이미지를 기반으로 시작
FROM python:3.9-slim

# 2. redis-cli를 설치하기 위해 redis-tools 패키지 설치
RUN apt-get update && apt-get install -y redis-tools

# 3. 컨테이너의 작업 디렉터리 설정
WORKDIR /data

# 4. 로컬의 redis/init 폴더에 있는 모든 파일(스크립트)을 이미지 안으로 복사
COPY . .

# 5. 컨테이너가 실행될 때 init-redis.sh를 실행하도록 설정
CMD ["sh", "init-redis.sh"]