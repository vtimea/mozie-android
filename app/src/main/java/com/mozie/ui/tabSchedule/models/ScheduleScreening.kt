package com.mozie.ui.tabSchedule.models

import org.joda.time.DateTime

class ScheduleScreening(
    val id: String,
    val startTime: DateTime?,
    val type: String?,
    val voice: String?
)