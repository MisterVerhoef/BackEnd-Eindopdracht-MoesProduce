package novi.backend.eindopdrachtmoesproducebackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
        logger.info("Received Authorization header: " + requestTokenHeader);

        String username = null;
        String jwtToken = extractTokenFromHeader(requestTokenHeader);

        if (jwtToken != null) {
            username = extractUsernameFromToken(jwtToken, request);
        }

        authenticateUserIfValid(request, username, jwtToken);
        filterChain.doFilter(request, response);
    }
    private String extractTokenFromHeader(String requestTokenHeader) {
        if (requestTokenHeader != null && requestTokenHeader.startsWith(BEARER_PREFIX)) {
            return requestTokenHeader.substring(BEARER_PREFIX_LENGTH);
        } else {
            logger.warn("JWT Token does not begin with Bearer String" + requestTokenHeader);
            return null;
        }
    }

    private String extractUsernameFromToken(String jwtToken, HttpServletRequest request) {
        try {
            return jwtUtil.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT Token is expired", e);
            request.setAttribute("expired", true);
        } catch (io.jsonwebtoken.SignatureException e) {
            logger.error("Invalid JWT signature", e);
            request.setAttribute("invalid", "signature");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.error("Invalid JWT token format", e);
            request.setAttribute("invalid", "format");
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.error("Unsupported JWT token", e);
            request.setAttribute("invalid", "unsupported");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty", e);
            request.setAttribute("invalid", "empty");
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation", e);
            request.setAttribute("error", "jwt_processing");
        }
        return null;
    }

    private void authenticateUserIfValid(HttpServletRequest request, String username, String jwtToken) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails, request);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }
}