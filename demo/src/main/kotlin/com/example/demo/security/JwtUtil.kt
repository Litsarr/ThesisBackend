package com.example.demo.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    // Inject the secret key from the application properties or environment variable
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    // Convert the secret key string to a SecretKey object
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())  // Convert the secret key string to a SecretKey
    }

    // Generates the token with both username and userId
    fun generateToken(username: String, userId: Long): String {
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)  // Add the userId claim
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // 10 hours expiration
            .signWith(secretKey)
            .compact()
    }

    // Validates the token based on username and expiration
    fun validateToken(token: String, username: String): Boolean {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return username == claims.subject && !claims.expiration.before(Date())
    }

    // Extracts username from the token
    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body.subject
    }

    // Extract userId from the token
    fun getUserIdFromToken(token: String): Long {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["userId"] as Long
    }
}
