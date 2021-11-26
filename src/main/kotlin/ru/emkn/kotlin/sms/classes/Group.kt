package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.GROUP_NAMES

data class Group(val groupName: String) {
    init {
        require(GROUP_NAMES.contains(groupName)) { "group name must be mentioned in config file" }
    }
}
