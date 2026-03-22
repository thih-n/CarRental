<%-- 
    Document   : login
    Created on : Jan 8, 2026, 4:53:01 AM
    Author     : Nguyen Duc Thinh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Car Rental System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm">
                <div class="card-header bg-dark text-white text-center">
                    <h4>Welcome Back</h4>
                </div>
                <div class="card-body p-4">

                    <%-- Success Message from Registration --%>
                    <% if("registered".equals(request.getParameter("message"))) { %>
                        <div class="alert alert-success" role="alert">
                            Registration successful! You can now log in.
                        </div>
                    <% } %>

                    <%-- Success Message from Password Reset --%>
                    <% if("password_reset_success".equals(request.getParameter("message"))) { %>
                        <div class="alert alert-success" role="alert">
                            Password reset successful! Please log in with your new password.
                        </div>
                    <% } %>

                    <%-- Display Error Message for failed login --%>
                    <% if(request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger" role="alert">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>

                    <form action="login" method="POST">
                        <div class="mb-3">
                            <label for="email" class="form-label">Email Address</label>
                            <input type="email" class="form-control" id="email" name="email" 
                                   value="${not empty email ? email : rememberedEmail}" required>
                        </div>

                        <div class="mb-4">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                            <div class="text-end mt-1">
                                <a href="forgot-password" class="small text-muted">Forgot Password?</a>
                            </div>
                        </div>

                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="remember" name="remember" ${rememberChecked ? 'checked' : ''}>
                            <label class="form-check-label" for="remember">Remember me</label>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-dark btn-lg">Login</button>
                        </div>
                    </form>
                </div>
                <div class="card-footer text-center py-3">
                    Don't have an account yet? <a href="register">Register here</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>