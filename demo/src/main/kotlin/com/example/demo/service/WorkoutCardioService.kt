package com.example.demo.service

import com.example.demo.entity.WorkoutCardio
import com.example.demo.repository.WorkoutCardioRepository
import org.springframework.stereotype.Service

@Service
class WorkoutCardioService(private val workoutCardioRepository: WorkoutCardioRepository) {

    fun getWorkoutCardioById(id: Long): WorkoutCardio? {
        return workoutCardioRepository.findById(id).orElse(null)
    }

    fun getWorkoutCardioByName(name: String): WorkoutCardio? {
        return workoutCardioRepository.findByName(name)
    }

    fun createWorkoutCardio(workoutCardio: WorkoutCardio): WorkoutCardio {
        return workoutCardioRepository.save(workoutCardio)
    }

    fun getAllWorkoutCardios(): List<WorkoutCardio> {
        return workoutCardioRepository.findAll()
    }
}