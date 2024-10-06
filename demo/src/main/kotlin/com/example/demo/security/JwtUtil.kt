package com.example.demo.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)  // Generate secret key

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

    // **New Method**: Extract userId from the token
    fun getUserIdFromToken(token: String): Long {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["userId"] as Long
    }
}
