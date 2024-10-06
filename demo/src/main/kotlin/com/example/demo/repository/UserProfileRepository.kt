package com.example.demo.repository

import com.example.demo.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    // Find UserProfile by the account's ID
    fun findByAccount_Id(accountId: Long): UserProfile?
}
