package com.example.demo.controller

import com.example.demo.entity.UserAccount
import com.example.demo.entity.WorkoutRoutine
import com.example.demo.repository.WorkoutInfoRepository
import com.example.demo.repository.WorkoutRoutineRepository
import com.example.demo.service.UserAccountService
import com.example.demo.service.UserProfileService
import com.example.demo.service.WorkoutRoutineService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/workout-routine")
class WorkoutRoutineController(
    private val workoutRoutineService: WorkoutRoutineService,
    private val userProfileService: UserProfileService,
    private val userAccountService: UserAccountService,
    private val workoutInfoRepository: WorkoutInfoRepository,
    private val workoutRoutineRepository: WorkoutRoutineRepository
) {

    @PostMapping("/generate")
    fun generateWorkoutRoutine(authentication: Authentication): ResponseEntity<Map<String, List<WorkoutRoutine>>> {
        try {
            val userDetails = authentication.principal as? UserDetails
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val username = userDetails.username
            println("Generating workout routine for username: $username")

            val userAccount = userAccountService.findByUsername(username)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            println("UserAccount found with ID: ${userAccount.id}")

            val userProfile = userProfileService.getProfileByUserId(userAccount.id)
            println("UserProfile found: $userProfile")

            return if (userProfile != null) {
                val workoutRoutinePlan = workoutRoutineService.generate7DayWorkoutPlan(userProfile)

                // Persist each workout routine day and associated entities
                workoutRoutinePlan.forEach { (day, routines) ->
                    routines.forEach { routine ->
                        routine.workoutInfo?.let { workoutInfo ->
                            if (workoutInfo.id == 0L) {
                                workoutInfoRepository.save(workoutInfo) // Correctly save workoutInfo
                            }
                        }

                        if (routine.id == 0L) {
                            workoutRoutineRepository.save(routine) // Correctly save workoutRoutine
                        }
                    }
                }

                ResponseEntity.ok(workoutRoutinePlan)
            } else {
                ResponseEntity.badRequest().body(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }





    @GetMapping("/my-routines")
    fun getWorkoutRoutines(authentication: Authentication): ResponseEntity<List<WorkoutRoutine>> {
        try {
            // Extract authenticated user's username
            val userDetails = authentication.principal as? UserDetails
                ?: return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            val username = userDetails.username
            println("Fetching workout routines for username: $username")

            // Find the UserAccount by username
            val userAccount = userAccountService.findByUsername(username)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

            // Retrieve all workout routines for the user
            val workoutRoutines = workoutRoutineService.findAllByUser(userAccount.id)

            return ResponseEntity.ok(workoutRoutines)
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}
