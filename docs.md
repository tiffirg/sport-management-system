# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName | Описание                                 |
| ----------               | --------- | --------------------------               |
| --help                   | -h        | Аптечка                                  |
| --protocolStart          | -ps       | Команда: Получить протоколы старта       |
| --protocolResults        | -pr       | Команда: Получить результаты             |
| --protocolResultsTeam    | -prt      | Команда: Получить результаты команд      |
| --title                  | -tl       | Название соревнования                    |
| --date                   | -dt       | Дата соревнования                        |
| --dataRequests           | -dr       | Пути к заявкам                           |
| --dataProtocolStart      | -dps      | Пути к прокотолам старта                 |
| --dataProtocolCheckpoint | -dpс      | Пути к прокотолам прохождения чекпоинтов |
| --dataProtocolFinish     | -dpf      | Пути к прокотолам финиша                 |

# Run & Test Application

## Run

### 

### Получить протоколы старта
```
main.kt -tl Title -dt 12.12.2012 -ps -dr path1 path2...
```

### Получить результаты соревнования для каждого участника
```
1. main.kt -tl Title -dt 12.12.2012 -pr -dps path1 path2... -dpс path1 path2...
```

```
2. main.kt -tl Title -dt 12.12.2012 -pr -dps path_directory -dpс path_directory
```

```
3. main.kt -tl Title -dt 12.12.2012 -pr -dpс path1 path2...
```

```
4. main.kt -tl Title -dt 12.12.2012 -pr -dpс path_directory
```



### Получить результаты команд
```
1. main.kt -tl Title -dt 12.12.2012 -prt -dpf path1 path2...
```

```
2. main.kt -tl Title -dt 12.12.2012 -prt -dpf path_directory
```

```
3. main.kt -tl Title -dt 12.12.2012 -prt
```

## Test

Запустить скрипт `Tests.kt`
