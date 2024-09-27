package mattia.consiglio.consitech.lms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component

public class HostsFilter extends OncePerRequestFilter {
    private final List<String> allowedHosts;

    public HostsFilter(@Qualifier("allowedHosts") List<String> allowedHosts) {
        this.allowedHosts = allowedHosts;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String host = request.getRemoteHost();
        String origin = request.getHeader("origin");
        String referer = request.getHeader("referer");

        System.out.println("Request URL " + request.getRequestURI());
        System.out.println("Request remote host " + host);
        System.out.println("Request origin: " + origin);
        System.out.println("Request referrer: " + referer);

        if (origin != null) {
            origin = origin.toLowerCase();
        }
        if (referer != null) {
            referer = referer.toLowerCase();
            referer = referer.replaceAll("/$", "");
        }

        if (allowedHosts.contains(origin) || allowedHosts.contains(referer)) {
            filterChain.doFilter(request, response);
        } else {
            response.getWriter().write("Host not allowed");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
