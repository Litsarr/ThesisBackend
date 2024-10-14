package com.example.demo.service

import com.example.demo.entity.WorkoutClassification
import com.example.demo.repository.WorkoutClassificationRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class WorkoutClassificationService(private val workoutClassificationRepository: WorkoutClassificationRepository) {

    fun getWorkoutClassificationById(id: Long): WorkoutClassification? {
        return workoutClassificationRepository.findById(id).orElse(null)
    }

    fun getWorkoutClassificationByName(name: String): WorkoutClassification? {
        return workoutClassificationRepository.findByName(name)
    }

    fun createWorkoutClassification(workoutClassification: WorkoutClassification): WorkoutClassification {
        return workoutClassificationRepository.save(workoutClassification)
    }

    fun getAllWorkoutClassifications(): List<WorkoutClassification> {
        return workoutClassificationRepository.findAll()
    }
}
