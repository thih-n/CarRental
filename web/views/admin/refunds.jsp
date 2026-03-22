<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý hoàn tiền - Admin</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/refunds"><i class="bi bi-arrow-return-left me-2"></i>Hoàn tiền</a>
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
                <h5 class="mb-0">Quản lý hoàn tiền</h5>
            </div>
            <div class="text-end">
                <div class="fw-semibold">Admin</div>
            </div>
        </div>

        <div class="p-4">
            <c:if test="${param.success}">
                <div class="alert alert-success">Cập nhật trạng thái thành công!</div>
            </c:if>

            <div class="card">
                <div class="card-header bg-white">
                    <form method="get" class="d-flex gap-2">
                        <select name="status" class="form-select" style="width: 150px;">
                            <option value="all">Tất cả trạng thái</option>
                            <option value="Pending" ${refundStatus == 'Pending' ? 'selected' : ''}>Chờ xử lý</option>
                            <option value="Completed" ${refundStatus == 'Completed' ? 'selected' : ''}>Hoàn thành</option>
                        </select>
                        <input type="text" name="search" class="form-control" 
                               placeholder="Tìm theo tên, mã booking..." value="${refundSearch}">
                        <button type="submit" class="btn btn-primary">Lọc</button>
                        <a href="${pageContext.request.contextPath}/admin/refunds" class="btn btn-outline-secondary">Reset</a>
                    </form>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã thanh toán</th>
                                    <th>Khách hàng</th>
                                    <th>Mã booking</th>
                                    <th>Tài xế</th>
                                    <th>Ngày nhận xe</th>
                                    <th>Số tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${refundList}">
                                    <tr>
                                        <td>#${r.paymentID}</td>
                                        <td>${r.customerName}</td>
                                        <td>${r.bookingID != null ? r.bookingID : '-'}</td>
                                        <td>${r.driverName != null ? r.driverName : '-'}</td>
                                        <td><fmt:formatDate value="${r.pickupDate}" pattern="dd/MM/yyyy"/></td>
                                        <td><fmt:formatNumber value="${r.amount}" pattern="#,###"/>đ</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.paymentStatus == 'Pending'}">
                                                    <span class="badge bg-warning">Chờ xử lý</span>
                                                </c:when>
                                                <c:when test="${r.paymentStatus == 'Completed'}">
                                                    <span class="badge bg-success">Hoàn thành</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${r.paymentStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><fmt:formatDate value="${r.paymentDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/refund-detail?paymentID=${r.paymentID}" 
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="bi bi-eye"></i> Chi tiết
                                            </a>
                                            <c:if test="${r.paymentStatus == 'Pending'}">
                                                <form method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="completeRefund"/>
                                                    <input type="hidden" name="paymentID" value="${r.paymentID}"/>
                                                    <button type="submit" class="btn btn-sm btn-success"
                                                            onclick="return confirm('Xác nhận đã hoàn tiền?')">
                                                        <i class="bi bi-check-lg"></i> Hoàn thành
                                                    </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty refundList}">
                                    <tr>
                                        <td colspan="9" class="text-center text-muted py-3">Không có dữ liệu hoàn tiền.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
