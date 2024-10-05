package com.example.demo.service

import com.example.demo.entity.UserAccount
import com.example.demo.repository.UserAccountRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserAccountService(
    private val userAccountRepository: UserAccountRepository,  // Inject the UserAccountRepository
    private val passwordEncoder: PasswordEncoder      // Inject the PasswordEncoder for password hashing
) {

    fun register(userAccount: UserAccount): UserAccount {
        // Check if the username already exists
        if (userAccountRepository.existsByUsername(userAccount.username)) {
            throw IllegalArgumentException("Username already exists")
        }

        // Encode the password
        userAccount.password = passwordEncoder.encode(userAccount.password)

        // Save and return the registered user
        return userAccountRepository.save(userAccount)
    }

    fun findByUsername(username: String): UserAccount? {
        return userAccountRepository.findByUsername(username)
    }
}