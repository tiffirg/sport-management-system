import ru.emkn.kotlin.sms.classes.*


// группы

val M21 = Group("М21")
val M40 = Group("М40")
val M60 = Group("М60")
val W60 = Group("Ж60")


// разряды

val rank2 = Rank("2р")
val rank1 = Rank("1р")
val rankCandidate = Rank("КМС")
val rankMaster = Rank("МС")

// Команда "Московский компас"

val vasily = Athlete("Смирнов", "Василий", 2001,
    M21, rank2, "Московский Компас")
val ivan = Athlete("Смирнов", "Иван", 1978,
    M40, rankCandidate, "Московский Компас")
val maria = Athlete("Калинина", "Мария", 1958,
    W60, rank1, "Московский Компас")
val mikhail = Athlete("Петров", "Михаил", 1990,
    M21, rankMaster, "Московский Компас")

val MoscowTeam = Team("Московский Компас", listOf(vasily, ivan, maria, mikhail))


// Команда "Выборгские медведи"

val dmitriy = Athlete("Иванов", "Дмитрий", 1950,
    M60, rankCandidate, "Выборгские медведи")
val kirill = Athlete("Иванов", "Кирилл", 1953,
    M60, rankMaster, "Выборгские медведи")

val VyborgTeam = Team("Выборгские медведи", listOf(dmitriy, kirill))


