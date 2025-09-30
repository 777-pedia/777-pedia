import random
from datetime import datetime, timedelta

# --- 설정값 ---
DAILY_KEY_PREFIX = "movie_scores:daily:"
WEEKLY_KEY_PREFIX = "movie_scores:weekly:"
OUTPUT_FILENAME = "/data/movie-rankings.redis"
MAX_MOVIE_ID = 100
SCORE_PER_DAY = 5000
DAYS_TO_GENERATE = 14
TTL_IN_SECONDS = 30 * 24 * 60 * 60
# --- 설정값 끝 ---

def get_weekly_key_format(date_obj):
    year, week, _ = date_obj.isocalendar()
    return f"{WEEKLY_KEY_PREFIX}{year}_W{week:02d}"

def generate_commands():
    end_date = datetime.now().date()
    start_date = end_date - timedelta(days=DAYS_TO_GENERATE)
    print(f"영화 랭킹 데이터 생성: {start_date} ~ {end_date}")

    daily_scores = {}
    weekly_scores = {}

    current_date = start_date
    while current_date <= end_date:
        daily_key = f"{DAILY_KEY_PREFIX}{current_date.strftime('%Y%m%d')}"
        weekly_key = get_weekly_key_format(current_date)
        daily_scores.setdefault(daily_key, {})
        weekly_scores.setdefault(weekly_key, {})

        for _ in range(SCORE_PER_DAY):
            movie_id = str(random.randint(1, MAX_MOVIE_ID))
            score = random.choice([0.5, 1.0, 1.5])
            daily_scores[daily_key][movie_id] = daily_scores[daily_key].get(movie_id, 0) + score
            weekly_scores[weekly_key][movie_id] = weekly_scores[weekly_key].get(movie_id, 0) + score
        current_date += timedelta(days=1)

    with open(OUTPUT_FILENAME, "w", encoding="utf-8") as f:
        all_scores = {**daily_scores, **weekly_scores} # 두 딕셔너리 병합
        for key, scores in all_scores.items():
            for item, score in scores.items():
                f.write(f'ZADD "{key}" {score} "{item}"\n')
            f.write(f'EXPIRE "{key}" {TTL_IN_SECONDS}\n')

    print(f"성공! '{OUTPUT_FILENAME}' 파일이 생성되었습니다.")

if __name__ == "__main__":
    generate_commands()