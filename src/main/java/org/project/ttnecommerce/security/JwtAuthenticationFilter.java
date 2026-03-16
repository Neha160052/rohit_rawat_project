package org.project.ttnecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.repository.BlacklistedTokenRepository;
import org.project.ttnecommerce.security.Utils.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Step 1: Authorization header check
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            // Step 2: blacklist check
            if (blacklistedTokenRepository.existsByToken(token)) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                response.getWriter().write("""
                {
                    "error": "Unauthorized",
                    "message": "Token has been revoked"
                }
                """);

                return;
            }

            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Step 3: authenticate user if not already authenticated
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            CustomUserDetails userDetails =
                    (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (jwtUtils.isTokenValid(token, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/auth/login") ||
                path.startsWith("/auth/logout") ||
                path.startsWith("/auth/refresh") ||
                path.startsWith("/api/customers/register") ||
                path.startsWith("/api/customers/activate-customer") ||
                path.startsWith("/api/customers/resend-activation-link") ||
                path.startsWith("/api/forgot-password") ||
                path.startsWith("/api/reset-password") ||
                path.startsWith("/api/sellers/register") ||
                path.startsWith("/error");
    }
}