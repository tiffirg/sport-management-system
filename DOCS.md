# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName          | Описание                                   |
| ----------               | ---------          | --------------------------                 |
| --help                   | -h                 | Аптечка                                    |
| title                    | title              | Название соревнования                      |
| date                     | date               | Дата соревнования                          |
| protocolStart            | protocolStart      | Команда: Получить протоколы старта         |
| resultsAthlete           | resultsAthlete     | Команда: Получить результаты для участника |
| resultsTeam              | resultsTeam        | Команда: Получить результаты команд        |


# Run & Test Application

## Run

### 

### Получить протоколы старта

```
competition <title> <date> protocolStart path1, path2...
```

### Получить результаты соревнования для каждого участника

```
1. competition <title> <date> resultsAthlete path1, path2...
```

```
2. competition <title> <date> resultsAthlete <InputStream>
```

```
3. competition <title> <date> resultsAthlete path1, path2... --protocolsStart path1 path2...
```

```
4. competition <title> <date> resultsAthlete <InputStream> --protocolsStart path1 path2...
```


### Получить результаты команд

```
1. competition <title> <date> resultsTeam
```

```
2. competition <title> <date> resultsTeam path1, path2...
```


## Test

Запустить скрипт `Tests.kt`
