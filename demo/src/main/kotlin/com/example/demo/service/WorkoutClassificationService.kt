package com.example.demo.service

import com.example.demo.entity.WorkoutClassification
import com.example.demo.repository.WorkoutClassificationRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class WorkoutClassificationService(private val workoutClassificationRepository: WorkoutClassificationRepository) {


    // Populate classifications at startup
    @PostConstruct
    fun populateClassifications() {
        val classifications = listOf("push", "pull", "knee", "hip", "cardio", "core")

        for (classification in classifications) {
            if (workoutClassificationRepository.findByName(classification) == null) {
                workoutClassificationRepository.save(WorkoutClassification(name = classification))
            }
        }
    }

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