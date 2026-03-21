<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff Dashboard</title>
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
        .sidebar .dropdown-menu { background: #1e293b; border: none; padding: 0; }
        .sidebar .dropdown-item { color: #cbd5e1; padding: 10px 14px 10px 30px; border-radius: 0; }
        .sidebar .dropdown-item:hover { background: #334155; color: #fff; }
        .sidebar .dropdown-toggle::after { float: right; margin-top: 8px; }
        .content { flex: 1; }
        .topbar { background: #fff; border-bottom: 1px solid #e5e7eb; padding: 14px 24px; }
        .content-body { padding: 24px; }
    </style>
</head>
<body>
<div class="d-flex layout">
    <aside class="sidebar d-flex flex-column">
        <div class="brand"><i class="bi bi-briefcase me-2"></i>Staff Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link active" href="${pageContext.request.contextPath}/staff/dashboard">Dashboard</a>
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
            
            <a class="nav-link" href="${pageContext.request.contextPath}/profile">My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Staff Dashboard</h5>
                <small class="text-muted">Quản lý hợp đồng và vận hành chuyến xe.</small>
            </div>
        </div>
        <div class="content-body">
            <div class="row g-3">
                <div class="col-md-6">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">Booking Execution</h5>
                            <p class="text-muted">Xử lý các chuyến đang chờ nhận/trả xe.</p>
                            <a class="btn btn-dark" href="${pageContext.request.contextPath}/staff/bookings">Open list</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">Quản lý tài xế</h5>
                            <p class="text-muted">Quản lý tài khoản và yêu cầu của tài xế.</p>
                            <a class="btn btn-dark" href="${pageContext.request.contextPath}/staff/drivers">Open list</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">Profile</h5>
                            <p class="text-muted">Cập nhật thông tin cá nhân và mật khẩu.</p>
                            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/profile">Update profile</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
