package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout_classification")
data class WorkoutClassification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var name: String
)