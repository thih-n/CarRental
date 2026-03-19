<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết booking - Admin</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/bookings"><i class="bi bi-calendar2-check me-2"></i>Quản lý booking</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/locations"><i class="bi bi-geo-alt me-2"></i>Địa điểm nhận/trả</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reviews"><i class="bi bi-star me-2"></i>Đánh giá khách hàng</a>
<a class="nav-link" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-cash-coin me-2"></i>Doanh thu</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>        </nav>

        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content-area">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Chi tiết booking</h5>
                <small class="text-muted">Thông tin chi tiết của đơn đặt xe</small>
            </div>
            <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-dark">
                <i class="bi bi-arrow-left me-1"></i> Quay lại
            </a>
        </div>

        <div class="dashboard-body">
            <c:if test="${not empty booking}">
                <div class="row">
                    <div class="col-md-6">
                        <div class="card mb-4">
                            <div class="card-header bg-white fw-semibold">
                                <i class="bi bi-person me-2"></i>Thông tin khách hàng
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless mb-0">
                                    <tr>
                                        <td class="text-muted" style="width: 140px;">Mã hợp đồng:</td>
                                        <td class="fw-semibold">${booking.contractCode}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Tên khách hàng:</td>
                                        <td>${booking.customerName}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Số điện thoại:</td>
                                        <td>${booking.phoneNumber}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Email:</td>
                                        <td>${booking.email}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card mb-4">
                            <div class="card-header bg-white fw-semibold">
                                <i class="bi bi-car-front me-2"></i>Thông tin xe
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless mb-0">
                                    <tr>
                                        <td class="text-muted" style="width: 140px;">Tên xe:</td>
                                        <td>${booking.carName}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Biển số:</td>
                                        <td>${booking.plateNumber}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="card mb-4">
                            <div class="card-header bg-white fw-semibold">
                                <i class="bi bi-calendar me-2"></i>Thời gian thuê
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless mb-0">
                                    <tr>
                                        <td class="text-muted" style="width: 140px;">Ngày nhận xe:</td>
                                        <td>${booking.startDate}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Ngày trả xe:</td>
                                        <td>${booking.endDate}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card mb-4">
                            <div class="card-header bg-white fw-semibold">
                                <i class="bi bi-geo-alt me-2"></i>Địa điểm
                            </div>
                            <div class="card-body">
                                <table class="table table-borderless mb-0">
                                    <tr>
                                        <td class="text-muted" style="width: 140px;">Điểm nhận xe:</td>
                                        <td>${booking.pickupLocation}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-muted">Điểm trả xe:</td>
                                        <td>${booking.returnLocation}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card mb-4">
                    <div class="card-header bg-white fw-semibold">
                        <i class="bi bi-credit-card me-2"></i>Thanh toán
                    </div>
                    <div class="card-body">
                        <table class="table table-borderless mb-0">
                            <tr>
                                <td class="text-muted" style="width: 140px;">Tổng tiền:</td>
                                <td class="fw-bold text-success">${booking.totalAmount}đ</td>
                            </tr>
                            <tr>
                                <td class="text-muted">Trạng thái:</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${booking.statusName == 'Chờ duyệt'}">
                                            <span class="badge bg-warning text-dark">${booking.statusName}</span>
                                        </c:when>
                                        <c:when test="${booking.statusName == 'Đã duyệt'}">
                                            <span class="badge bg-success">${booking.statusName}</span>
                                        </c:when>
                                        <c:when test="${booking.statusName == 'Đang thuê'}">
                                            <span class="badge bg-primary">${booking.statusName}</span>
                                        </c:when>
                                        <c:when test="${booking.statusName == 'Hoàn thành'}">
                                            <span class="badge bg-info">${booking.statusName}</span>
                                        </c:when>
                                        <c:when test="${booking.statusName == 'Đã hủy'}">
                                            <span class="badge bg-danger">${booking.statusName}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${booking.statusName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                <c:if test="${booking.statusName == 'Chờ duyệt'}">
                    <div class="d-flex gap-2">
                        <form action="${pageContext.request.contextPath}/admin/bookings" method="post">
                            <input type="hidden" name="action" value="approve">
                            <input type="hidden" name="bookingID" value="${booking.bookingID}">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle me-1"></i> Duyệt
                            </button>
                        </form>
                        <form action="${pageContext.request.contextPath}/admin/bookings" method="post">
                            <input type="hidden" name="action" value="reject">
                            <input type="hidden" name="bookingID" value="${booking.bookingID}">
                            <button type="submit" class="btn btn-danger">
                                <i class="bi bi-x-circle me-1"></i> Từ chối
                            </button>
                        </form>
                    </div>
                </c:if>
            </c:if>

            <c:if test="${empty booking}">
                <div class="alert alert-warning">Không tìm thấy thông tin booking.</div>
            </c:if>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>