package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout_routine")
data class WorkoutRoutine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    var user: UserProfile,  // Reference to UserAssessment

    @Column(name = "workout_id", nullable = false)
    var workoutId: Int,

    @Column(nullable = false)
    var sets: Int,

    @Column(nullable = false)
    var weight: Double,

    @Column(nullable = false)
    var reps: Int,

    @Column(name = "day_num", nullable = false)
    var dayNum: Int,

    @Column(name = "isRestDay", nullable = false)
    var isRestDay: Boolean
)
