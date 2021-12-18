import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.services.Distances
import ru.emkn.kotlin.sms.services.GeneralDatabase
import ru.emkn.kotlin.sms.services.Groups
import ru.emkn.kotlin.sms.services.Teams

class CompetitionDatabase : GeneralDatabase() {
    fun processConfig() {
        transaction {
            SchemaUtils.create(Groups)
            SchemaUtils.create(Teams)
            SchemaUtils.create(Distances)
        }
    }
}