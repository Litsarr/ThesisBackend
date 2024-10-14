package com.example.demo.service

import com.example.demo.entity.WorkoutClassification
import com.example.demo.repository.WorkoutClassificationRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer(
    private val workoutService: WorkoutService,
    private val workoutInfoService: WorkoutInfoService,
    private val workoutClassificationRepository: WorkoutClassificationRepository
) {

    @Bean
    fun runDataInitialization(): CommandLineRunner {
        return CommandLineRunner {
            populateClassifications()
            workoutService.populateWorkoutsFromSpreadsheet()
            workoutInfoService.init()
        }
    }

    // Replace @PostConstruct method
    fun populateClassifications() {
        val classifications = listOf("push", "pull", "knee", "hip", "cardio", "core")

        for (classification in classifications) {
            if (workoutClassificationRepository.findByName(classification) == null) {
                workoutClassificationRepository.save(WorkoutClassification(name = classification))
            }
        }
    }
}