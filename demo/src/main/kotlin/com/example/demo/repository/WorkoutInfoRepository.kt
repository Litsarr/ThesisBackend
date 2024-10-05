package com.example.demo.repository

import com.example.demo.entity.WorkoutInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutInfoRepository : JpaRepository<WorkoutInfo, Long> {
    fun findByWorkoutId(workoutId: Long): List<WorkoutInfo>
}