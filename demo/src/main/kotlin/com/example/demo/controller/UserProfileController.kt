package com.example.demo.controller

import com.example.demo.dto.UserProfileRequest
import com.example.demo.entity.UserProfile
import com.example.demo.service.AuthenticationService
import com.example.demo.service.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profile")
class UserProfileController(
    private val authenticationService: AuthenticationService,  // Inject the AuthenticationService
    private val userProfileService: UserProfileService         // Inject the UserProfileService
) {

    @PostMapping("/update")
    fun createOrUpdateProfile(@RequestBody profileRequest: UserProfileRequest): ResponseEntity<String> {
        return try {
            // Get the currently authenticated user based on ID from the JWT
            val account = authenticationService.getAuthenticatedUser()

            // Create or update the profile
            val profile = UserProfile(
                account = account,
                height = profileRequest.height,
                weight = profileRequest.weight,
                BMICategory = profileRequest.BMICategory,
                fitnessGoal = profileRequest.fitnessGoal,
                fitnessScore = profileRequest.fitnessScore.toInt(),
                muscleGroup = profileRequest.muscleGroup
            )
            userProfileService.createProfile(profile)

            ResponseEntity.status(HttpStatus.CREATED).body("User profile created/updated successfully")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating/updating profile")
        }
    }

}