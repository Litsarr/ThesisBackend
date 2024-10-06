package com.example.demo.security


import com.example.demo.service.UserAccountService
import com.example.demo.service.UserDetailsService
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory

@Component
class JwtRequestFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService  // Inject UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestURI = request.requestURI
        println("Processing request for URI: $requestURI")

        // Skip JWT validation for /register and /login endpoints
        if (requestURI.contains("/api/auth/register") || requestURI.contains("/api/auth/login")) {
            println("Skipping JWT validation for: $requestURI")
            filterChain.doFilter(request, response)
            return
        }

        val requestTokenHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwtToken: String? = null

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7)
            println("JWT Token found: $jwtToken")
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken)
                println("Extracted Username from token: $username")
            } catch (e: Exception) {
                println("Unable to get JWT Token: ${e.message}")
            }
        } else {
            println("JWT Token does not begin with Bearer String")
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            if (jwtUtil.validateToken(jwtToken!!, userDetails.username)) {
                println("JWT Token validated successfully for user: $username")
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            } else {
                println("JWT Token validation failed for user: $username")
            }
        }

        filterChain.doFilter(request, response)
    }
}


