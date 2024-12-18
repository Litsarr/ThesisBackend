package com.example.demo.dto

data class UserProfileRequest(
    val height: Double,
    val weight: Double,
    val BMICategory: String,
    val fitnessGoal: String,
    val fitnessScore: String,
    val muscleGroup: String
)