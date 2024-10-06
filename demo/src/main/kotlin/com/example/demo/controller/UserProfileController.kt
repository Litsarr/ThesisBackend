package com.example.demo.controller

import com.example.demo.dto.UserProfileRequest
import com.example.demo.entity.UserProfile
import com.example.demo.service.AuthenticationService
import com.example.demo.service.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/profile")
class UserProfileController(
    private val authenticationService: AuthenticationService,
    private val userProfileService: UserProfileService
) {

    @PostMapping("/create")
    fun createProfile(@RequestBody profileRequest: UserProfileRequest) {
        val account = authenticationService.getAuthenticatedUser()
        val existingProfile = userProfileService.getProfileByUserId(account.id)

        if (existingProfile != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Profile already exists")
        }

        val profile = UserProfile(
            account = account,
            height = profileRequest.height,
            weight = profileRequest.weight,
            BMICategory = profileRequest.BMICategory,
            fitnessGoal = profileRequest.fitnessGoal,
            fitnessScore = profileRequest.fitnessScore,
            muscleGroup = profileRequest.muscleGroup
        )
        userProfileService.createProfile(profile)
    }


    @PutMapping("/update")
    fun updateProfile(@RequestBody profileRequest: UserProfileRequest) {
        // Get the currently authenticated user based on ID from the JWT
        val account = authenticationService.getAuthenticatedUser()

        // Find the existing profile
        val existingProfile = userProfileService.getProfileByUserId(account.id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found")

        // Update the existing profile
        existingProfile.height = profileRequest.height
        existingProfile.weight = profileRequest.weight
        existingProfile.BMICategory = profileRequest.BMICategory
        existingProfile.fitnessGoal = profileRequest.fitnessGoal
        existingProfile.fitnessScore = profileRequest.fitnessScore.toInt()
        existingProfile.muscleGroup = profileRequest.muscleGroup

        userProfileService.updateProfile(existingProfile)
    }
}

