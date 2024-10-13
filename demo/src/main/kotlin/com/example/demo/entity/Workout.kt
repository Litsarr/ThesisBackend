package com.example.demo.entity

import jakarta.persistence.*
import org.w3c.dom.Text

@Entity
@Table(name = "workout")
data class Workout(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var equipment: String,

    @ManyToOne
    @JoinColumn(name = "classification", referencedColumnName = "name")
    var classification: WorkoutClassification,  // Foreign key to classification entity

    @Column(name = "demo_url", nullable = true, length = 500)
    var demoUrl: String? = null,

    @Column(name = "image_url", nullable = true)
    var imageUrl: String? = null
)