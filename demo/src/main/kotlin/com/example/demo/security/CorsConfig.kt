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
                    .allowedOrigins("https://hammerhead-app-9jxwf.ondigitalocean.app")  // Allow only your deployed backend's domain
                    .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow these HTTP methods
                    .allowedHeaders("*")  // Allow all headers
                    .allowCredentials(true)  // Allow credentials if needed
            }
        }
    }
}
