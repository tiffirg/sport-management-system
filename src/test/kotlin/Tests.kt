import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.data.*
import ru.emkn.kotlin.sms.services.ArgumentsHandler
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Test1 {

    @ExperimentalCli
    @Test
    fun testParserProtocolStart() {
        val args = arrayOf("SportRegion", "20.03.2021", "protocolStart", "path1", "path2", "path3")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandStart(
                    pathsRequests = listOf("path1", "path2", "path3")
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsAthlete1() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsAthlete", "path")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResultsAthlete(
                    pathProtocolStart = null,
                    pathProtocolCheckpoint = "path"
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsAthlete2() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsAthlete")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResultsAthlete(
                    pathProtocolStart = null,
                    pathProtocolCheckpoint = null
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsAthlete3() {
        val args1 =
            arrayOf("SportRegion", "20.03.2021", "resultsAthlete", "path", "-ps", "pathPS")
        val args2 = arrayOf(
            "SportRegion",
            "20.03.2021",
            "resultsAthlete",
            "path",
            "--protocolStart",
            "pathPS"
        )
        val result = Arguments(
            title = "SportRegion",
            date = Arguments.checkDate("20.03.2021"),
            command = CommandResultsAthlete(
                pathProtocolStart = "pathPS",
                pathProtocolCheckpoint = "path"
            )
        )
        assertEquals(
            result,
            ArgumentsHandler.apply(args1)
        )
        assertEquals(
            result,
            ArgumentsHandler.apply(args2)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsTeam1() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsTeam")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResults(
                    pathResultsAthlete = null
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsTeam2() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsTeam", "path")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResults(
                    pathResultsAthlete = "path"
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }
}