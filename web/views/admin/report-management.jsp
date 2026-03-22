<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Báo cáo doanh thu - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background: #f4f6f9; }
        .admin-wrapper { min-height: 100vh; }
        .sidebar {
            width: 280px;
            background: #111827;
            color: #fff;
            position: sticky;
            top: 0;
            height: 100vh;
        }
        .sidebar .brand {
            padding: 20px;
            border-bottom: 1px solid rgba(255,255,255,.1);
            font-weight: 700;
            font-size: 1.1rem;
        }
        .sidebar .nav-link {
            color: #cbd5e1;
            border-radius: 10px;
            margin: 4px 12px;
            padding: 10px 14px;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background: #1f2937;
            color: #fff;
        }
        .content-area { flex: 1; }
        .topbar {
            background: #fff;
            border-bottom: 1px solid #e5e7eb;
            padding: 14px 24px;
        }
        .dashboard-body { padding: 24px; }
        .stat-card {
            border: 0;
            border-radius: 14px;
            box-shadow: 0 4px 18px rgba(0,0,0,.06);
        }
    </style>
</head>
<body>
<div class="d-flex admin-wrapper">
    <aside class="sidebar d-flex flex-column">
        <div class="brand d-flex align-items-center gap-2">
            <i class="bi bi-speedometer2"></i>
            <span>Admin Panel</span>
        </div>

        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-house-door me-2"></i>Dashboard</a>
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/refunds"><i class="bi bi-arrow-return-left me-2"></i>Hoàn tiền</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users?type=customer"><i class="bi bi-people me-2"></i>Quản lý người dùng</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/cars"><i class="bi bi-car-front me-2"></i>Quản lý xe</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings"><i class="bi bi-calendar2-check me-2"></i>Quản lý booking</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/locations"><i class="bi bi-geo-alt me-2"></i>Địa điểm nhận/trả</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reviews"><i class="bi bi-star me-2"></i>Đánh giá khách hàng</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-cash-coin me-2"></i>Doanh thu</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>
        </nav>

        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content-area">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Báo cáo doanh thu</h5>
                <small class="text-muted">Thống kê doanh thu booking</small>
            </div>
        </div>

        <div class="dashboard-body">
            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="card-body">
                            <div class="text-muted small">Doanh thu (Booked + Completed)</div>
                            <h4 class="mb-0">
                                <c:choose>
                                    <c:when test="${empty totalRevenue}">0đ</c:when>
                                    <c:otherwise>${totalRevenue}đ</c:otherwise>
                                </c:choose>
                            </h4>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="card-body">
                            <div class="text-muted small">Số booking hoàn thành (Completed)</div>
                            <h4 class="mb-0">${completedBookings}</h4>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card">
                        <div class="card-body">
                            <div class="text-muted small">Số booking bị hủy (Cancelled)</div>
                            <h4 class="mb-0">${cancelledBookings}</h4>
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
