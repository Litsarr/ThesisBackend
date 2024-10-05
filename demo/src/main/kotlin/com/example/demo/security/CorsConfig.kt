package com.example.demo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")  // Allow CORS for all endpoints
                    .allowedOrigins("http://your-mobile-app-url")  // Allow only your app's domain (or "*" for all)
                    .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow these HTTP methods
                    .allowedHeaders("*")  // Allow all headers
                    .allowCredentials(true)
            }
        }
    }
}