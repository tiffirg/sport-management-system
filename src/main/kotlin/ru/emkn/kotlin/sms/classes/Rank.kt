package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.RANK_NAMES
import ru.emkn.kotlin.sms.utils.IncorrectRankException


data class Rank(val rankName: String?) {
    init {
        if (!RANK_NAMES.contains(rankName) && rankName != null) {
            throw IncorrectRankException(rankName)
        }
    }

    override fun toString(): String {
        if (rankName.isNullOrEmpty()) {
            return ""
        }
        return rankName
    }
}
