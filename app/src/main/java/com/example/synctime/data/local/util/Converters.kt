package com.example.synctime.data.local.util

import androidx.room.TypeConverter
import com.example.synctime.data.local.entity.*
import com.example.synctime.data.model.PositionType

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole) = value.name

    @TypeConverter
    fun toUserRole(value: String) = UserRole.valueOf(value)

    @TypeConverter
    fun fromPositionType(value: PositionType) = value.code

    @TypeConverter
    fun toPositionType(value: String) = PositionType.fromCode(value)

    @TypeConverter
    fun fromScheduleStatus(value: ScheduleStatus) = value.name

    @TypeConverter
    fun toScheduleStatus(value: String) = ScheduleStatus.valueOf(value)

    @TypeConverter
    fun fromAttendanceStatus(value: AttendanceStatus) = value.name

    @TypeConverter
    fun toAttendanceStatus(value: String) = AttendanceStatus.valueOf(value)

    @TypeConverter
    fun fromRequestType(value: RequestType) = value.name

    @TypeConverter
    fun toRequestType(value: String) = RequestType.valueOf(value)

    @TypeConverter
    fun fromRequestStatus(value: RequestStatus) = value.name

    @TypeConverter
    fun toRequestStatus(value: String) = RequestStatus.valueOf(value)
}
