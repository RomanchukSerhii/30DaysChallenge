package com.serhii_romanchuk.a30_dayschallenge.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Challenge (
    val id: Int,
    val name: String,
    val repsPerMonthNeedToDo: String,
    val schedule: String
    ) : Parcelable
{
    var repsPerDayNeedToDo: Int = 0
    var currentRepsPerDayDone: Int = 0
    var currentRepsDone: Int = 0
    var currentDay: Int = 1
    private var risingRepsRate: Int = 0

    init {
        if (schedule != SCHEDULE_NORMAL) {
            risingRepsRate = when(repsPerMonthNeedToDo) {
                "3 000" -> 2
                "5 000" -> 3
                else -> 5
            }
        }
    }

    fun setRepsPerDayNeedToDo() {
        val repsLeftToDoPerMonth = repsPerMonthNeedToDo.filter { !it.isWhitespace() }.toInt() - currentRepsDone
        repsPerDayNeedToDo = if (schedule == SCHEDULE_NORMAL) {
            repsLeftToDoPerMonth / (31 - currentDay)
        } else {
            repsLeftToDoPerMonth / (31 - currentDay) - (30 - currentDay) * risingRepsRate
        }
    }

    companion object {
        const val SCHEDULE_NORMAL = "Звичайний"
    }
}