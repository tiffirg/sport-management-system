package ru.emkn.kotlin.sms.data
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sksamuel.hoplite.ConfigLoader
import ru.emkn.kotlin.sms.GROUP_NAMES
import java.io.File
import java.time.LocalDateTime



//const val separator: Char = ','
val const_null = null


const val command_stop: String = "-st"

/*fun main() {
    // Тест первой функции
    val teamList:MutableList<Team> = mutableListOf()
    var team: Team



    var pathToTheRequestListFile = readLine()

    while (pathToTheRequestListFile != command_stop) {
        if(pathToTheRequestListFile != null) {
            team = request(pathToTheRequestListFile)
            teamList.add(team)
            pathToTheRequestListFile = readLine()
        }
    }
    println(teamList)


    // Тест второй функции
    val protocolStartList: MutableList<MutableList<ProtocolStart>> = mutableListOf()
    var protocolStart: MutableList<ProtocolStart>
    protocolStart = protocolStart(readLine()!!)
    protocolStartList.add(protocolStart)

    protocolStart = protocolStart(readLine()!!)
    protocolStartList.add(protocolStart)
    println(protocolStartList)


    // Тест третьей функции
    val protocolPassingDistanceList: MutableList<MutableList<ProtocolPassingDistance>> = mutableListOf()
    var protocolPassingDistance: MutableList<ProtocolPassingDistance> = mutableListOf()

    protocolPassingDistance = protocolPassingDistance(readLine()!!)
    protocolPassingDistanceList.add(protocolPassingDistance)
    protocolPassingDistance = protocolPassingDistance(readLine()!!)
    protocolPassingDistanceList.add(protocolPassingDistance)
    println(protocolPassingDistanceList)




    // Тест четвертой функции
    val protocolCheckpointPassingList: MutableList<MutableList<protocolCheckpointPassing>> = mutableListOf()
    var protocolCheckpointPassing: MutableList<protocolCheckpointPassing> = mutableListOf()
    protocolCheckpointPassing = protocolCheckpoints(readLine()!!)
    protocolCheckpointPassingList.add(protocolCheckpointPassing)
    protocolCheckpointPassing = protocolCheckpoints(readLine()!!)
    protocolCheckpointPassingList.add(protocolCheckpointPassing)

    println(protocolCheckpointPassingList)


}*/

fun request(Path: String): Team {
    val requestFile = File(Path)
    val requestList: List<List<String>> = csvReader().readAll(requestFile)    // Для метода .remove()
    val teamName = requestList.first().first()  // teamName может быть пустым

    requestList.drop(1)

    var participant: Athlete
    val participantList: MutableList<Athlete> = mutableListOf()
    // Characteristic of participant:
    var surname: String
    var name: String
    var birthYear: Int
    var sports_category: String                 // in ru: спортивный разряд
    var preferred_age_group: String
    var athleteNumber = const_null
    var startTime = const_null
    // in future – for a validation check:
    var group: Group
    var rank: Rank
    requestList.forEach {
        // requestList[i] = [surname, name, birthYear, sports_category, startTime
        surname = it.component1()               // May cause exception: surname empty -> exception
        name = it.component2()                  // May cause exception: name empty -> exception
        birthYear = it.component3().toInt()     // May cause exception: birthYear empty or isn't Int -> exception
        sports_category = it.component4()       // May cause exception: sports_category isn't in config -> exception
        preferred_age_group = it.component5()             // May cause exception: preferred_age_group isn't in config -> exception
        athleteNumber = const_null              // ??
        startTime = const_null                  // ??
        // Validation check. May cause IllegalArgumentException
        group = Group(preferred_age_group)
        rank = Rank(sports_category)

        // Characteristics are valid -> participant is valid
        participant = Athlete(surname, name, birthYear, group, rank, athleteNumber, startTime, teamName)
        participantList.add(participant)
    }

    return Team(teamName, participantList)
}



data class ProtocolStart(val group: Group,
                         val athleteNumber: Int?, val surname: String, val name: String, val birthYear: Int,
                         val rank: Rank, val startTime: String)


fun protocolStart(Path: String): MutableList<ProtocolStart>{
    val protocolStartFile = File(Path)
    val protocolStartList: List<List<String>> = csvReader().readAll(protocolStartFile)      // Для метода .remove()
    val preferred_age_group = protocolStartList.first().first()

    val group = Group(preferred_age_group)
    protocolStartList.drop(1)

    val startOfParticularGroupList: MutableList<ProtocolStart> = mutableListOf()
    var startOfParticularGroup: ProtocolStart
    // Characteristic of startOfParticularGroup:
    var athleteNumber: Int
    var surname: String
    var name: String
    var birthYear: Int
    var sports_category: String
    var timeStart: String
    // in future – for a validation check:
    var rank: Rank
    protocolStartList.forEach{

        athleteNumber = it.component1().toInt()
        surname = it.component2()
        name = it.component3()
        birthYear = it.component4().toInt()
        sports_category = it.component5()
        timeStart = it.last()
        // Validation check. May cause IllegalArgumentException
        rank = Rank(sports_category)

        startOfParticularGroup = ProtocolStart(group, athleteNumber, surname, name, birthYear, rank, timeStart)
        startOfParticularGroupList.add(startOfParticularGroup)
    }
    return startOfParticularGroupList
}





data class ProtocolPassingDistance(val athleteNumber: Int?,
                                   val distance: String, val timeCheckpoint: String)


fun protocolPassingDistance(Path: String): MutableList<ProtocolPassingDistance>{
    val protocolPassingDistanceFile = File(Path)
    val protocolPassingDistanceList: MutableList<List<String>> = csvReader().readAll(protocolPassingDistanceFile) as MutableList<List<String>>     // Для метода .remove()
    val athleteNumber = protocolPassingDistanceList.first().first().toInt()
    protocolPassingDistanceList.drop(1)

    val check_points_of_particular_athlete: MutableList<ProtocolPassingDistance> = mutableListOf()
    var passing_distance_data: ProtocolPassingDistance

    var distanceChekpoint: String
    var timeChekpoint: String
    protocolPassingDistanceList.forEach{
        distanceChekpoint = it.component1()
        timeChekpoint = it.component2()
        passing_distance_data = ProtocolPassingDistance(athleteNumber, distanceChekpoint, timeChekpoint)
        check_points_of_particular_athlete.add(passing_distance_data)
    }

    return check_points_of_particular_athlete
}




data class protocolCheckpointPassing(val chekpoint: String, val athleteNumber: Int?, val timeOfCheckpoint: String)

fun protocolCheckpoints(Path: String): MutableList<protocolCheckpointPassing>{
    val protocolCheckpointsFile = File(Path)
    val protocolCheckpointsList: List<List<String>> = csvReader().readAll(protocolCheckpointsFile)     // Для метода .remove()
    val checkpoint = protocolCheckpointsList.first().first()
    protocolCheckpointsList.drop(1)
    val athletes_for_particular_checkpoint: MutableList<protocolCheckpointPassing> = mutableListOf()
    var checkpoint_data: protocolCheckpointPassing

    var athleteNumber: Int
    var timeOfCheckpoint: String
    protocolCheckpointsList.forEach{
        athleteNumber = it.component1().toInt()
        timeOfCheckpoint = it.component2()
        checkpoint_data = protocolCheckpointPassing(checkpoint, athleteNumber, timeOfCheckpoint)
        athletes_for_particular_checkpoint.add(checkpoint_data)
    }

    return athletes_for_particular_checkpoint
}
