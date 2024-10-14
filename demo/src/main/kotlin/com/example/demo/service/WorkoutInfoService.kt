package com.example.demo.service

import com.example.demo.entity.Workout
import com.example.demo.entity.WorkoutInfo
import com.example.demo.repository.WorkoutInfoRepository
import com.example.demo.repository.WorkoutRepository

import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(WorkoutInfoService::class.java)

    fun init() {
        try {
            // Check if WorkoutInfo table is empty before populating
            if (workoutInfoRepository.count() == 0L) {
                logger.info("WorkoutInfo table is empty, starting population.")

                // Load file from classpath
                val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("spreadsheet/Workout Spreadsheet.xlsx")
                    ?: throw FileNotFoundException("Spreadsheet not found in classpath!")

                // Populate WorkoutInfo using the spreadsheet
                inputStream.use { stream ->
                    populateWorkoutInfoFromExcel(stream)
                }
            } else {
                logger.info("WorkoutInfo table already populated, skipping initialization.")
            }
        } catch (e: Exception) {
            logger.error("Error occurred during WorkoutInfo initialization: ${e.message}", e)
            throw e
        }
    }

    fun populateWorkoutInfoFromExcel(inputStream: InputStream) {
        try {
            val workbook = XSSFWorkbook(inputStream)
            val workoutInfos = mutableListOf<WorkoutInfo>()

            workbook.use { wb ->
                val sheet = wb.getSheetAt(0)
                logger.info("Processing sheet: ${sheet.sheetName}, rows: ${sheet.physicalNumberOfRows}")

                for (rowNum in 1 until sheet.physicalNumberOfRows) {
                    val row = sheet.getRow(rowNum)
                    val exerciseName = row.getCell(0).stringCellValue
                    val equipment = row.getCell(1).stringCellValue

                    val workout = workoutRepository.findByNameAndEquipment(exerciseName, equipment)
                    if (workout != null) {
                        workoutInfos.addAll(populateWorkoutInfoForGoals(row, workout))
                    } else {
                        logger.warn("Workout not found for name: $exerciseName, equipment: $equipment, skipping row.")
                    }
                }
            }

            // Batch insert workoutInfos
            if (workoutInfos.isNotEmpty()) {
                workoutInfoRepository.saveAll(workoutInfos)
                logger.info("Successfully inserted ${workoutInfos.size} WorkoutInfo entries.")
            } else {
                logger.warn("No WorkoutInfo entries to insert.")
            }
        } catch (e: Exception) {
            logger.error("Error populating WorkoutInfo from Excel: ${e.message}", e)
            throw e
        }
    }

    fun populateWorkoutInfoForGoals(row: Row, workout: Workout): List<WorkoutInfo> {
        val workoutInfos = mutableListOf<WorkoutInfo>()
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

                logger.info("Creating WorkoutInfo for ${workout.name}, Goal: $fitnessGoal, Score: $scoreCategory, Sets: $sets, Reps: $reps, Weight: $weight")

                val workoutInfo = WorkoutInfo(
                    workout = workout,
                    sets = sets,
                    reps = reps,
                    weight = weight,
                    fitnessGoal = fitnessGoal,
                    fitnessScore = scoreCategory
                )
                workoutInfos.add(workoutInfo)
            }
        }

        return workoutInfos
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
