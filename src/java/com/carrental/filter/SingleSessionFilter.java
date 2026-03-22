package com.carrental.filter;

import com.carrental.dao.UserDAO;
import com.carrental.entity.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SingleSessionFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        // Bỏ qua các file tĩnh và trang public
        if (path.endsWith(".css") || path.endsWith(".js") || 
            path.endsWith(".png") || path.endsWith(".jpg") ||
            path.endsWith(".jpeg") || path.endsWith(".gif") ||
            path.endsWith(".ico") || path.endsWith(".woff") ||
            path.endsWith(".woff2") || path.endsWith(".ttf") ||
            path.contains("/login") || path.contains("/register")) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = req.getSession(false);
        
        // Kiểm tra Single Session Logic
        if (session != null && session.getAttribute("USER_SESSION") != null) {
            User user = (User) session.getAttribute("USER_SESSION");
            String sessionToken = (String) session.getAttribute("CURRENT_TOKEN");
            
            if (sessionToken != null) {
                UserDAO dao = new UserDAO();
                String dbToken = dao.getSessionToken(user.getUserID());
                
                // Nếu DB Token khác Session Token -> Có người khác đăng nhập đè lên
                if (dbToken == null || !dbToken.equals(sessionToken)) {
                    session.invalidate();
                    res.sendRedirect(contextPath + "/login?error=session_expired");
                    return;
                }
            }
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {}
}