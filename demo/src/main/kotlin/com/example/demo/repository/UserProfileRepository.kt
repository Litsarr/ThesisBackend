package com.example.demo.repository

import com.example.demo.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : JpaRepository<UserProfile, Long> {
    // You can add any custom query methods if needed in the future
}