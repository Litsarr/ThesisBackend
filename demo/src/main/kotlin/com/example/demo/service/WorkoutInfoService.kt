package com.example.demo.service

import com.example.demo.entity.Workout
import com.example.demo.entity.WorkoutInfo
import com.example.demo.repository.WorkoutInfoRepository
import com.example.demo.repository.WorkoutRepository

import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.FileInputStream

@Service
class WorkoutInfoService(
    private val workoutRepository: WorkoutRepository,
    private val workoutInfoRepository: WorkoutInfoRepository,
    private val workoutService: WorkoutService
) {

    // Path to your spreadsheet
    private val filePath = "C:/Users/63956/Downloads/Workout Spreadsheet.xlsx"

    @PostConstruct
    fun init() {
        // Wait for WorkoutService to populate first
        waitForWorkoutPopulation()

        // Check if WorkoutInfo table is empty before populating
        if (workoutInfoRepository.count() == 0L) {
            populateWorkoutInfoFromExcel(filePath)
        } else {
            println("WorkoutInfo table already populated, skipping initialization.")
        }
    }

    private fun waitForWorkoutPopulation() {
        while (!workoutService.isWorkoutPopulated) {
            Thread.sleep(1000) // Wait 1 second before checking again
        }
    }

    @Transactional
    fun populateWorkoutInfoFromExcel(filePath: String) {
        val inputStream = FileInputStream(filePath)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        for (rowNum in 1 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(rowNum)
            val exerciseName = row.getCell(0).stringCellValue
            val equipment = row.getCell(1).stringCellValue

            val workout = workoutRepository.findByNameAndEquipment(exerciseName, equipment)
            if (workout != null) {
                populateWorkoutInfoForGoals(row, workout)
            }
        }
        workbook.close()
        inputStream.close()
    }

    private fun populateWorkoutInfoForGoals(row: Row, workout: Workout) {
        val fitnessGoals = listOf("Muscle Building", "Weight Loss")
        val fitnessScoreCategories = listOf("Below Average", "Average", "Above Average")

        fitnessGoals.forEach { fitnessGoal ->
            fitnessScoreCategories.forEach { scoreCategory ->
                val sets = row.getCell(9).numericCellValue.toInt()
                val reps = if (fitnessGoal == "Muscle Building") {
                    row.getCell(11).numericCellValue.toInt()
                } else {
                    row.getCell(10).numericCellValue.toInt()
                }

                val weight = when (fitnessGoal) {
                    "Muscle Building" -> when (scoreCategory) {
                        "Below Average" -> row.getCell(6).numericCellValue
                        "Average" -> row.getCell(7).numericCellValue
                        "Above Average" -> row.getCell(8).numericCellValue
                        else -> 0.0
                    }
                    "Weight Loss" -> when (scoreCategory) {
                        "Below Average" -> row.getCell(3).numericCellValue
                        "Average" -> row.getCell(4).numericCellValue
                        "Above Average" -> row.getCell(5).numericCellValue
                        else -> 0.0
                    }
                    else -> 0.0
                }

                println("Creating WorkoutInfo: Sets: $sets, Reps: $reps, Weight: $weight, Goal: $fitnessGoal, Score: $scoreCategory")

                val workoutInfo = WorkoutInfo(
                    workout = workout,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    fitnessGoal = fitnessGoal,
                    fitnessScore = scoreCategory
                )

                workoutInfoRepository.save(workoutInfo)
            }
        }
    }


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