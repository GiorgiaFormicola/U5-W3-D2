package GiorgiaFormicola.U5_W3_D2.security;

import GiorgiaFormicola.U5_W3_D2.entities.Employee;
import GiorgiaFormicola.U5_W3_D2.exceptions.UnauthorizedException;
import GiorgiaFormicola.U5_W3_D2.services.EmployeesService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenTools tokenTools;
    private final EmployeesService employeesService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Invalid token supplied in the authorization header");
        String accessToken = authorizationHeader.replace("Bearer ", "");
        tokenTools.verifyToken(accessToken);
        UUID employeeId = this.tokenTools.extractIdFromToken(accessToken);
        Employee authenticatedEmployee = this.employeesService.findById(employeeId);
        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedEmployee, null, authenticatedEmployee.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
        /*|| (request.getMethod().equals("GET"));*/
        /*|| (request.getMethod().equals("POST") && request.getServletPath().endsWith("/employees"));*/
    }
}
