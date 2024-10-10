package com.example.demo.service

import com.example.demo.entity.Workout
import com.example.demo.repository.WorkoutClassificationRepository
import com.example.demo.repository.WorkoutRepository
import jakarta.annotation.PostConstruct
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

@Service
class WorkoutService(    private val workoutRepository: WorkoutRepository,
                         private val workoutClassificationRepository: WorkoutClassificationRepository) {


    var isWorkoutPopulated = false // Flag to indicate if workout table is populated

    @PostConstruct
    fun populateWorkoutsFromSpreadsheet() {
        // Load file from classpath
        val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("spreadsheet/Workout Spreadsheet.xlsx")
            ?: throw FileNotFoundException("Spreadsheet not found in classpath!")

        // If the file is not found, throw an exception

        // Use the input stream and workbook in a try-with-resources manner to ensure they're closed
        inputStream.use { stream ->
            val workbook = XSSFWorkbook(stream)
            workbook.use { wb ->
                val sheet = wb.getSheetAt(0)

                // Iterate through each row of the sheet
                for (row in sheet) {
                    if (row.rowNum == 0) continue // Skip header row

                    val exerciseName = row.getCell(0).stringCellValue
                    val equipment = row.getCell(1).stringCellValue
                    val classificationName = row.getCell(2).stringCellValue
                    val workoutDescription = row.getCell(12).stringCellValue
                    val imagePath = row.getCell(13).stringCellValue
                    val videoURL = row.getCell(14).stringCellValue
                    val classification = workoutClassificationRepository.findByName(classificationName)

                    if (classification != null) {
                        val workout = Workout(
                            name = exerciseName,
                            description = workoutDescription,
                            equipment = equipment,
                            classification = classification,
                            imageUrl = imagePath,
                            demoUrl = videoURL
                        )

                        // Save the workout if it doesn't already exist
                        if (!workoutRepository.existsByNameAndEquipment(exerciseName, equipment)) {
                            workoutRepository.save(workout)
                        }
                    }
                }
            }
        }

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