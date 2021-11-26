import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.classes.Arguments
import ru.emkn.kotlin.sms.classes.CommandResults
import ru.emkn.kotlin.sms.classes.CommandResultsGroup
import ru.emkn.kotlin.sms.classes.CommandStart
import ru.emkn.kotlin.sms.services.ArgumentsHandler
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Test1 {

    @ExperimentalCli
    @Test
    fun testParserProtocolStart() {
        val args = arrayOf("/config.yaml", "protocolStart", "path1", "path2", "path3")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
                command = CommandStart(
                    pathsRequests = listOf("path1", "path2", "path3")
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsGroup1() {
        val args = arrayOf("/config.yaml", "resultsGroup", "path")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
                command = CommandResultsGroup(
                    pathProtocolStart = null,
                    pathProtocolCheckpoint = "path",
                    false
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsGroup2() {
        val args = arrayOf("/config.yaml", "resultsGroup")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
                command = CommandResultsGroup(
                    pathProtocolStart = null,
                    pathProtocolCheckpoint = null,
                    false
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsGroup4() {
        val args = arrayOf("/config.yaml", "resultsGroup", "-cp")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
                command = CommandResultsGroup(
                    pathProtocolStart = null,
                    pathProtocolCheckpoint = null,
                    true
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }

    @ExperimentalCli
    @Test
    fun testParserResultsGroup3() {
        val args1 =
            arrayOf("/config.yaml", "resultsGroup", "path", "-ps", "pathPS")
        val args2 = arrayOf(
            "/config.yaml",
            "resultsGroup",
            "path",
            "--protocolStart",
            "pathPS"
        )
        val result = Arguments(
            pathConfig = "/config.yaml",
            command = CommandResultsGroup(
                pathProtocolStart = "pathPS",
                pathProtocolCheckpoint = "path",
                false
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
        val args = arrayOf("/config.yaml", "resultsTeam")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
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
        val args = arrayOf("/config.yaml", "resultsTeam", "path")
        assertEquals(
            Arguments(
                pathConfig = "/config.yaml",
                command = CommandResults(
                    pathResultsAthlete = "path"
                )
            ),
            ArgumentsHandler.apply(args)
        )
    }
}