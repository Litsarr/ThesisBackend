package com.example.demo.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table(name = "user_profile")
data class UserProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_account_id")
    @JsonManagedReference
    val account: UserAccount,

    @Column(nullable = false)
    var height: Double,

    @Column(nullable = false)
    var weight: Double,

    @Column(name = "BMI_categ", nullable = false)
    var BMICategory: String,

    @Column(name = "fitness_goal", nullable = false)
    var fitnessGoal: String,

    @Column(name = "fitness_score", nullable = false)
    var fitnessScore: Int,

    @Column(name = "muscle_group", nullable = false)
    var muscleGroup: String
) {
    override fun toString(): String {
        return "UserProfile(id=$id, height=$height, weight=$weight, BMICategory=$BMICategory, fitnessGoal=$fitnessGoal, fitnessScore=$fitnessScore, muscleGroup=$muscleGroup)"
    }
}
