<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background: #f4f6f9; }
        .layout { min-height: 100vh; }
        .sidebar {
            width: 260px;
            background: #0f172a;
            color: #fff;
            height: 100vh;
            position: sticky;
            top: 0;
        }
        .sidebar .brand { padding: 20px; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.1); }
        .sidebar .nav-link { color: #cbd5e1; border-radius: 10px; margin: 4px 12px; padding: 10px 14px; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: #1e293b; color: #fff; }
        .content { flex: 1; }
        .topbar { background: #fff; border-bottom: 1px solid #e5e7eb; padding: 14px 24px; }
        .content-body { padding: 24px; }
    </style>
</head>
<body>
<c:choose>
    <%-- Staff layout --%>
    <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 3}">
<div class="d-flex layout">
    <aside class="sidebar d-flex flex-column">
        <div class="brand"><i class="bi bi-briefcase me-2"></i>Staff Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/staff/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/staff/bookings">Booking Execution</a>
            <div class="dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-car-front me-2"></i>Quản lý tài xế
                </a>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/staff/drivers">Quản lý tài khoản tài xế</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/staff/driver/requests">Quản lý yêu cầu tài xế</a></li>
                </ul>
            </div>
            <a class="nav-link active" href="${pageContext.request.contextPath}/profile">My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">My Profile</h5>
                <small class="text-muted">Quản lý thông tin cá nhân.</small>
            </div>
        </div>
        <div class="content-body">
    </c:when>
    <%-- Driver layout --%>
    <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 4}">
<div class="d-flex layout">
    <aside class="sidebar d-flex flex-column">
        <div class="brand"><i class="bi bi-car-front me-2"></i>Driver Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/schedule">Lịch trình</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/requests">Đơn đã gửi</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">My Profile</h5>
                <small class="text-muted">Quản lý thông tin cá nhân.</small>
            </div>
        </div>
        <div class="content-body">
    </c:when>
    <%-- Regular layout --%>
    <c:otherwise>
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">My Profile</h3>
            <p class="text-muted mb-0">Quản lý thông tin cá nhân.</p>
        </div>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/home">Back Home</a>
    </div>
    </c:otherwise>
</c:choose>

    <c:if test="${param.updated != null}">
        <div class="alert alert-success">Cập nhật thông tin cá nhân thành công.</div>
    </c:if>
    <c:if test="${param.passwordChanged != null}">
        <div class="alert alert-success">Đổi mật khẩu thành công.</div>
    </c:if>

    <div class="row g-3">
        <div class="col-lg-6">
            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Thông tin cơ bản</div>
                <div class="card-body">
                    <c:if test="${param.error == 'fullname_required'}">
                        <div class="alert alert-danger">Họ tên không được để trống.</div>
                    </c:if>
                    <c:if test="${param.error == 'update_failed'}">
                        <div class="alert alert-danger">Cập nhật hồ sơ thất bại. Vui lòng thử lại.</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/profile/update" method="post" class="row g-3">
                        <div class="col-md-12">
                            <label class="form-label">Full name</label>
                            <input type="text" class="form-control" name="fullName" value="${profile.fullName}" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input type="text" class="form-control" name="phoneNumber" value="${profile.phoneNumber}" />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Address</label>
                            <input type="text" class="form-control" name="address" value="${profile.address}" />
                        </div>
                        <div class="col-12">
                            <button class="btn btn-dark" type="submit">Update profile</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Đổi mật khẩu</div>
                <div class="card-body">
                    <c:if test="${param.error == 'invalid_new_password'}">
                        <div class="alert alert-danger">Mật khẩu mới không hợp lệ. Phải có ít nhất 8 ký tự.</div>
                    </c:if>
                    <c:if test="${param.error == 'password_confirm_not_match'}">
                        <div class="alert alert-danger">Mật khẩu xác nhận không khớp với mật khẩu mới.</div>
                    </c:if>
                    <c:if test="${param.error == 'change_password_failed'}">
                        <div class="alert alert-danger">Mật khẩu hiện tại không đúng.</div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/profile/password" method="post" class="row g-3">
                        <div class="col-md-12">
                            <label class="form-label">Mật khẩu hiện tại</label>
                            <input type="password" class="form-control" name="currentPassword" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Mật khẩu mới</label>
                            <input type="password" class="form-control" name="newPassword" required />
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Xác nhận mật khẩu mới</label>
                            <input type="password" class="form-control" name="confirmPassword" required />
                        </div>
                        <div class="col-12">
                            <button class="btn btn-dark" type="submit">Đổi mật khẩu</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

<c:choose>
    <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 3}">
        </div>
    </main>
</div>
    </c:when>
    <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 4}">
        </div>
    </main>
</div>
    </c:when>
    <c:otherwise>
</div>
<jsp:include page="/views/common/footer.jsp"/>
    </c:otherwise>
</c:choose>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
