package com.example.demo.repository

import com.example.demo.entity.WorkoutRoutine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutRoutineRepository : JpaRepository<WorkoutRoutine, Long> {
    fun findByUserId(userId: Long): List<WorkoutRoutine>
    fun findAllByUserId(userId: Long): List<WorkoutRoutine>
}