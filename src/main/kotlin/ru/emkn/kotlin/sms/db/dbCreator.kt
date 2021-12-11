package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


// Примеры таблиц для протоколов
object ProtocolPassingDistance: Table() {
    val athleteNumber = integer("athleteNumber")
    val checkpointDist = varchar("checkpoint", 10)
    var checkpointTime = varchar("starTime", 9)
}

object ProtocolPassingCheckpoint: Table (){
    val checkpointDist = varchar("checkpoint", 10)
    val athleteNumber = integer("athleteNumber")
    var checkpointTime = varchar("starTime", 9)
}

object ProtocolResults: Table (){
    val group = varchar("group", 10)
    val place = integer("place")
    val athleteNumber = integer("athleteNumber")

    val surname = varchar("surname", 100)
    val name = varchar("name", 100)
    val birthYear = integer("birthYear")
    val rank = varchar("rank", 100)
    val timeFin = varchar("teamFin", 100)
}



// Дальше идут аналогичные примеры, но к ним я дописал функции записи данных в таблицы, чтобы вы могли посмотреть,
// как организуется запись в таблицы, и, возможно, сказать мне, что я что-то сделал не так или вас все устраивает.

// Как посмотреть, что происходит:
// 1. Скачайте DB BROWSER FOR SQLITE отсюда https://sqlitebrowser.org/dl/
// 2. Запустите функцию db_creator(). Она сгенерит файл
// 3. Откройте файл PRIVET_VADIM_I_NADIA.db с помощью DB BROWSER FOR SQLITE


// Пример таблицы для RequestList
object RequestList : Table() {
    val team_m = varchar("team", 100)
    val surname_m = varchar("surname", 100)
    val name_m = varchar("name", 100)
    val birthYear_m = integer("birthYear")
    val rank_m = varchar("rank", 4)
    val preferredGroup_m = varchar("preferredGroup", 4)

    override val primaryKey = PrimaryKey(surname_m, name_m, birthYear_m, name = "athlete")  // На эту строчку можете
                                                                                            // забить, она пока не важна
}

// Функция записи в таблицу. Потом -- очевидно -- засуну в объект RequestList
fun requestList(team: String, surname: String, name: String, birthYear: Int, rank: String, group: String) {
    Database.connect("jdbc:sqlite:PRIVET_VADIM_I_NADIA.db" , "org.sqlite.JDBC")
    transaction{
        SchemaUtils.create(RequestList)
        RequestList.insert {
            it[team_m] = team
            it[surname_m] = surname
            it[name_m] = name
            it[birthYear_m] = birthYear
            it[rank_m] = rank
            it[preferredGroup_m] = group
        }
    }

}


// Аналогично с RequestList
object ProtocolStart : Table() {
    val groupName_m = varchar("groupName", 100)
    var athleteNumber_m = integer("athleteNumber")
    val surname_m = varchar("surname", 100)
    val name_m = varchar("name", 100)
    val birthYear_m = integer("birthYear")
    val rank_m = varchar("rank", 4)
    var startTime_m = varchar("starTime", 9)

    override val primaryKey = PrimaryKey(startTime_m, name = "startTime") // На эту строчку можете забить, она пока не важна
}

// Аналогично...
fun protocolStart(groupName: String, athleteNumber: Int, surname: String, name: String, birthYear: Int, rank: String, startTime: String) {
    Database.connect("jdbc:sqlite:PRIVET_VADIM_I_NADIA.db" , "org.sqlite.JDBC")
    transaction{
        SchemaUtils.create(ProtocolStart)
        ProtocolStart.insert {
            it[groupName_m] = groupName
            it[athleteNumber_m] = athleteNumber
            it[surname_m] = surname
            it[name_m] = name
            it[birthYear_m] = birthYear
            it[rank_m] = rank
            it[startTime_m] = startTime
        }
    }

}

// !!!Если будете запускать второй раз, то удалите PRIVET_VADIM_I_NADIA.db

