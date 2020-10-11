package com.mozie.ui.tabSchedule.models

class ScheduleMovie(
    val id: String,
    val title: String?,
    val genre: String?,
    val length: Int?,
    val posterUrl: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleMovie

        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}