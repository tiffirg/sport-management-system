import ru.emkn.kotlin.sms.initConfig
import ru.emkn.kotlin.sms.startProtocolsGeneration
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestStartProcessing {

    @Test
    fun smallTest1() {
        initConfig("src/test/resources/config.yaml")
        val applications = listOf(MoscowTeam, VyborgTeam)
        val protocols = startProtocolsGeneration(applications)
        val athletes = protocols.flatMap { athleteGroup -> athleteGroup.athletes }
        val startNumbers = athletes.map { it.athleteNumber }

        // стартовые номера участников из одной группы должны начинаться с одинаковой цифры
        assertEquals(dmitriy.athleteNumber.toString()[0], kirill.athleteNumber.toString()[0])
        assertEquals(vasily.athleteNumber.toString()[0], mikhail.athleteNumber.toString()[0])
    }

}