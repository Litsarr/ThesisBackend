package com.example.demo.entity

import jakarta.persistence.*

@Entity
@Table(name = "workout_cardio")
data class WorkoutCardio(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(name = "desc", nullable = false)
    var description: String,

    @Column(nullable = false)
    var equipment: String,

    @ManyToOne
    @JoinColumn(name = "classification", referencedColumnName = "name")
    var classification: WorkoutClassification,  // Foreign key to classification entity

    @Column(nullable = false)
    var time: Int,  // Time for cardio in minutes

    @Column(name = "demo_url", nullable = true)
    var demoUrl: String? = null,

    @Column(name = "image_url", nullable = true)
    var imageUrl: String? = null
)
