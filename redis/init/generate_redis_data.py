import random
from datetime import datetime, timedelta

# --- 설정값 ---
KEY_PREFIX = "popularKeywords:"
OUTPUT_FILENAME = "/data/dummy-data.redis"
SEARCH_COUNT_PER_HOUR = 100
KEYWORDS = [
    "영웅", "도시", "보물", "그림자", "마법", "용사",
    "김영웅", "이영웅", "박용사", "최도시", "정천사", "강바다", "이숲", "최천사"
]
TTL_IN_SECONDS = 7 * 24 * 60 * 60
# --- 설정값 끝 ---

def generate_commands():
    start_time = datetime(2025, 9, 28, 0, 0)
    end_time = datetime.now()

    print(f"데이터 생성 시작: {start_time.strftime('%Y-%m-%d %H:%M')} 부터 {end_time.strftime('%Y-%m-%d %H:%M')} 까지")
    hourly_counts = {}

    current_time = start_time
    while current_time <= end_time:
        key = KEY_PREFIX + current_time.strftime("%Y%m%d%H")

        # ✅ 디버깅을 위해 현재 어떤 키를 생성하는지 출력
        print(f"Generating data for key: {key}")

        hourly_counts[key] = {}
        for _ in range(SEARCH_COUNT_PER_HOUR):
            keyword = random.choice(KEYWORDS)
            hourly_counts[key][keyword] = hourly_counts[key].get(keyword, 0) + 1
        current_time += timedelta(hours=1)

    with open(OUTPUT_FILENAME, "w", encoding="utf-8") as f:
        f.write("FLUSHALL\n")
        for key, counts in hourly_counts.items():
            for keyword, score in counts.items():
                f.write(f'ZADD "{key}" {score} "{keyword}"\n')
            f.write(f'EXPIRE "{key}" {TTL_IN_SECONDS}\n')

    print(f"\n성공! '{OUTPUT_FILENAME}' 파일이 생성되었습니다.")

if __name__ == "__main__":
    import os
    os.makedirs(os.path.dirname(OUTPUT_FILENAME), exist_ok=True)
    generate_commands()