package com.example.demo.service

import com.example.demo.entity.Workout
import com.example.demo.repository.WorkoutRepository
import org.springframework.stereotype.Service

@Service
class WorkoutService(private val workoutRepository: WorkoutRepository) {

    fun getWorkoutById(id: Long): Workout? {
        return workoutRepository.findById(id).orElse(null)
    }

    fun getWorkoutByName(name: String): Workout? {
        return workoutRepository.findByName(name)
    }

    fun createWorkout(workout: Workout): Workout {
        return workoutRepository.save(workout)
    }

    fun getAllWorkouts(): List<Workout> {
        return workoutRepository.findAll()
    }
}