# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName          | Описание                                   |
| ----------               | ---------          | --------------------------                 |
| --help                   | -h                 | Аптечка                                    |
| type                     | type               | Вид соревнования                           |
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
competition <type competition> <title> <date> protocolStart path1, path2...
```

### Получить результаты соревнования для каждого участника

```
1. competition <type competition> <title> <date> resultsAthlete path1, path2...
```

```
2. competition <type competition> <title> <date> resultsAthlete <InputStream>
```

```
3. competition <type competition> <title> <date> resultsAthlete path1, path2... --protocolsStart path1 path2...
```

```
4. competition <type competition> <title> <date> resultsAthlete <InputStream> --protocolsStart path1 path2...
```


### Получить результаты команд

```
1. competition <type competition> <title> <date> resultsTeam
```

```
2. competition <type competition> <title> <date> resultsTeam path1, path2...
```


## Test

Запустить скрипт `Tests.kt`
