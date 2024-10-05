package com.example.demo.controller

import com.example.demo.dto.LoginRequest
import com.example.demo.dto.RegistrationRequest
import com.example.demo.entity.UserAccount
import com.example.demo.entity.UserProfile
import com.example.demo.service.AuthenticationService
import com.example.demo.service.UserAccountService
import com.example.demo.service.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(
    private val userAccountService: UserAccountService,
    private val authenticationService: AuthenticationService,
    private val userProfileService: UserProfileService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody registrationRequest: RegistrationRequest): ResponseEntity<String> {
        return try {
            // Register the user account (only username and password)
            val userAccount = UserAccount(username = registrationRequest.username, password = registrationRequest.password)
            userAccountService.register(userAccount)

            ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val token = authenticationService.authenticate(loginRequest.username, loginRequest.password)
        return if (token != null) {
            val responseBody = mapOf("token" to token)
            ResponseEntity.ok(responseBody)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid credentials"))
        }
    }
}