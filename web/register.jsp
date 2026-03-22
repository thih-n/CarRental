<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% 
    java.util.Map<String, String> errors = (java.util.Map<String, String>) request.getAttribute("errors");
    if (errors == null) errors = new java.util.HashMap<>();
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - Car Rental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding-top: 40px;
            padding-bottom: 40px;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
        }
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px 15px 0 0 !important;
        }
        .is-invalid ~ .invalid-feedback {
            display: block;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header bg-transparent text-white text-center py-4">
                    <h4 class="mb-0">Tạo tài khoản</h4>
                </div>
                <div class="card-body p-4">
                    
                    <% if(request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger" role="alert">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>

                    <form action="register" method="POST">
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Họ tên <span class="text-danger">*</span></label>
                            <input type="text" class="form-control ${errors.fullName != null ? 'is-invalid' : ''}" 
                                   id="fullName" name="fullName" 
                                   value="${fullName != null ? fullName : ''}" 
                                   required minlength="2" maxlength="100">
                            <c:if test="${errors.fullName != null}">
                                <div class="invalid-feedback">${errors.fullName}</div>
                            </c:if>
                        </div>
                        
                        <div class="mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" class="form-control ${errors.email != null ? 'is-invalid' : ''}" 
                                   id="email" name="email" 
                                   value="${email != null ? email : ''}" 
                                   required>
                            <c:if test="${errors.email != null}">
                                <div class="invalid-feedback">${errors.email}</div>
                            </c:if>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="password" class="form-label">Mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" class="form-control ${errors.password != null ? 'is-invalid' : ''}" 
                                       id="password" name="password" 
                                       required minlength="6" maxlength="50">
                                <c:if test="${errors.password != null}">
                                    <div class="invalid-feedback">${errors.password}</div>
                                </c:if>
                            </div>
                            <div class="col-md-6">
                                <label for="confirmPassword" class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" class="form-control ${errors.confirmPassword != null ? 'is-invalid' : ''}" 
                                       id="confirmPassword" name="confirmPassword" 
                                       required>
                                <c:if test="${errors.confirmPassword != null}">
                                    <div class="invalid-feedback">${errors.confirmPassword}</div>
                                </c:if>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control ${errors.phone != null ? 'is-invalid' : ''}" 
                                   id="phone" name="phone" 
                                   value="${phone != null ? phone : ''}"
                                   placeholder="0123456789">
                            <c:if test="${errors.phone != null}">
                                <div class="invalid-feedback">${errors.phone}</div>
                            </c:if>
                        </div>

                        <div class="mb-4">
                            <label for="address" class="form-label">Địa chỉ</label>
                            <input type="text" class="form-control" 
                                   id="address" name="address" 
                                   value="${address != null ? address : ''}">
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">Đăng ký</button>
                        </div>
                    </form>
                </div>
                <div class="card-footer text-center py-3">
                    Đã có tài khoản? <a href="login.jsp">Đăng nhập</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
