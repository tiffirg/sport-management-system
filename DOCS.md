# Консольная система для проведения спортивных соревновнований


# Commands

| FullName                 | ShortName          | Описание                                                             |
| ----------               | ---------          | --------------------------                                           |
| --help                   | -h                 | Аптечка                                                              |
| title                    | title              | Название соревнования                                                |
| date                     | date               | Дата соревнования                                                    |
| protocolStart            | protocolStart      | Команда: Получить протоколы старта                                   |
| resultsAthlete           | resultsGroup       | Команда: Получить результаты для групп                               |
| --protocolStart          | -ps                | Параметр к resultsGroup: путь к протоколу старта                     |
| --checkpointAthlete      | -cpa               | Параметр к resultsGroup: информация чекпоинтов в виде "по участнику" (`default = false`) |
| resultsTeam              | resultsTeam        | Команда: Получить результаты команд                                  |


# Run & Test Application

## Examples of input .csv

### Request list
```csv
СПбГУ,,,,
Ж12,Рощина,Надежда,2003,1р
М10,Санников,Вадим,2003,3р
М10,Ананикян,Александр,2002,2р
```

### Protocol checkpoint
`--checkpointAthlete = false`


```csv
32,
11,12:02:58
21,12:02:58
22,12:04:53
46,
11,12:04:16
22,12:05:13
21,12:06:28
33,
22,12:07:41
11,12:07:56
21,12:09:00
47,
11,12:09:11
48,
11,12:12:32
52,
11,12:14:08
51,
11,12:18:41
50,
11,12:20:11
49,
11,12:22:00
53,
22,12:10:11
21,12:11:01
11,12:23:31
34,
22,12:06:41
21,12:07:39
```

### Protocol checkpoint
`--checkpointAthlete = true`


```csv
To be continued
```


## Examples of command results .csv
### Result command `protocolStart: Start protocol
```csv
Ж12,,,,,,
11,Рощина,Надежда,2003,1р,СПбГУ,12:00:00
М10,,,,,,
21,Санников,Вадим,2003,3р,СПбГУ,12:01:00
22,Ананикян,Александр,2002,2р,СПбГУ,12:02:00
```

### Result command `resultsGroup: Results of Group
```csv
Ж12,,,,,,,,,
1,11,Рощина,Надежда,2003,1р,СПбГУ,00:23:31,1,
М10,,,,,,,,,
1,22,Санников,Вадим,2003,3р,СПбГУ,00:08:11,1,
2,21,Ананикян,Александр,2002,2р,СПбГУ,00:10:01,2,
```

### Result command `resultsTeam: Results of Team
```csv
СПбГУ,277,,,,,,
11,Рощина,Надежда,2003,1р,Ж12,1,100
22,Санников,Вадим,2003,3р,М10,1,100
21,Ананикян,Александр,2002,2р,М10,2,77
```

## Run

### Получить протоколы старта

```
competition <path config> protocolStart pathRequestList1, pathRequestList2...
```

### Получить результаты соревнования для каждого участника

Для ввода можно использовать альтернативный способ представления данных о чекпоинтах, с помощью параметра: `--checkpointAthlete` или `-cpa`

```
1. competition <pathconfig> resultsGroup path1
```

```
2. competition <path config> resultsGroup <InputStream>
```

```
3. competition <path config> resultsGroup path --protocolStart path
```

```
4. competition <path config> resultsGroup <InputStream> --protocolStart path
```



### Получить результаты команд

```
1. competition <path config> resultsTeam
```

```
2. competition <path config> resultsTeam path
```


## Test

Скрипт `testsParser.kt` - проверка парсера

Скрипт `testsCommands.kt` - проверка команд
