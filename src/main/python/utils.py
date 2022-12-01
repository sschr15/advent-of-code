import os


def get_challenge(year: int, day: int, separator: str = "\n") -> list[str]:
    """Returns the challenge input as a list of strings"""
    path = f'inputs/{year}/day{day}'
    if not os.path.exists(path):
        path = f'inputs/{year}/day{day}.txt'
    if not os.path.exists(path):
        raise FileNotFoundError(f'Input file for {day} Dec {year} not found')

    with open(path, 'r') as f:
        return f.read().split(separator)
