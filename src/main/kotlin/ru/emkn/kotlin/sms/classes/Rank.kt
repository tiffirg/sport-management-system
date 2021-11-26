package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.RANKS


data class Rank(val rankName: String) {
    init {
        require(RANKS.contains(rankName)) { "rank name must be mentioned in config file" }
    }
}
