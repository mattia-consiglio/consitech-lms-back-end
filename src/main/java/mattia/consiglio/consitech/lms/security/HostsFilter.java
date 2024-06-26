package mattia.consiglio.consitech.lms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class HostsFilter extends OncePerRequestFilter {
    @Autowired
    private List<String> allowedHosts;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String host = request.getHeader("origin");
        System.out.println("Host: " + host);

        if (allowedHosts.contains(host)) {
            filterChain.doFilter(request, response);
        } else {
            response.getWriter().write("Host not allowed");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
