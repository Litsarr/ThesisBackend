package com.example.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "workout_routine")
data class WorkoutRoutine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne // Change to ManyToOne to allow multiple workouts for the same user
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    @JsonBackReference // Prevent circular serialization
    var user: UserProfile,

    @ManyToOne
    @JoinColumn(name = "workout_info_id", referencedColumnName = "id")
    var workoutInfo: WorkoutInfo? = null,

    @Column(name = "day_num", nullable = false)
    var dayNum: Int,

    @Column(name = "isRestDay", nullable = false)
    var isRestDay: Boolean
)


