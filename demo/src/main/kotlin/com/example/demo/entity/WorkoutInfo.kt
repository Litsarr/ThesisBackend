package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout_info")
data class WorkoutInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "workout_id", referencedColumnName = "id")
    var workout: Workout,

    @Column(nullable = false)
    var sets: Int,

    @Column(nullable = false)
    var reps: Int,

    @Column(nullable = false)
    var weight: Double,

    @Column(name = "fitness_goal", nullable = false)
    var fitnessGoal: String,

    @Column(name = "fitness_score", nullable = false)
    var fitnessScore: String  // Change to String to store categories
)