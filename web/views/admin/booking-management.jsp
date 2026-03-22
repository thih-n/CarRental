<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý booking - Admin</title>
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
                <h5 class="mb-0">Quản lý booking</h5>
                <small class="text-muted">Quản lý và theo dõi các đơn đặt xe</small>
            </div>
        </div>

        <div class="dashboard-body">
            <c:if test="${param.success == 'approve'}">
                <div class="alert alert-success">Duyệt booking thành công.</div>
            </c:if>
            <c:if test="${param.success == 'reject'}">
                <div class="alert alert-success">Từ chối booking thành công.</div>
            </c:if>
            <c:if test="${param.success == 'cancel'}">
                <div class="alert alert-success">Hủy booking thành công.</div>
            </c:if>

            <!-- Search Form -->
            <form method="get" class="row g-3 mb-4">
                <div class="col-md-3">
                    <input type="text" name="contractCode" class="form-control" placeholder="Tìm theo mã hợp đồng..." value="${contractCode}">
                </div>
                <div class="col-md-4">
                    <div class="position-relative">
                        <input type="text" name="customerName" id="customerSearch" class="form-control" placeholder="Tìm theo tên khách hàng..." value="${customerName}" autocomplete="off">
                        <div id="customerSuggestions" class="dropdown-menu position-absolute w-100" style="max-height: 200px; overflow-y: auto; display: none; z-index: 1000;"></div>
                    </div>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-dark w-100">
                        <i class="bi bi-search me-1"></i> Tìm
                    </button>
                </div>
                <div class="col-md-2">
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-outline-secondary w-100">
                        <i class="bi bi-x-circle me-1"></i> Xóa filter
                    </a>
                </div>
            </form>

            <div class="card">
                <div class="card-header bg-white fw-semibold">Danh sách booking</div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>STT</th>
                                <th>Mã hợp đồng</th>
                                <th>Khách hàng</th>
                                <th>Xe</th>
                                <th>Ngày nhận</th>
                                <th>Ngày trả</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="b" items="${bookings}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${b.contractCode}</td>
                                <td>${b.customerName}</td>
                                <td>${b.carName}</td>
                                <td>${b.startDate}</td>
                                <td>${b.endDate}</td>
                                <td>${b.totalAmount}đ</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${b.statusName == 'Chờ duyệt'}">
                                            <span class="badge bg-warning text-dark">${b.statusName}</span>
                                        </c:when>
                                        <c:when test="${b.statusName == 'Đã duyệt'}">
                                            <span class="badge bg-success">${b.statusName}</span>
                                        </c:when>
                                        <c:when test="${b.statusName == 'Đang thuê'}">
                                            <span class="badge bg-primary">${b.statusName}</span>
                                        </c:when>
                                        <c:when test="${b.statusName == 'Hoàn thành'}">
                                            <span class="badge bg-info">${b.statusName}</span>
                                        </c:when>
                                        <c:when test="${b.statusName == 'Đã hủy'}">
                                            <span class="badge bg-danger">${b.statusName}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${b.statusName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/booking/detail?id=${b.bookingID}" class="btn btn-sm btn-outline-dark">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <c:if test="${b.statusName == 'Chờ duyệt'}">
                                        <form action="${pageContext.request.contextPath}/admin/bookings/approve" method="post" style="display:inline;">
                                            <input type="hidden" name="bookingID" value="${b.bookingID}">
                                            <button type="submit" class="btn btn-sm btn-success">
                                                <i class="bi bi-check"></i>
                                            </button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/admin/bookings/reject" method="post" style="display:inline;">
                                            <input type="hidden" name="bookingID" value="${b.bookingID}">
                                            <button type="submit" class="btn btn-sm btn-danger">
                                                <i class="bi bi-x"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty bookings}">
                            <tr>
                                <td colspan="9" class="text-center text-muted py-3">Chưa có booking nào.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Autocomplete for customer names
    const customerNames = [
        <c:forEach var="name" items="${customerNames}" varStatus="status">
            "${name}"<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];

    const customerInput = document.getElementById('customerSearch');
    const suggestionsBox = document.getElementById('customerSuggestions');

    customerInput.addEventListener('input', function() {
        const query = this.value.toLowerCase().trim();
        
        if (query.length === 0) {
            suggestionsBox.style.display = 'none';
            return;
        }

        const filtered = customerNames.filter(name => 
            name.toLowerCase().includes(query)
        );

        if (filtered.length > 0) {
            suggestionsBox.innerHTML = filtered.map(name => 
                '<a class="dropdown-item" href="#" onclick="selectCustomer(\'' + name.replace(/'/g, "\\'") + '\')">' + name + '</a>'
            ).join('');
            suggestionsBox.style.display = 'block';
        } else {
            suggestionsBox.style.display = 'none';
        }
    });

    function selectCustomer(name) {
        customerInput.value = name;
        suggestionsBox.style.display = 'none';
    }

    // Hide suggestions when clicking outside
    document.addEventListener('click', function(e) {
        if (!customerInput.contains(e.target) && !suggestionsBox.contains(e.target)) {
            suggestionsBox.style.display = 'none';
        }
    });
</script>
</body>
</html>
