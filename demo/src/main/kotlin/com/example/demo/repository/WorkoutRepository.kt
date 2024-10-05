package com.example.demo.repository

import com.example.demo.entity.Workout
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkoutRepository : JpaRepository<Workout, Long> {
    fun findByName(name: String): Workout?
}