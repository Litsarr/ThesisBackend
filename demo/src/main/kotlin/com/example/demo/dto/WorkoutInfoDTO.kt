package com.example.demo.dto

data class WorkoutInfoDTO(
    val id: Long,
    val workoutId: Long,  // Reference to the workout ID
    val sets: Int,
    val reps: Int,
    val weight: Double,
    val fitnessGoal: String,
    val fitnessScore: String
)
