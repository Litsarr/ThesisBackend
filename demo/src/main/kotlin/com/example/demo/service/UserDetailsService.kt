package com.example.demo.service

import com.example.demo.entity.UserAccount
import com.example.demo.repository.UserAccountRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
    private val userAccountRepository: UserAccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: UserAccount = userAccountRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        // You can return a custom UserDetails implementation or Spring's default User
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            listOf()  // Set authorities or roles if you have them
        )
    }
}