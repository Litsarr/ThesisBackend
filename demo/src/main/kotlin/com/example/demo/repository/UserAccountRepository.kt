package com.example.demo.repository

import com.example.demo.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, Long> {
    // Method to check if a username already exists
    fun existsByUsername(username: String): Boolean

    // Method to find a user by username
    fun findByUsername(username: String): UserAccount?
}
