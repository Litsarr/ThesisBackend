package com.example.demo.service

import com.example.demo.entity.Workout
import com.example.demo.repository.WorkoutClassificationRepository
import com.example.demo.repository.WorkoutRepository
import jakarta.annotation.PostConstruct
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.FileInputStream

@Service
class WorkoutService(    private val workoutRepository: WorkoutRepository,
                         private val workoutClassificationRepository: WorkoutClassificationRepository) {


    var isWorkoutPopulated = false // Flag to indicate if workout table is populated

    @PostConstruct
    fun populateWorkoutsFromSpreadsheet() {
        // Path to your Excel file
        val filePath = "C:/Users/63956/Downloads/Workout Spreadsheet.xlsx"
        val inputStream = FileInputStream(filePath)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        // Iterate through each row of the sheet
        for (row in sheet) {
            if (row.rowNum == 0) continue // Skip header row

            val exerciseName = row.getCell(0).stringCellValue
            val equipment = row.getCell(1).stringCellValue
            val classificationName = row.getCell(2).stringCellValue

            val classification = workoutClassificationRepository.findByName(classificationName)

            if (classification != null) {
                val workoutDescription = "$exerciseName using $equipment"

                val workout = Workout(
                    name = exerciseName,
                    description = workoutDescription,
                    equipment = equipment,
                    classification = classification
                )

                if (!workoutRepository.existsByNameAndEquipment(exerciseName, equipment)) {
                    workoutRepository.save(workout)
                }
            }
        }

        workbook.close()
        inputStream.close()

        isWorkoutPopulated = true // Set the flag when done
    }

    fun getWorkoutById(id: Long): Workout? {
        return workoutRepository.findById(id).orElse(null)
    }

    fun getWorkoutByName(name: String): Workout? {
        return workoutRepository.findByName(name)
    }

    fun createWorkout(workout: Workout): Workout {
        return workoutRepository.save(workout)
    }

    fun getAllWorkouts(): List<Workout> {
        return workoutRepository.findAll()
    }
}