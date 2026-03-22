<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lương tài xế - Admin</title>
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>
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
                <h5 class="mb-0">Lương tài xế</h5>
                <small class="text-muted">Thống kê lương tài xế theo chuyến</small>
            </div>
        </div>

        <div class="dashboard-body">
            <c:if test="${param.success == 'update'}">
                <div class="alert alert-success">Cập nhật lương thành công.</div>
            </c:if>
            <c:if test="${param.error == 'updateFailed'}">
                <div class="alert alert-danger">Cập nhật lương thất bại.</div>
            </c:if>
            <c:if test="${param.error == 'invalidInput'}">
                <div class="alert alert-danger">Dữ liệu không hợp lệ.</div>
            </c:if>

            <div class="row g-3 mb-4">
                <div class="col-md-12">
                    <div class="card stat-card">
                        <div class="card-body">
                            <div class="text-muted small">Tổng lương tài xế</div>
                            <h4 class="mb-0">
                                <c:choose>
                                    <c:when test="${empty totalDriverSalaries}">0đ</c:when>
                                    <c:otherwise><fmt:formatNumber value="${totalDriverSalaries}" pattern="#,###"/>đ</c:otherwise>
                                </c:choose>
                            </h4>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/admin/driver-salaries" class="row g-3">
                        <div class="col-md-3">
                            <input type="text" name="search" class="form-control" placeholder="Tìm theo tên..." value="${search}">
                        </div>
                        <div class="col-md-2">
                            <select name="month" class="form-select">
                                <option value="">-- Chọn tháng --</option>
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${month == m ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <select name="year" class="form-select">
                                <option value="">-- Chọn năm --</option>
                                <c:forEach var="y" begin="2020" end="${currentYear}">
                                    <option value="${y}" ${year == y ? 'selected' : ''}>Năm ${y}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search"></i> Tìm kiếm
                            </button>
                        </div>
                        <div class="col-md-2">
                            <a href="${pageContext.request.contextPath}/admin/driver-salaries" class="btn btn-outline-secondary w-100">
                                <i class="bi bi-arrow-counterclockwise"></i> Reset
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-lg-12">
                    <div class="card">
                        <div class="card-header bg-white fw-semibold">Danh sách lương tài xế</div>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>Tài xế</th>
                                        <th>Lương cứng</th>
                                        <th>Tỷ lệ hoa hồng</th>
                                        <th>Số chuyến</th>
                                        <th>Hoa hồng</th>
                                        <th>Tổng lương</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="salary" items="${driverSalaries}">
                                    <tr>
                                        <td>${salary.fullName}</td>
                                        <td><fmt:formatNumber value="${salary.baseSalary}" pattern="#,###"/>đ</td>
                                        <td>${salary.commissionRate}%</td>
                                        <td>${salary.tripCount}</td>
                                        <td><fmt:formatNumber value="${salary.totalSalary}" pattern="#,###"/>đ</td>
                                        <td><strong><fmt:formatNumber value="${salary.baseSalary.add(salary.totalSalary)}" pattern="#,###"/>đ</strong></td>
                                        <td>
                                            <button type="button" class="btn btn-sm btn-outline-primary" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#editModal${salary.userID}">
                                                <i class="bi bi-pencil"></i> Sửa
                                            </button>
                                            
                                            <div class="modal fade" id="editModal${salary.userID}" tabindex="-1">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">Sửa lương tài xế: ${salary.fullName}</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <form method="post" action="${pageContext.request.contextPath}/admin/driver-salaries">
                                                            <input type="hidden" name="action" value="update">
                                                            <input type="hidden" name="userID" value="${salary.userID}">
                                                            <div class="modal-body">
                                                                <div class="mb-3">
                                                                    <label class="form-label">Lương cứng (VND)</label>
                                                                    <input type="number" name="baseSalary" class="form-control" 
                                                                           value="${salary.baseSalary}" min="0" step="100000">
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label">Tỷ lệ hoa hồng (%)</label>
                                                                    <input type="number" name="commissionRate" class="form-control" 
                                                                           value="${salary.commissionRate}" min="0" max="100" step="1">
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                                <button type="submit" class="btn btn-primary">Lưu</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty driverSalaries}">
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-3">Chưa có dữ liệu lương.</td>
                                    </tr>
                                </c:if>
                                </tbody>
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
