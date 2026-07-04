package com.example.synctime.data.mapper

import com.example.synctime.data.local.entity.UserEntity
import com.example.synctime.data.model.StaffDto
import com.example.synctime.data.model.UserAuthDto

fun UserEntity.toAuthDto(): UserAuthDto {
    return UserAuthDto(
        id = id,
        fullName = fullName,
        role = role,
        position = position,
        branchId = branchId
    )
}

fun UserEntity.toStaffDto(branchName: String = "Chi nhánh chính"): StaffDto {
    return StaffDto(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        branchId = branchId,
        position = position,
        branchName = branchName
    )
}
