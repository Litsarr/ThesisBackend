package com.example.demo.service

import com.example.demo.entity.WorkoutRoutine
import com.example.demo.repository.WorkoutRoutineRepository
import org.springframework.stereotype.Service

@Service
class WorkoutRoutineService(private val workoutRoutineRepository: WorkoutRoutineRepository) {

    fun getWorkoutRoutinesForUser(userId: Long): List<WorkoutRoutine> {
        return workoutRoutineRepository.findByUserId(userId)
    }

    fun createWorkoutRoutine(workoutRoutine: WorkoutRoutine): WorkoutRoutine {
        return workoutRoutineRepository.save(workoutRoutine)
    }

    fun getAllWorkoutRoutines(): List<WorkoutRoutine> {
        return workoutRoutineRepository.findAll()
    }
}