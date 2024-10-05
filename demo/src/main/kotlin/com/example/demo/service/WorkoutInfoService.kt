package com.example.demo.service

import com.example.demo.entity.WorkoutInfo
import com.example.demo.repository.WorkoutInfoRepository
import org.springframework.stereotype.Service

@Service
class WorkoutInfoService(private val workoutInfoRepository: WorkoutInfoRepository) {

    fun getWorkoutInfoByWorkoutId(workoutId: Long): List<WorkoutInfo> {
        return workoutInfoRepository.findByWorkoutId(workoutId)
    }

    fun createWorkoutInfo(workoutInfo: WorkoutInfo): WorkoutInfo {
        return workoutInfoRepository.save(workoutInfo)
    }

    fun getAllWorkoutInfos(): List<WorkoutInfo> {
        return workoutInfoRepository.findAll()
    }
}