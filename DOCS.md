# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName          | Описание                                   |
| ----------               | ---------          | --------------------------                 |
| --help                   | -h                 | Аптечка                                    |
| title                    | title              | Название соревнования                      |
| date                     | date               | Дата соревнования                          |
| protocolStart            | protocolStart      | Команда: Получить протоколы старта         |
| resultsAthlete           | resultsGroup       | Команда: Получить результаты для групп     |
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
1. competition <title> <date> resultsGroup path1
```

```
2. competition <title> <date> resultsGroup <InputStream>
```

```
3. competition <title> <date> resultsGroup path  --protocolsStart path
```

```
4. competition <title> <date> resultsGroup <InputStream> --protocolsStart path
```


### Получить результаты команд

```
1. competition <title> <date> resultsTeam
```

```
2. competition <title> <date> resultsTeam path
```


## Test

Запустить скрипт `Tests.kt`
