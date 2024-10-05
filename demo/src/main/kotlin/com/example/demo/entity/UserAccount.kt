package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_account")
data class UserAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    var password: String,

    @OneToOne(mappedBy = "account", cascade = [CascadeType.ALL])
    val profile: UserProfile? = null
)