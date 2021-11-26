package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.RANKS
import ru.emkn.kotlin.sms.utils.IncorrectGroupException

data class Group(val groupName: String) {
    init {
        if (!GROUP_NAMES.contains(groupName)) {
            throw IncorrectGroupException(groupName)
        }
    }
}
