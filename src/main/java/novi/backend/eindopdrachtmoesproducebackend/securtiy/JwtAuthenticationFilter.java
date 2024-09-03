package novi.backend.eindopdrachtmoesproducebackend.securtiy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, java.io.IOException {

        // Haal de "Authorization" header op uit het verzoek
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Controleer of de header niet null is en begint met "Bearer "
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Haal het JWT-token op door de "Bearer " prefix te verwijderen
            jwtToken = requestTokenHeader.substring(7);
            try {
                // Extraheer de gebruikersnaam uit het JWT-token
                username = jwtUtil.extractUsername(jwtToken);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token is verlopen");
            } catch (IllegalArgumentException e) {
                System.out.println("Kan JWT Token niet verkrijgen");
            }
        } else {
            logger.warn("JWT Token begint niet met een Bearer String");
        }
        
        // Controleer of de gebruikersnaam niet null is en er geen authenticatie is ingesteld in de SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Laad de gebruikersdetails op basis van de gebruikersnaam
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Valideer het JWT-token
            if (jwtUtil.validateToken(jwtToken, userDetails)) {

                // Maak een UsernamePasswordAuthenticationToken aan met de gebruikersdetails en autoriteiten
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Stel de authenticatie in de SecurityContext
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Ga verder met de filterketen
        filterChain.doFilter(request, response);
    }
}