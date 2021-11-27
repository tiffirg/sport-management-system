package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.Athlete
import java.lang.Math.max
import java.time.LocalTime

fun scoreInGroup(athleteTime: LocalTime, leaderTime: LocalTime): Int {
    return 0.coerceAtLeast(100 * (2 - athleteTime.toSecondOfDay() / leaderTime.toSecondOfDay()))
    //max(0, 100 * (2 - <результат>/<результат победителя>)
}

fun teamResultsGeneration() {

}