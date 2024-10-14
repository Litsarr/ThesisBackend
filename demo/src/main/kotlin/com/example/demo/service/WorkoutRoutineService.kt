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
        val usedExerciseNames = mutableSetOf<String>() // Track exercise names used for this day

        // Main exercises (randomize equipment/machine each time)
        val mainExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
            classification, userProfile.fitnessGoal, categorizedScore
        ).groupBy { it.workout.name } // Group by exercise name to get all possible equipment options

        val selectedMainExercises = mainExercises
            .keys // Get the distinct exercise names
            .shuffled() // Shuffle the exercise names for randomness
            .filter { it !in usedExerciseNames } // Ensure exercise name uniqueness for the day
            .take(mainExerciseCount) // Take the number of required exercises
            .map { exerciseName ->
                // For each exercise, randomly pick an equipment/machine from the available options
                val workoutOptions = mainExercises[exerciseName]!!.shuffled() // Shuffle for random selection each time
                workoutOptions.first() // Pick the first after shuffling to randomize the equipment
            }

        usedExerciseNames.addAll(selectedMainExercises.map { it.workout.name }) // Mark used exercise names

        // Core exercises (randomize equipment/machine each time)
        val coreExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
            "core", userProfile.fitnessGoal, categorizedScore
        ).groupBy { it.workout.name }

        val selectedCoreExercises = coreExercises
            .keys
            .shuffled()
            .filter { it !in usedExerciseNames }
            .take(coreExerciseCount)
            .map { exerciseName ->
                val workoutOptions = coreExercises[exerciseName]!!.shuffled() // Shuffle for random selection
                workoutOptions.first() // Randomly select one with different equipment each time
            }

        usedExerciseNames.addAll(selectedCoreExercises.map { it.workout.name }) // Mark used exercise names

        // Combine main and core exercises for the day
        val exercises = mutableListOf<WorkoutInfo>()
        exercises.addAll(selectedMainExercises)
        exercises.addAll(selectedCoreExercises)

        // Add cardio if it's a weight loss plan (randomize equipment each time)
        if (isWeightLoss) {
            val cardioExercises = workoutInfoRepository.findByWorkout_Classification_NameAndFitnessGoalAndFitnessScore(
                "cardio", userProfile.fitnessGoal, categorizedScore
            ).groupBy { it.workout.name }

            val selectedCardio = cardioExercises.keys.shuffled()
                .filter { it !in usedExerciseNames }
                .take(1)
                .map { exerciseName ->
                    val workoutOptions = cardioExercises[exerciseName]!!.shuffled() // Shuffle for random selection
                    workoutOptions.first() // Randomize the equipment for cardio as well
                }

            usedExerciseNames.addAll(selectedCardio.map { it.workout.name }) // Mark used exercise names
            exercises.addAll(selectedCardio)
        }

        if (exercises.isEmpty()) return null

        // Create WorkoutRoutine for each exercise
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

