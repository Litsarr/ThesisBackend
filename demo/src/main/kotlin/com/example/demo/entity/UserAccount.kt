package com.example.demo.entity

import com.fasterxml.jackson.annotation.JsonBackReference
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
    @JsonBackReference
    val profile: UserProfile? = null
) {
    override fun toString(): String {
        return "UserAccount(id=$id, username=$username)"
    }
}
