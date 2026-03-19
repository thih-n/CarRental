<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Booking Detail</title>
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
<div class="d-flex layout">
    <aside class="sidebar d-flex flex-column">
        <div class="brand"><i class="bi bi-briefcase me-2"></i>Staff Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/staff/dashboard">Dashboard</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/staff/bookings">Booking Execution</a>
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
                <h5 class="mb-0">Chi tiết chuyến ${booking.contractCode}</h5>
                <small class="text-muted">Thông tin chi tiết chuyến xe.</small>
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/staff/bookings">
                <i class="bi bi-arrow-left me-1"></i>Quay lại
            </a>
        </div>

        <div class="content-body">
            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Thông tin chuyến</div>
                <div class="card-body">
                    <div class="row g-4">
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Khách hàng</h6>
                            <p class="mb-1"><strong>Tên:</strong> ${booking.customerName}</p>
                            <p class="mb-1"><strong>SĐT:</strong> ${booking.customerPhone}</p>
                            <p class="mb-0"><strong>Email:</strong> ${booking.customerEmail}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Tài xế</h6>
                            <c:choose>
                                <c:when test="${empty booking.driverID}">
                                    <p class="mb-0 text-muted">Không có tài xế.</p>
                                </c:when>
                                <c:otherwise>
                                    <p class="mb-1"><strong>Tên:</strong> ${booking.driverName}</p>
                                    <p class="mb-1"><strong>SĐT:</strong> ${booking.driverPhone}</p>
                                    <p class="mb-0"><strong>Email:</strong> ${booking.driverEmail}</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Xe</h6>
                            <p class="mb-1"><strong>Tên xe:</strong> ${booking.carName}</p>
                            <p class="mb-0"><strong>Biển số:</strong> ${booking.licensePlate}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Thời gian</h6>
                            <p class="mb-1"><strong>Bắt đầu:</strong> ${booking.startDateTime}</p>
                            <p class="mb-0"><strong>Kết thúc:</strong> ${booking.endDateTime}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Địa điểm</h6>
                            <p class="mb-1"><strong>Nhận xe:</strong> ${booking.pickupLocation}</p>
                            <p class="mb-0"><strong>Trả xe:</strong> ${booking.dropoffLocation}</p>
                        </div>
                        <div class="col-md-6">
                            <h6 class="text-uppercase text-muted">Trạng thái</h6>
                            <p class="mb-1"><strong>Detail:</strong> ${booking.detailStatus}</p>
                            <p class="mb-0"><strong>Schedule:</strong> ${booking.scheduleStatus}</p>
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
