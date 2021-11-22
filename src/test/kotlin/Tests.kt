import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.data.*
import ru.emkn.kotlin.sms.data.TypeCommand.*
import ru.emkn.kotlin.sms.services.ArgumentsHandler
import ru.emkn.kotlin.sms.utils.transformDate
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
                    pathsRequests = listOf(Path(path = "path1"), Path(path = "path2"), Path(path = "path3")),
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsAthlete1() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsAthlete", "path1", "path2", "path3")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResultsAthlete(
                    pathsProtocolsStart = listOf(),
                    pathsProtocolsCheckpoint = listOf(Path(path = "path1"), Path(path = "path2"), Path(path = "path3")),
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
                    pathsProtocolsStart = listOf(),
                    pathsProtocolsCheckpoint = listOf(),
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsAthlete3() {
        val args1 =
            arrayOf("SportRegion", "20.03.2021", "resultsAthlete", "path1", "path2", "path3", "-ps", "pathPS1 pathPS2")
        val args2 = arrayOf(
            "SportRegion",
            "20.03.2021",
            "resultsAthlete",
            "path1",
            "path2",
            "path3",
            "--protocolsStart",
            "pathPS1 pathPS2"
        )
        val result = Arguments(
            title = "SportRegion",
            date = Arguments.checkDate("20.03.2021"),
            command = CommandResultsAthlete(
                pathsProtocolsStart = listOf(Path(path = "pathPS1"), Path(path = "pathPS2")),
                pathsProtocolsCheckpoint = listOf(Path(path = "path1"), Path(path = "path2"), Path(path = "path3")),
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
                    pathsResults = listOf()
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsTeam2() {
        val args = arrayOf("SportRegion", "20.03.2021", "resultsTeam", "path1", "path2")
        assertEquals(
            Arguments(
                title = "SportRegion",
                date = Arguments.checkDate("20.03.2021"),
                command = CommandResults(
                    pathsResults = listOf(Path(path = "path1"), Path(path = "path2")),
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }
}