package com.example.demo.repository

import com.example.demo.entity.WorkoutCardio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutCardioRepository : JpaRepository<WorkoutCardio, Long> {
    fun findByName(name: String): WorkoutCardio?
}