package com.example.demo.service

import com.example.demo.entity.UserAccount
import com.example.demo.entity.UserProfile
import com.example.demo.repository.UserAccountRepository
import com.example.demo.repository.UserProfileRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserProfileService(
    private val userProfileRepository: UserProfileRepository,
    private val userAccountRepository: UserAccountRepository  // Inject the UserProfileRepository
) {

    fun createProfile(userProfile: UserProfile): UserProfile {
        // Check if the user account exists
        val userAccount = userAccountRepository.findById(userProfile.account.id)

        // Save the new user profile to the database
        return userProfileRepository.save(userProfile)
    }


    fun getProfile(userId: Long): Optional<UserProfile> {
        return userProfileRepository.findById(userId)
    }

    fun categorizeFitnessScore(score: Int): String {
        return when (score) {
            in 4..6 -> "Below Average"
            in 7..9 -> "Average"
            in 10..12 -> "Above Average"
            else -> throw IllegalArgumentException("Invalid fitness score: $score")
        }
    }

    // Fetch UserProfile by username
    fun getProfileByUsername(username: String): UserProfile {
        val userAccount = userAccountRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        return userProfileRepository.findByUserID(userAccount.id)
            ?: throw EntityNotFoundException("UserProfile not found for user: $username")
    }

    fun getProfileByUserId(userId: Long): UserProfile? {
        return userProfileRepository.findByUserID(userId)

    }

    fun updateProfile(profile: UserProfile): UserProfile {
        val existingProfile = userProfileRepository.findById(profile.id)
            .orElseThrow { EntityNotFoundException("Profile not found for id: ${profile.id}") }
        // Optionally update fields here based on business logic before saving
        return userProfileRepository.save(existingProfile)  // Save will update if the entity already exists
    }

}