// Вопросы, на которые я хотел бы, чтобы вы ответили:
// 1.   Я могу организовать связь между столбцов между разными таблицами, например, athleteNumber
// у одного и того же спортсмена в разных таблицах один и тот же. Вопрос: надо ли нам это, если да, то что связывать?
// 2.   В правильном ли я вообще направлении двигаюсь? Вы же хотели протоколы хранить в бд?
// 3.   Я вроде могу сделать так, чтобы не было ошибки при многократном вызове db_creator(),
// чтоб не надо было каждый раз базу данных удалять. Это нам надо?


fun db_creator(){
    // Просто какие-то данные, на которых я решил потестить, как работает функция
    val team = "Выборгский СДЮШСОР №10"
    val surname = "Иванов"
    val name = "Иванов"
    val birthYear = 2002
    val rank = "КМС"
    val group = "М10"
    val athleteNumber: Int = 101
    val startTime: String = "12:01:38"


    requestList(team, surname, name, birthYear, rank, group)
    protocolStart(group, athleteNumber, surname, name, birthYear, rank, startTime)
}


// IGNORE: Example code from https://github.com/JetBrains/Exposed
/*    object Users : Table() {
    val id = varchar("id", 10) // Column<String>
    val name = varchar("name", length = 50) // Column<String>
    val cityId = (integer("city_id") references Cities.id).nullable() // Column<Int?>

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
}

object Cities : Table() {
    val id = integer("id").autoIncrement() // Column<Int>
    val name = varchar("name", 50) // Column<String>

    override val primaryKey = PrimaryKey(id, name = "PK_Cities_ID")
}


       /*Database.connect("jdbc:sqlite:mem:test.db" , "org.sqlite.JDBC")*/
        transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create (Cities, Users)

        val saintPetersburgId = Cities.insert {
            it[name] = "St. Petersburg"
        } get Cities.id

        val munichId = Cities.insert {
            it[name] = "Munich"
        } get Cities.id

        val pragueId = Cities.insert {
            it.update(name, stringLiteral("   Prague   ").trim().substring(1, 2))
        }[Cities.id]

        val pragueName = Cities.select { Cities.id eq pragueId }.single()[Cities.name]


        Users.insert {
            it[id] = "andrey"
            it[name] = "Andrey"
            it[Users.cityId] = saintPetersburgId
        }

        Users.insert {
            it[id] = "sergey"
            it[name] = "Sergey"
            it[Users.cityId] = munichId
        }

        Users.insert {
            it[id] = "eugene"
            it[name] = "Eugene"
            it[Users.cityId] = munichId
        }

        Users.insert {
            it[id] = "alex"
            it[name] = "Alex"
            it[Users.cityId] = null
        }

        Users.insert {
            it[id] = "smth"
            it[name] = "Something"
            it[Users.cityId] = null
        }

        Users.update({ Users.id eq "alex"}) {
            it[name] = "Alexey"
        }

        Users.deleteWhere{ Users.name like "%thing"}

        println("All cities:")

        for (city in Cities.selectAll()) {
            println("${city[Cities.id]}: ${city[Cities.name]}")
        }

        println("Manual join:")
        (Users innerJoin Cities).slice(Users.name, Cities.name).
        select {(Users.id.eq("andrey") or Users.name.eq("Sergey")) and
                Users.id.eq("sergey") and Users.cityId.eq(Cities.id)}.forEach {
            println("${it[Users.name]} lives in ${it[Cities.name]}")
        }

        println("Join with foreign key:")


        (Users innerJoin Cities).slice(Users.name, Users.cityId, Cities.name).
        select { Cities.name.eq("St. Petersburg") or Users.cityId.isNull()}.forEach {
            if (it[Users.cityId] != null) {
                println("${it[Users.name]} lives in ${it[Cities.name]}")
            }
            else {
                println("${it[Users.name]} lives nowhere")
            }
        }

        println("Functions and group by:")

        ((Cities innerJoin Users).slice(Cities.name, Users.id.count()).selectAll().groupBy(Cities.name)).forEach {
            val cityName = it[Cities.name]
            val userCount = it[Users.id.count()]

            if (userCount > 0) {
                println("$userCount user(s) live(s) in $cityName")
            } else {
                println("Nobody lives in $cityName")
            }
        }


    }*/