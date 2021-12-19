import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.services.TDistances
import ru.emkn.kotlin.sms.services.GeneralDatabase
import ru.emkn.kotlin.sms.services.TGroups
import ru.emkn.kotlin.sms.services.TTeams

class CompetitionDatabase : GeneralDatabase() {
    fun processConfig() {
        transaction {
            SchemaUtils.create(TGroups)
            SchemaUtils.create(TTeams)
            SchemaUtils.create(TDistances)
        }
    }
}