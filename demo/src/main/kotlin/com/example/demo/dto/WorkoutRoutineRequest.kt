package com.example.demo.dto

data class WorkoutRoutineRequest(
    val userId: Long,
    val workoutInfoId: Long?,
    val dayNum: Int,
    val isRestDay: Boolean
)
