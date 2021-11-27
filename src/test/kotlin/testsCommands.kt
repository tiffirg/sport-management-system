import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.cli.ExperimentalCli
import org.junit.Ignore
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.services.CsvHandler
import java.io.File
import java.time.LocalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

const val PATH_CONFIG = "src/test/resources/config.yaml"
const val PATH_APPLICATION = "src/test/resources/sampleApplication.csv"
const val PATH_PROTOCOL_START = "src/test/resources/sampleProtocolStart.csv"
const val PATH_DATA_CHECKPOINT = "src/test/resources/sampleDataCheckpoint.csv"
const val PATH_RESULTS_GROUP = "src/test/resources/sampleResultsGroup.csv"
const val PATH_RESULTS_TEAM = "src/test/resources/sampleResultsTeam.csv"


internal class TestsCommands {

    @Test
    fun testStartProcessing() {
        initConfig(PATH_CONFIG)

        val applications = listOf(MoscowTeam, VyborgTeam)
        val protocols = startProtocolsGeneration(applications)
        val athletes = protocols.flatMap { athleteGroup -> athleteGroup.athletes }
        val startNumbers = athletes.map { it.athleteNumber }

        // стартовые номера участников из одной группы должны начинаться с одинаковой цифры
        assertEquals(dmitriy.athleteNumber.toString()[0], kirill.athleteNumber.toString()[0])
        assertEquals(vasily.athleteNumber.toString()[0], mikhail.athleteNumber.toString()[0])
    }

    @ExperimentalCli
    @Test
    fun testCommandProtocolStart() {
        val args = arrayOf(PATH_CONFIG, "protocolStart", PATH_APPLICATION)
        main(args)
        val pathProtocolStart = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/ps_${EVENT_NAME}_$EVENT_DATE.csv"
        val dataSampleProtocolStart = CsvHandler.parseProtocolStart(PATH_PROTOCOL_START).values
        val sampleAthletesGroups = dataSampleProtocolStart.groupBy { athlete -> athlete.group }

        val dataProtocolStart = CsvHandler.parseProtocolStart(pathProtocolStart).values
        val athletesGroups = dataProtocolStart.groupBy { athlete -> athlete.group }
        for ((group, athletes) in athletesGroups) {
            val sampleAthletes = sampleAthletesGroups[group]
            val numbers = mutableSetOf<Char>()
            val times = mutableSetOf<LocalTime?>()
            for (athlete in athletes) {
                numbers.add(athlete.athleteNumber.toString()[0])
                times.add(athlete.startTime)
                assertEquals(1, sampleAthletes?.count {
                            it.surname == athlete.surname &&
                            it.name == athlete.name &&
                            it.rank.rankName == athlete.rank.rankName
                })
            }
            assertEquals(1, numbers.size)
            assertEquals(athletes.size, times.size)
        }
    }

    @ExperimentalCli
    @Test
    fun testCommandResultsGroup1() {
        val args = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT)
        main(args)
//        val pathResultsGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/rg_${EVENT_NAME}_$EVENT_DATE.csv"
        val dataSampleResultsGroup = CsvHandler.parseResultsGroup(PATH_RESULTS_GROUP)
        val dataResultsGroup = CsvHandler.parseResultsGroup(PATH_RESULTS_GROUP)
        for (resultsGroup in dataResultsGroup) {
            val sampleResults = dataSampleResultsGroup.find { it.group.groupName == resultsGroup.group.groupName }?.results
            assert(!sampleResults.isNullOrEmpty())
            assertEquals(sampleResults!!.map { it.listForResultsGroup.drop(2) }, resultsGroup.results.map { it.listForResultsGroup.drop(2) })
            val numbers = mutableSetOf<Char>()

            for (result in resultsGroup.results) {
                numbers.add(result.athleteNumber.toString()[0])
            }
            assertEquals(1, numbers.size)
        }
    }

    @ExperimentalCli
    @Test
    fun testCommandResultsGroup2() {
        val pathProtocolsStart = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/ps_${EVENT_NAME}_$EVENT_DATE.csv"
        val args1 = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT)
        val args2 = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "-ps", pathProtocolsStart)
        val args3 = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "--protocolStart", pathProtocolsStart)
        val pathResultsGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/rg_${EVENT_NAME}_$EVENT_DATE.csv"
        main(args1)
        val data1 = CsvHandler.parseResultsGroup(pathResultsGroup)
        main(args2)
        val data2 = CsvHandler.parseResultsGroup(pathResultsGroup)
        main(args3)
        val data3 = CsvHandler.parseResultsGroup(pathResultsGroup)
        assertEquals(data1.size, data2.size)
        assertEquals(data1.size, data3.size)
        for (i in data1.indices) {
            val result1 = data1[i].results
            val result2 = data2[i].results
            val result3 = data3[i].results
            assertEquals(result1.size, result2.size)
            assertEquals(result1.size, result3.size)
            assertEquals(result1.map { it.listForResultsGroup }, result2.map { it.listForResultsGroup })
            assertEquals(result1.map { it.listForResultsGroup }, result3.map { it.listForResultsGroup })
        }
    }

    @ExperimentalCli
    @Test
    @Ignore
    fun testCommandResultsTeam1() {
        val args = arrayOf(PATH_CONFIG, "resultsTeam", PATH_RESULTS_GROUP)
        main(args)
        val pathResultsTeam = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/rt_${EVENT_NAME}_$EVENT_DATE.csv"
        assertEquals(
            csvReader().readAll(File(pathResultsTeam)),
            csvReader().readAll(File(PATH_RESULTS_TEAM))
        )
    }

    @ExperimentalCli
    @Test
    @Ignore
    fun testCommandResultsTeam2() {
        val args = arrayOf(PATH_CONFIG, "resultsTeam")
        main(args)
        val pathResultsTeam = "src/test/resources/${EVENT_NAME}_$EVENT_DATE/rt_${EVENT_NAME}_$EVENT_DATE.csv"
        assertEquals(
            csvReader().readAll(File(pathResultsTeam)),
            csvReader().readAll(File(PATH_RESULTS_TEAM))
        )
    }
}