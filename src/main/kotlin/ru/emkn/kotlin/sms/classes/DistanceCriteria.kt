package ru.emkn.kotlin.sms.classes

import java.time.Duration

enum class DistanceType {
    FIXED, CHOICE
}

interface DistanceCriteria {
    val distanceType: DistanceType
    fun assertValid(competitorData: CompetitorData) : Boolean
    fun getResult(competitorData: CompetitorData) : Duration?
    fun getSplit(competitorData: CompetitorData) : List<CheckpointDuration>?
}

class FixedRace() : DistanceCriteria {
    override val distanceType = DistanceType.FIXED
    override fun assertValid(competitorData: CompetitorData): Boolean {
        TODO("Not yet implemented")
    }

    override fun getResult(competitorData: CompetitorData): Duration? {
        TODO("Not yet implemented")
    }

    override fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        TODO("Not yet implemented")
    }

}

class ChoiceRace: DistanceCriteria {
    override val distanceType = DistanceType.CHOICE
    override fun assertValid(competitorData: CompetitorData): Boolean {
        TODO("Not yet implemented")
    }

    override fun getResult(competitorData: CompetitorData): Duration? {
        TODO("Not yet implemented")
    }

    override fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        TODO("Not yet implemented")
    }
}