# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName          | Описание                                                             |
| ----------               | ---------          | --------------------------                                           |
| --help                   | -h                 | Аптечка                                                              |
| title                    | title              | Название соревнования                                                |
| date                     | date               | Дата соревнования                                                    |
| protocolStart            | protocolStart      | Команда: Получить протоколы старта                                   |
| resultsAthlete           | resultsGroup       | Команда: Получить результаты для групп                               |
| --protocolStart          | -ps                | Параметр к resultsGroup: путь к протоколу результатов                |
| --checkpointAthlete      | -cpa               | Параметр к resultsGroup: информация чекпоинтов в виде 'по участнику' |
| resultsTeam              | resultsTeam        | Команда: Получить результаты команд                                  |


# Run & Test Application

## Run

### 

### Получить протоколы старта

```
competition <path config> protocolStart path1, path2...
```

### Получить результаты соревнования для каждого участника

For each input option, you can use the command `--checkpointAthlete` or `-cpa`

```
1. competition <pathconfig> resultsGroup path1
```

```
2. competition <path config> resultsGroup <InputStream>
```

```
3. competition <path config> resultsGroup path --protocolsStart path
```

```
4. competition <path config> resultsGroup <InputStream> --protocolsStart path
```



### Получить результаты команд

```
1. competition <path config> resultsTeam
```

```
2. competition <path config> resultsTeam path
```


## Test

Запустить скрипт `Tests.kt`
