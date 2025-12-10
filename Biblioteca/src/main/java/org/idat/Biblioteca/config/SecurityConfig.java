package org.idat.Biblioteca.config;

import org.idat.Biblioteca.security.*;
import org.idat.Biblioteca.service.JWTService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration // Marca esta clase como clase de configuración de Spring
public class SecurityConfig {

    // Dependencias inyectadas por constructor
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTService jwtService;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // Constructor con inyección de dependencias
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JWTService jwtService,
                          CustomAccessDeniedHandler customAccessDeniedHandler,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean // Crea un bean para el filtro de autenticación JWT
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(customUserDetailsService, jwtService);
    }

    @Bean // Configura el AuthenticationManager para manejar autenticaciones
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean // Configura la cadena principal de filtros de seguridad
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF (común en APIs REST con JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // API stateless sin sesiones
                )
                .cors(cors -> cors.configurationSource(request -> {
                    // Configuración CORS para permitir peticiones desde cualquier origen
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("*")); // Permitir todos los orígenes
                    corsConfig.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); // Métodos HTTP permitidos
                    corsConfig.setAllowedHeaders(List.of("*")); // Permitir todos los headers
                    return corsConfig;
                }))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler) // Manejo de errores de acceso denegado
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // Manejo de errores de autenticación
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (acceso sin autenticación)
                        .requestMatchers("/auth/register", "/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/libros", "/libros/**").permitAll()

                        // Endpoints para usuarios autenticados (USER o ADMIN)
                        .requestMatchers("/usuarios/micuenta").hasAnyAuthority("USUARIO", "ADMIN")
                        .requestMatchers("/prestamos/misprestamos").hasAnyAuthority("USUARIO", "ADMIN")

                        // Endpoints exclusivos para administradores
                        .requestMatchers("/usuarios/**", "/libros/**", "/prestamos/**").hasAuthority("ADMIN")

                        .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
                );

        // Añade el filtro JWT antes del filtro de autenticación por usuario/contraseña
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}