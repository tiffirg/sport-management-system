import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.services.CsvHandler
import java.io.File
import java.time.LocalTime
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

const val PATH_CONFIG = "src/test/resources/config.yaml"
const val PATH_APPLICATION1 = "src/test/resources/application1.csv"
const val PATH_APPLICATION2 = "src/test/resources/application2.csv"
const val PATH_PROTOCOL_START1 = "src/test/resources/protocolStart1.csv"
const val PATH_PROTOCOL_START1_2 = "src/test/resources/protocolStart1_2.csv"
const val PATH_DATA_CHECKPOINT = "src/test/resources/dataCheckpoint.csv"
const val PATH_RESULTS_GROUP1 = "src/test/resources/resultsGroup1.csv"
const val PATH_RESULTS_GROUP1_2 = "src/test/resources/resultsGroup1_2.csv"
const val PATH_SPLIT_RESULTS_GROUP1 = "src/test/resources/splitResults1.csv"
const val PATH_SPLIT_RESULTS_GROUP1_2 = "src/test/resources/splitResults1_2.csv"
const val PATH_RESULTS_TEAM1 = "src/test/resources/resultsTeam1.csv"
const val PATH_RESULTS_TEAM1_2 = "src/test/resources/resultsTeam1_2.csv"

@ExperimentalCli
internal class TestsCommands {

   @AfterTest
    fun afterTest() {
        val dir = File("src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING")
        if (dir.exists()) {
            dir.deleteRecursively()
        }
    }

    @Test
    fun testCommandProtocolStart1() {
        val args = arrayOf(PATH_CONFIG, "protocolStart", PATH_APPLICATION1)
        main(args)
        val pathProtocolStart = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/ps_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val dataSampleProtocolStart = CsvHandler.parseProtocolStart(PATH_PROTOCOL_START1)
        val sampleAthletesGroups = dataSampleProtocolStart.groupBy { athlete -> athlete.group }

        val dataProtocolStart = CsvHandler.parseProtocolStart(pathProtocolStart)
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

    @Test
    fun testCommandProtocolStart2() {
        val args = arrayOf(PATH_CONFIG, "protocolStart", PATH_APPLICATION1, PATH_APPLICATION2)
        main(args)
        val pathProtocolStart = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/ps_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val dataSampleProtocolStart = CsvHandler.parseProtocolStart(PATH_PROTOCOL_START1_2)
        val sampleAthletesGroups = dataSampleProtocolStart.groupBy { athlete -> athlete.group }

        val dataProtocolStart = CsvHandler.parseProtocolStart(pathProtocolStart)
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

    @Test
    fun testCommandResultsGroup1() {
        val args = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "-ps", PATH_PROTOCOL_START1)
        main(args)
        val dataSampleResultsGroup = CsvHandler.parseResultsGroup(PATH_RESULTS_GROUP1)
        val pathResultGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rg_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val pathSplitResultGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rs_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val data = csvReader().readAll(File(pathSplitResultGroup))
        val dataSample = csvReader().readAll(File(PATH_SPLIT_RESULTS_GROUP1))
        assert(data == dataSample)
        val dataResultsGroup = CsvHandler.parseResultsGroup(pathResultGroup)
        for (resultsGroup in dataResultsGroup) {
            val sampleResults = dataSampleResultsGroup.find { it.group.groupName == resultsGroup.group.groupName }?.results
            assert(!sampleResults.isNullOrEmpty())
            assertEquals(sampleResults!!.map { it.listForResultsGroup.drop(2) }, resultsGroup.results.map { it.listForResultsGroup.drop(2) })
            val numbers = mutableSetOf<Char>()

            for (result in resultsGroup.results) {
                numbers.add(result.competitor.athleteNumber.toString()[0])
            }
            assertEquals(1, numbers.size)
        }

    }

    @Test
    fun testCommandResultsGroup2() {
        val args1 = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "-ps", PATH_PROTOCOL_START1)
        val args2 = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "--protocolStart", PATH_PROTOCOL_START1)
        val pathResultsGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rg_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        main(args1)
        val data1 = CsvHandler.parseResultsGroup(pathResultsGroup)
        main(args2)
        val data2 = CsvHandler.parseResultsGroup(pathResultsGroup)
        assertEquals(data1.size, data2.size)
        for (i in data1.indices) {
            val result1 = data1[i].results
            val result2 = data2[i].results
            assertEquals(result1.size, result1.size)
            assertEquals(result1.size, result2.size)
            assertEquals(result1.map { it.listForResultsGroup }, result2.map { it.listForResultsGroup })
        }
    }

    @Test
    fun testCommandResultsGroup3() {
        val args = arrayOf(PATH_CONFIG, "resultsGroup", PATH_DATA_CHECKPOINT, "-ps", PATH_PROTOCOL_START1_2)
        main(args)
        val dataSampleResultsGroup = CsvHandler.parseResultsGroup(PATH_RESULTS_GROUP1_2)
        val pathResultGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rg_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val pathSplitResultGroup = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rs_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val data = csvReader().readAll(File(pathSplitResultGroup))
        val dataSample = csvReader().readAll(File(PATH_SPLIT_RESULTS_GROUP1_2))
        assert(data == dataSample)
        val dataResultsGroup = CsvHandler.parseResultsGroup(pathResultGroup)
        for (resultsGroup in dataResultsGroup) {
            val sampleResults = dataSampleResultsGroup.find { it.group.groupName == resultsGroup.group.groupName }?.results
            assert(!sampleResults.isNullOrEmpty())
            assertEquals(sampleResults!!.map { it.listForResultsGroup.drop(2) }, resultsGroup.results.map { it.listForResultsGroup.drop(2) })
            val numbers = mutableSetOf<Char>()

            for (result in resultsGroup.results) {
                numbers.add(result.competitor.athleteNumber.toString()[0])
            }
            assertEquals(1, numbers.size)
        }

    }

    @Test
    fun testCommandResultsTeam1() {
        val args = arrayOf(PATH_CONFIG, "resultsTeam", PATH_RESULTS_GROUP1)
        main(args)
        val pathResultsTeam = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rt_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val data = csvReader().readAll(File(pathResultsTeam))
        val dataSample = csvReader().readAll(File(PATH_RESULTS_TEAM1))
        assert(data == dataSample)
    }

    @Test
    fun testCommandResultsTeam2() {
        val args = arrayOf(PATH_CONFIG, "resultsTeam", PATH_RESULTS_GROUP1_2)
        main(args)
        val pathResultsTeam = "src/test/resources/${EVENT_NAME}_$EVENT_DATE_STRING/rt_${EVENT_NAME}_$EVENT_DATE_STRING.csv"
        val data = csvReader().readAll(File(pathResultsTeam))
        val dataSample = csvReader().readAll(File(PATH_RESULTS_TEAM1_2))
        assert(data == dataSample)
    }
}