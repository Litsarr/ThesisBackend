package com.example.demo.service

import com.example.demo.entity.UserAccount
import com.example.demo.repository.UserAccountRepository
import com.example.demo.security.JwtUtil
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userAccountRepository: UserAccountRepository,
    private val jwtUtil: JwtUtil
) {

    private val passwordEncoder = BCryptPasswordEncoder()

    // This method handles login authentication and returns a JWT token
    fun authenticate(username: String, password: String): String? {
        val user = userAccountRepository.findByUsername(username) ?: return null
        return if (passwordEncoder.matches(password, user.password)) {
            jwtUtil.generateToken(username)  // Return JWT token if credentials match
        } else {
            null
        }
    }

    // This method fetches the currently authenticated user based on the JWT token
    fun getAuthenticatedUser(): UserAccount {
        // Extract the authenticated user's username from the SecurityContext
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name

        // Find the user by username in the database
        return userAccountRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
    }
}
