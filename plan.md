# План разработки консольной системы для проведения спортивных соревновнований

### 0. Либы и конфиг

#### Libraries:
* 0.1. [`cli`](https://github.com/Kotlin/kotlinx-cli)
* 0.2. [`logging`](https://github.com/MicroUtils/kotlin-logging)
* 0.3. [`csv`](https://github.com/doyaaaaaken/kotlin-csv)
* 0.4. [`config`](https://github.com/sksamuel/hoplite)

#### Config:
* 0.1. typeCompetition = run|ski|swim|cycle|orient
* 0.2. Данные групп.
* ###### 0.3. Данные разрядов.

### 1. Exit Codes

### Exit codes

| Название            | Exit code | Описание                   |
| ----------          | --------- | -------------------------- |
| SUCCESS             | 0         | Операция прошла успешно    |
| HELP                | 1         | Неправильный ввод          |
| INVALID_DATE        | 2         | Некорректная дата          |
| READ_ERROR          | 3         | Ошибка считывания файла    |
| INCORRECT_DATA      | 4         | Некорректные данные        |
| INCORRECT_CHECKPOINT | 5        | Некорректные чекпоинт     |

### 2. Примеры данных

#### Request list:

```csv
Выборгский СДЮШСОР №10,,,,,,,
Иванов,Иван,2002,КМС,М21,,,
Петров,Пётр,1978,I,М40,,,  
Пупкин,Василий,2011,3ю,М10,,
```

#### Protocol Start:

```csv
М10,,,,,,
241,Пупкин,Василий,2011,3ю,12:01:00,
242,Пирогов,Григорий,2011,3ю,12:02:00
243,Смирнов,Сергей,2012,,12:03:00
```

#### Protocol finish:

```csv
243,,
1km,12:06:15
2km,12:10:36
Finish,12:14:51
```

#### Protocol checkpoint:
```csv
1km,,
241,12:04:17
242,12:05:11
243,12:06:15
```

#### Results

```csv
М10,,,,,,,
1,242,Пирогов,Григорий,2011,3ю,00:12:51,
2,243,Смирнов,Сергей,2012,,00:12:57,
3,241,Пупкин,Василий,2011,3ю,00:13:15
```

#### 3. Хранение данных
* В каталоге, где лежит конфигурационные данные (подкаталог config), сохраняются данные соревнования.
* Данные соревнования - подкаталог вида - `typeCompetition.title.date`.

### 4. Parser: ArgumentsParser

| FullName                 | ShortName | Описание                                 |
| ----------               | --------- | --------------------------               |
| --help                   | -h        | Аптечка                                  |
| --protocolStart          | -ps       | Команда: Получить протоколы старта       |
| --protocolResults        | -pr       | Команда: Получить результаты             |
| --protocolResultsTeam    | -prt      | Команда: Получить результаты команд      |
| --title                  | -tl       | Название соревнования                    |
| --date                   | -dt       | Дата соревнования                        |
| --dataRequests           | -dr      | Пути к заявкам                           |
| --dataProtocolStart      | -dps      | Пути к прокотолам старта                 |
| --dataProtocolCheckpoint | -dpс      | Пути к прокотолам прохождения чекпоинтов |
| --dataProtocolFinish     | -dpf      | Пути к прокотолам финиша                 |

* 4.1. Для вызова команд требуется `title` и `date` соревнования.

### 5. main.kt

* 5.1. Парсинг аргументов.
* 5.2. Вызов метода `run` у объекта `Competition`.
* 5.3. Ловля эксепшенов с выводом кодов.

### 6. Object App
* 6.1. Метод `run(arguments)` - запуск приложения.
* 6.2. Проверка наличия указанных файлов.
* 6.3. Вывод каждого режима сохраняется с одним именем, поэтому если в аргументах нет этих файлов, то пробуем проверить их существование в месте сохранения, иначе - HELP

### 7. Class HandlerCompetition(title: String, date: Date)
* 7.1. Метод `processRequests(requests)` обрабатывает заявки
* 7.2. Метод `processProtocols(requests)` обрабатывает протоколы
* 7.3. Метод `processResults(requests)` обрабатывает результыт по каждому участнику
* 7.4. Cоздание соревнования с теми же `title` и `date` - удаление прошлого соревнования.

### 8. Enum classes Group, Rank
* 8.1. Данные групп по каждому `typeCompetition` берутся с конфигурационного файла

### 9. Data class Team(name, list<Athlete>)

### 10. class? Time

### 11. Data class Athlete
(surname, name, birthYear, rank: Rank, group: Group, 
team: String, timeStart: Time, finishTime: Time, result: ?)

### 12. Data class GroupAthletes
(group: Group, athlete: list<Athlete>)

### 13. Toss (Жеребьевка)
* 12.1. Попробовать расширить GroupAthletes.athletes функцией с параметром жеребьевки

