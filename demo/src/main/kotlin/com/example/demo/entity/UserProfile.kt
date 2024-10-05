package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_profile")
data class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_account_id")
    val account: UserAccount,

    @Column(nullable = false)
    val height: Double,

    @Column(nullable = false)
    val weight: Double,

    @Column(name = "BMI_categ", nullable = false)
    val BMICategory: String,

    @Column(name = "fitness_goal", nullable = false)
    val fitnessGoal: String,

    @Column(name = "fitness_score", nullable = false)
    val fitnessScore: Int,

    @Column(name = "muscle_group", nullable = false)
    val muscleGroup: String
)
