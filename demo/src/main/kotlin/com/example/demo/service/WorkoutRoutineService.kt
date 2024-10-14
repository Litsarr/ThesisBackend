package com.example.demo.service

import com.example.demo.dto.WorkoutInfoDTO
import com.example.demo.entity.UserProfile
import com.example.demo.entity.WorkoutInfo
import com.example.demo.entity.WorkoutRoutine
import com.example.demo.repository.WorkoutInfoRepository
import com.example.demo.repository.WorkoutRoutineRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class WorkoutRoutineService(
    private val workoutInfoRepository: WorkoutInfoRepository,
    private val workoutRoutineRepository: WorkoutRoutineRepository
) {

    @Transactional
    fun generate7DayWorkoutPlan(userProfile: UserProfile): Map<String, List<WorkoutRoutine>> {
        val plan = mutableMapOf<String, List<WorkoutRoutine>>()

        // Determine the type of workout plan based on muscle group preference
        when (userProfile.muscleGroup) {
            "Upper" -> generateUpperPlan(userProfile, plan)
            "Lower" -> generateLowerPlan(userProfile, plan)
            "Both" -> generateBothPlan(userProfile, plan)
            "Weight Loss" -> generateWeightLossPlan(userProfile, plan)
            else -> throw IllegalArgumentException("Invalid muscle group preference: ${userProfile.muscleGroup}")
        }

        return plan
    }

    // Upper body plan (Push/Pull with core exercises)
    private fun generateUpperPlan(userProfile: UserProfile, plan: MutableMap<String, List<WorkoutRoutine>>) {
        plan["Day 1"] = assignWorkoutsForDay(userProfile, "push", 4, 2, false, 1)
            ?: listOf(createRestDayRoutine(userProfile, 1))
        plan["Day 2"] = assignWorkoutsForDay(userProfile, "pull", 4, 2, false, 2)
            ?: listOf(createRestDayRoutine(userProfile, 2))
        plan["Day 3"] = listOf(createRestDayRoutine(userProfile, 3))
        plan["Day 4"] = assignWorkoutsForDay(userProfile, "push", 4, 2, false, 4)
            ?: listOf(createRestDayRoutine(userProfile, 4))
        plan["Day 5"] = assignWorkoutsForDay(userProfile, "pull", 4, 2, false, 5)
            ?: listOf(createRestDayRoutine(userProfile, 5))
        plan["Day 6"] = listOf(createRestDayRoutine(userProfile, 6))
        plan["Day 7"] = listOf(createRestDayRoutine(userProfile, 7))
    }

    // Lower body plan (Knee/Hips exercises)
    private fun generateLowerPlan(userProfile: UserProfile, plan: MutableMap<String, List<WorkoutRoutine>>) {
        plan["Day 1"] = assignWorkoutsForDay(userProfile, "knee", 6, 0, false, 1)
            ?: listOf(createRestDayRoutine(userProfile, 1))
        plan["Day 2"] = assignWorkoutsForDay(userProfile, "hip", 6, 0, false, 2)
            ?: listOf(createRestDayRoutine(userProfile, 2))
        plan["Day 3"] = listOf(createRestDayRoutine(userProfile, 3))
        plan["Day 4"] = assignWorkoutsForDay(userProfile, "knee", 6, 0, false, 4)
            ?: listOf(createRestDayRoutine(userProfile, 4))
        plan["Day 5"] = assignWorkoutsForDay(userProfile, "hip", 6, 0, false, 5)
            ?: listOf(createRestDayRoutine(userProfile, 5))
        plan["Day 6"] = listOf(createRestDayRoutine(userProfile, 6))
        plan["Day 7"] = listOf(createRestDayRoutine(userProfile, 7))
    }

    // Both plan (Upper and Lower body workouts, no cardio)
    private fun generateBothPlan(userProfile: UserProfile, plan: MutableMap<String, List<WorkoutRoutine>>) {
        plan["Day 1"] = assignWorkoutsForDay(userProfile, "push", 4, 2, false, 1)
            ?: listOf(createRestDayRoutine(userProfile, 1))
        plan["Day 2"] = assignWorkoutsForDay(userProfile, "pull", 4, 2, false, 2)
            ?: listOf(createRestDayRoutine(userProfile, 2))
        plan["Day 3"] = listOf(createRestDayRoutine(userProfile, 3))
        plan["Day 4"] = assignWorkoutsForDay(userProfile, "knee", 6, 0, false, 4)
            ?: listOf(createRestDayRoutine(userProfile, 4))
        plan["Day 5"] = assignWorkoutsForDay(userProfile, "hip", 6, 0, false, 5)
            ?: listOf(createRestDayRoutine(userProfile, 5))
        plan["Day 6"] = listOf(createRestDayRoutine(userProfile, 6))
        plan["Day 7"] = listOf(createRestDayRoutine(userProfile, 7))
    }

    // Weight Loss plan (Upper/Lower with added cardio)
    private fun generateWeightLossPlan(userProfile: UserProfile, plan: MutableMap<String, List<WorkoutRoutine>>) {
        plan["Day 1"] = assignWorkoutsForDay(userProfile, "push", 4, 2, true, 1)
            ?: listOf(createRestDayRoutine(userProfile, 1))
        plan["Day 2"] = assignWorkoutsForDay(userProfile, "pull", 4, 2, true, 2)
            ?: listOf(createRestDayRoutine(userProfile, 2))
        plan["Day 3"] = listOf(createRestDayRoutine(userProfile, 3))
        plan["Day 4"] = assignWorkoutsForDay(userProfile, "knee", 6, 0, true, 4)
            ?: listOf(createRestDayRoutine(userProfile, 4))
        plan["Day 5"] = assignWorkoutsForDay(userProfile, "hip", 6, 0, true, 5)
            ?: listOf(createRestDayRoutine(userProfile, 5))
        plan["Day 6"] = listOf(createRestDayRoutine(userProfile, 6))
        plan["Day 7"] = listOf(createRestDayRoutine(userProfile, 7))
    }

    // Assign workouts for each day
    private fun assignWorkoutsForDay(
        userProfile: UserProfile,
        classification: String,
        mainExerciseCount: Int,
        coreExerciseCount: Int,
        isWeightLoss: Boolean,
        dayNum: Int
    ): List<WorkoutRoutine>? {
        val workoutRoutines = mutableListOf<WorkoutRoutine>()

        val categorizedScore = categorizeFitnessScore(userProfile.fitnessScore)

        // Main exercises (randomize equipment/machine each time)
        val mainExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
            classification, userProfile.fitnessGoal, categorizedScore
        )
        val selectedMainExercises = mainExercises
            .shuffled() // Randomize the exercises and equipment
            .take(mainExerciseCount) // Pick the number of main exercises needed for the day

        // Core exercises (randomize equipment/machine each time)
        val coreExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
            "core", userProfile.fitnessGoal, categorizedScore
        )
        val selectedCoreExercises = coreExercises
            .shuffled() // Randomize the exercises and equipment
            .take(coreExerciseCount) // Pick the number of core exercises needed for the day

        // Combine main and core exercises for the day
        val exercises = mutableListOf<WorkoutInfo>()
        exercises.addAll(selectedMainExercises)
        exercises.addAll(selectedCoreExercises)

        // Add cardio if it's a weight loss plan
        if (isWeightLoss) {
            val cardioExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
                "cardio", userProfile.fitnessGoal, categorizedScore
            )
            val selectedCardio = cardioExercises
                .shuffled() // Randomize cardio equipment/machine as well
                .take(1)
            exercises.addAll(selectedCardio)
        }

        if (exercises.isEmpty()) return null

        // Create WorkoutRoutine for each exercise (with randomized equipment each time)
        exercises.forEach { workoutInfo ->
            workoutRoutines.add(
                WorkoutRoutine(
                    user = userProfile,
                    workoutInfo = workoutInfo,
                    dayNum = dayNum,
                    isRestDay = false
                )
            )
        }

        return workoutRoutines
    }



    // Categorize the fitness score
    private fun categorizeFitnessScore(score: Int): String {
        return when (score) {
            in 4..6 -> "Below Average"
            in 7..9 -> "Average"
            in 10..12 -> "Above Average"
            else -> throw IllegalArgumentException("Invalid fitness score: $score")
        }
    }

    // Create a rest day routine
    private fun createRestDayRoutine(userProfile: UserProfile, dayNum: Int): WorkoutRoutine {
        return WorkoutRoutine(
            user = userProfile,
            workoutInfo = null,
            dayNum = dayNum,
            isRestDay = true
        )
    }

    // Retrieve workout routines for a user
    fun getWorkoutRoutinesForUser(userProfileId: Long): List<WorkoutRoutine> {
        return workoutRoutineRepository.findByUserId(userProfileId)
    }

    // Find all workout routines by user
    fun findAllByUser(userProfileId: Long): List<WorkoutRoutine> {
        return workoutRoutineRepository.findAllByUserId(userProfileId)
    }

}

