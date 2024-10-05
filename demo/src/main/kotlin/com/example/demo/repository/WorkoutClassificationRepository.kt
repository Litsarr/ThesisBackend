package com.example.demo.repository

import com.example.demo.entity.WorkoutClassification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutClassificationRepository : JpaRepository<WorkoutClassification, Long> {
    fun findByName(name: String): WorkoutClassification?
}