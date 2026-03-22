<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết hoàn tiền - Admin</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>         </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content-area">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </a>
            </div>
            <div class="text-end">
                <div class="fw-semibold">Admin</div>
            </div>
        </div>

        <div class="p-4">
            <h4 class="mb-4">Chi tiết hoàn tiền #${refundDetail.paymentID}</h4>
            
            <c:if test="${param.success}">
                <div class="alert alert-success">Cập nhật trạng thái thành công!</div>
            </c:if>
            <c:if test="${param.error}">
                <div class="alert alert-danger">Cập nhật trạng thái thất bại!</div>
            </c:if>

            <div class="row g-4">
                <!-- Thông tin thanh toán -->
                <div class="col-md-6">
                    <div class="card">
                        <div class="card-header fw-semibold">Thông tin thanh toán hoàn tiền</div>
                        <div class="card-body">
                            <table class="table table-borderless">
                                <tr>
                                    <td class="text-muted">Mã thanh toán:</td>
                                    <td><strong>#${refundDetail.paymentID}</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Số tiền hoàn:</td>
                                    <td><strong class="text-danger">-<fmt:formatNumber value="${refundDetail.amount}" pattern="#,###"/>đ</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Ngày tạo:</td>
                                    <td><fmt:formatDate value="${refundDetail.paymentDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Phương thức:</td>
                                    <td>${refundDetail.paymentMethod}</td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Mã giao dịch:</td>
                                    <td>${refundDetail.transactionCode}</td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Trạng thái:</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${refundDetail.paymentStatus == 'Pending'}">
                                                <span class="badge bg-warning">Chờ xử lý</span>
                                            </c:when>
                                            <c:when test="${refundDetail.paymentStatus == 'Success'}">
                                                <span class="badge bg-success">Hoàn thành</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Ghi chú:</td>
                                    <td>${refundDetail.note}</td>
                                </tr>
                            </table>
                            <c:if test="${refundDetail.paymentStatus == 'Pending'}">
                                <form method="post" class="mt-3">
                                    <input type="hidden" name="action" value="completeRefund"/>
                                    <input type="hidden" name="paymentID" value="${refundDetail.paymentID}"/>
                                    <button type="submit" class="btn btn-success w-100"
                                            onclick="return confirm('Xác nhận đã hoàn tiền cho khách hàng?')">
                                        <i class="bi bi-check-lg"></i> Xác nhận đã hoàn tiền
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>

                <!-- Thông tin booking/chuyến đi -->
                <div class="col-md-6">
                    <div class="card mb-4">
                        <div class="card-header fw-semibold">Thông tin chuyến đi</div>
                        <div class="card-body">
                            <table class="table table-borderless">
                                <tr>
                                    <td class="text-muted">Mã booking:</td>
                                    <td><strong>${refundDetail.bookingID}</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Ngày nhận xe:</td>
                                    <td><fmt:formatDate value="${refundDetail.pickupDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Ngày trả xe:</td>
                                    <td><fmt:formatDate value="${refundDetail.returnDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Địa điểm nhận:</td>
                                    <td>${refundDetail.pickupLocation}</td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Địa điểm trả:</td>
                                    <td>${refundDetail.returnLocation}</td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Xe:</td>
                                    <td>${refundDetail.modelName} (${refundDetail.licensePlate})</td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="card mb-4">
                        <div class="card-header fw-semibold">Thông tin khách hàng</div>
                        <div class="card-body">
                            <table class="table table-borderless">
                                <tr>
                                    <td class="text-muted">Tên:</td>
                                    <td><strong>${refundDetail.customerName}</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Email:</td>
                                    <td>${refundDetail.customerEmail}</td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Số điện thoại:</td>
                                    <td>${refundDetail.customerPhone}</td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header fw-semibold">Thông tin tài xế</div>
                        <div class="card-body">
                            <table class="table table-borderless">
                                <tr>
                                    <td class="text-muted">Tên:</td>
                                    <td><strong>${refundDetail.driverName != null ? refundDetail.driverName : 'Chưa chỉ định'}</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Số điện thoại:</td>
                                    <td>${refundDetail.driverPhone != null ? refundDetail.driverPhone : '-'}</td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Thông tin thanh toán gốc -->
                <div class="col-12">
                    <div class="card">
                        <div class="card-header fw-semibold">Thông tin thanh toán gốc (Đặt cọc)</div>
                        <div class="card-body">
                            <table class="table table-borderless">
                                <tr>
                                    <td class="text-muted">Mã thanh toán cọc:</td>
                                    <td><strong>#${refundDetail.depositPaymentID}</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Số tiền đặt cọc:</td>
                                    <td><strong><fmt:formatNumber value="${refundDetail.depositAmount}" pattern="#,###"/>đ</strong></td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Trạng thái cọc:</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${refundDetail.depositStatus == 'Success'}">
                                                <span class="badge bg-success">Đã thanh toán</span>
                                            </c:when>
                                            <c:when test="${refundDetail.depositStatus == 'Pending'}">
                                                <span class="badge bg-warning">Chờ xử lý</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">${refundDetail.depositStatus}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-muted">Tổng tiền hợp đồng:</td>
                                    <td><fmt:formatNumber value="${refundDetail.contractTotal}" pattern="#,###"/>đ</td>
                                </tr>
                            </table>
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
