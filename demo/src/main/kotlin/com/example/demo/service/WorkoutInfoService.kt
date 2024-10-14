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
import java.io.FileNotFoundException
import java.io.InputStream

@Service
class WorkoutInfoService(
    private val workoutRepository: WorkoutRepository,
    private val workoutInfoRepository: WorkoutInfoRepository,
    private val workoutService: WorkoutService
) {

    // Removed @PostConstruct, this method will be called manually from DataInitializer
    fun init() {
        // Wait for WorkoutService to populate first
        waitForWorkoutPopulation()

        // Check if WorkoutInfo table is empty before populating
        if (workoutInfoRepository.count() == 0L) {
            // Load file from classpath
            val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("spreadsheet/Workout Spreadsheet.xlsx")
                ?: throw FileNotFoundException("Spreadsheet not found in classpath!")

            // If the file is not found, throw an exception or log the error

            // Populate WorkoutInfo using the spreadsheet
            inputStream.use { stream ->
                populateWorkoutInfoFromExcel(stream)
            }
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
    fun populateWorkoutInfoFromExcel(inputStream: InputStream) {
        val workbook = XSSFWorkbook(inputStream)

        workbook.use { wb ->
            val sheet = wb.getSheetAt(0)

            for (rowNum in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(rowNum)
                val exerciseName = row.getCell(0).stringCellValue
                val equipment = row.getCell(1).stringCellValue

                val workout = workoutRepository.findByNameAndEquipment(exerciseName, equipment)
                if (workout != null) {
                    populateWorkoutInfoForGoals(row, workout)
                }
            }
        }
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

    // Core Service Methods
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
