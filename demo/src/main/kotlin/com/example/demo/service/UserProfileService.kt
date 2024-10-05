package com.example.demo.service

import com.example.demo.entity.UserProfile
import com.example.demo.repository.UserProfileRepository
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository  // Inject the UserProfileRepository
) {

    fun createProfile(userProfile: UserProfile): UserProfile {
        // Save the new user profile to the database
        return userProfileRepository.save(userProfile)
    }

    fun getProfile(userId: Long): UserProfile? {
        // Retrieve a user profile by their account ID
        return userProfileRepository.findById(userId).orElse(null)
    }
}
