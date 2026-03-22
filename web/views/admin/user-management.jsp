<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý người dùng - Admin</title>
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
        .nav-tabs .nav-link {
            border: none;
            color: #6b7280;
            padding: 12px 20px;
            font-weight: 500;
        }
        .nav-tabs .nav-link.active {
            color: #111827;
            border-bottom: 2px solid #111827;
            background: transparent;
        }
        .nav-tabs .nav-link:hover { border-color: transparent; }
        .search-box {
            max-width: 300px;
        }
        .filter-status {
            max-width: 150px;
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
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-2"></i>Quản lý người dùng</a>
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
                <h5 class="mb-0">Quản lý người dùng</h5>
                <small class="text-muted">Quản lý tài khoản người dùng trong hệ thống</small>
            </div>
            <c:if test="${not empty param.type && param.type != 'customer'}">
                <a class="btn btn-dark" href="${pageContext.request.contextPath}/admin/users?action=create&type=${param.type}">
                    <i class="bi bi-person-plus me-1"></i>Tạo tài khoản
                </a>
            </c:if>
        </div>

        <div class="dashboard-body">
            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Cập nhật thông tin thành công.</div>
            </c:if>
            <c:if test="${param.error == 'self_lock'}">
                <div class="alert alert-warning">Bạn không thể khóa chính mình.</div>
            </c:if>
            <c:if test="${param.error == 'admin_cannot_lock'}">
                <div class="alert alert-warning">Không thể khóa tài khoản quản trị viên.</div>
            </c:if>
            <c:if test="${param.locked == '1'}">
                <div class="alert alert-success">
                    <c:choose>
                        <c:when test="${param.type == 'driver'}">Khóa tài xế thành công.</c:when>
                        <c:when test="${param.type == 'customer'}">Khóa khách hàng thành công.</c:when>
                        <c:otherwise>Khóa tài khoản thành công.</c:otherwise>
                    </c:choose>
                    ${param.cancelledBookings > 0 ? param.cancelledBookings : '0'} đơn đã bị hủy.
                </div>
            </c:if>

            <ul class="nav nav-tabs mb-4">
                <li class="nav-item">
                    <a class="nav-link ${empty param.type || param.type == 'customer' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?type=customer">
                        <i class="bi bi-person me-1"></i>Khách hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.type == 'staff' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?type=staff">
                        <i class="bi bi-person-badge me-1"></i>Nhân viên
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.type == 'driver' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?type=driver">
                        <i class="bi bi-car-front me-1"></i>Tài xế
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${param.type == 'admin' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users?type=admin">
                        <i class="bi bi-shield-check me-1"></i>Admin
                    </a>
                </li>
            </ul>

            <!-- Search and Filter -->
            <form method="get" class="row g-3 mb-4">
                <input type="hidden" name="type" value="${param.type}">
                <div class="col-md-6">
                    <div class="input-group search-box">
                        <input type="text" name="search" class="form-control" placeholder="Tìm kiếm theo tên, email, số điện thoại..." value="${param.search}">
                        <button type="submit" class="btn btn-outline-secondary">
                            <i class="bi bi-search"></i>
                        </button>
                    </div>
                </div>
                <div class="col-md-3">
                    <select name="status" class="form-select filter-status" onchange="this.form.submit()">
                        <option value="">Tất cả trạng thái</option>
                        <option value="active" ${param.status == 'active' ? 'selected' : ''}>Hoạt động</option>
                        <option value="inactive" ${param.status == 'inactive' ? 'selected' : ''}>Khóa</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <a href="${pageContext.request.contextPath}/admin/users?type=${param.type}" class="btn btn-outline-secondary">Xóa filter</a>
                </div>
            </form>

            <div class="card">
                <div class="card-header bg-white fw-semibold">
                    <c:choose>
                        <c:when test="${empty param.type || param.type == 'customer'}">Danh sách khách hàng</c:when>
                        <c:when test="${param.type == 'staff'}">Danh sách nhân viên</c:when>
                        <c:when test="${param.type == 'driver'}">Danh sách tài xế</c:when>
                        <c:when test="${param.type == 'admin'}">Danh sách Admin</c:when>
                    </c:choose>
                    <span class="text-muted fw-normal ms-2">(${users.size()} người dùng)</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>STT</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="u" items="${users}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${u.fullName}</td>
                                <td>${u.email}</td>
                                <td>${u.phoneNumber}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.isActive}"><span class="badge bg-success">Hoạt động</span></c:when>
                                        <c:otherwise><span class="badge bg-warning text-dark">Khóa</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${u.createdAt}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/users?action=edit&id=${u.userID}&type=${param.type}" class="btn btn-sm btn-outline-dark">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <c:set var="currentUserID" value="${sessionScope.USER_SESSION.userID}" />
                                    <c:if test="${u.userID ne currentUserID}">
                                        <c:choose>
                                            <c:when test="${u.isActive}">
                                                <c:if test="${param.type == 'driver'}">
                                                    <button type="button" class="btn btn-sm btn-outline-warning" 
                                                            data-bs-toggle="modal" data-bs-target="#lockDriverModal"
                                                            data-userid="${u.userID}" data-username="${u.fullName}">
                                                        <i class="bi bi-lock"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${param.type == 'customer'}">
                                                    <button type="button" class="btn btn-sm btn-outline-warning" 
                                                            data-bs-toggle="modal" data-bs-target="#lockCustomerModal"
                                                            data-userid="${u.userID}" data-username="${u.fullName}">
                                                        <i class="bi bi-lock"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${param.type == 'admin' || param.type == 'staff'}">
                                                    <a href="${pageContext.request.contextPath}/admin/users?action=lock&id=${u.userID}&type=${param.type}" 
                                                       class="btn btn-sm btn-outline-warning" onclick="return confirm('Bạn có chắc muốn khóa tài khoản này?')">
                                                        <i class="bi bi-lock"></i>
                                                    </a>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>
                                                <c:if test="${param.type == 'driver' || param.type == 'customer' || param.type == 'admin' || param.type == 'staff'}">
                                                    <a href="${pageContext.request.contextPath}/admin/users?action=unlock&id=${u.userID}&type=${param.type}" 
                                                       class="btn btn-sm btn-outline-success" onclick="return confirm('Bạn có chắc muốn mở khóa tài khoản này?')">
                                                        <i class="bi bi-unlock"></i>
                                                    </a>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty users}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-3">Không tìm thấy người dùng nào.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Lock Driver Modal -->
<div class="modal fade" id="lockDriverModal" tabindex="-1" aria-labelledby="lockDriverModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="lockDriverModalLabel">Xác nhận khóa tài xế</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc muốn khóa tài khoản tài xế <strong id="driverName"></strong>?</p>
                
                <div id="ongoingTripInfo" class="alert alert-info" style="display: none;">
                    <i class="bi bi-info-circle me-2"></i>
                    <span id="ongoingTripText"></span>
                </div>
                
                <div id="cancelledTripsSection">
                    <h6 class="mb-3">Các chuyến sẽ bị hủy:</h6>
                    <div id="cancelledTripsList" class="table-responsive" style="max-height: 300px; overflow-y: auto;">
                        <table class="table table-sm table-bordered">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Khách hàng</th>
                                    <th>Ngày nhận xe</th>
                                    <th>Ngày trả xe</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody id="cancelledTripsBody">
                                <tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>
                            </tbody>
                        </table>
                    </div>
                    <p class="mt-2 text-muted"><small>Số chuyến sẽ hủy: <span id="cancelledCount">0</span></small></p>
                </div>
            </div>
            <div class="modal-footer">
                <form id="lockDriverForm" method="post">
                    <input type="hidden" name="action" value="lock">
                    <input type="hidden" name="userID" id="lockUserID">
                    <input type="hidden" name="type" value="driver">
                    <button type="submit" class="btn btn-danger">Xác nhận khóa và hủy chuyến</button>
                </form>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    var lockDriverModal = document.getElementById('lockDriverModal');
    lockDriverModal.addEventListener('show.bs.modal', function(event) {
        var button = event.relatedTarget;
        var userID = button.getAttribute('data-userid');
        var userName = button.getAttribute('data-username');
        
        document.getElementById('driverName').textContent = userName;
        document.getElementById('lockUserID').value = userID;
        
        // Fetch bookings to be cancelled
        fetch('${pageContext.request.contextPath}/admin/users?action=getDriverBookings&userID=' + userID)
            .then(response => response.json())
            .then(data => {
                var tbody = document.getElementById('cancelledTripsBody');
                var cancelledCount = document.getElementById('cancelledCount');
                var ongoingInfo = document.getElementById('ongoingTripInfo');
                var ongoingText = document.getElementById('ongoingTripText');
                
                if (data.ongoingTrip) {
                    ongoingInfo.style.display = 'block';
                    ongoingText.textContent = 'Đơn #' + data.ongoingTrip.bookingID + ' đang chạy sẽ KHÔNG bị hủy.';
                } else {
                    ongoingInfo.style.display = 'none';
                }
                
                if (data.cancelledTrips && data.cancelledTrips.length > 0) {
                    var html = '';
                    data.cancelledTrips.forEach(function(trip) {
                        html += '<tr>';
                        html += '<td>#' + trip.bookingID + '</td>';
                        html += '<td>' + (trip.customerName || 'N/A') + '</td>';
                        html += '<td>' + trip.pickupDate + '</td>';
                        html += '<td>' + trip.returnDate + '</td>';
                        html += '<td><span class="badge bg-secondary">' + trip.statusName + '</span></td>';
                        html += '</tr>';
                    });
                    tbody.innerHTML = html;
                    cancelledCount.textContent = data.cancelledTrips.length;
                } else {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không có chuyến nào cần hủy</td></tr>';
                    cancelledCount.textContent = '0';
                }
            })
            .catch(function(error) {
                var tbody = document.getElementById('cancelledTripsBody');
                tbody.innerHTML = '<tr><td colspan="5" class="text-danger">Lỗi tải dữ liệu</td></tr>';
            });
    });
});
</script>

<!-- Lock Customer Modal -->
<div class="modal fade" id="lockCustomerModal" tabindex="-1" aria-labelledby="lockCustomerModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="lockCustomerModalLabel">Xác nhận khóa khách hàng</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc muốn khóa tài khoản khách hàng <strong id="customerName"></strong>?</p>
                
                <div id="ongoingCustomerTripInfo" class="alert alert-info" style="display: none;">
                    <i class="bi bi-info-circle me-2"></i>
                    <span id="ongoingCustomerTripText"></span>
                </div>
                
                <div id="cancelledCustomerTripsSection">
                    <h6 class="mb-3">Các chuyến sẽ bị hủy:</h6>
                    <div id="cancelledCustomerTripsList" class="table-responsive" style="max-height: 300px; overflow-y: auto;">
                        <table class="table table-sm table-bordered">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Ngày nhận xe</th>
                                    <th>Ngày trả xe</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody id="cancelledCustomerTripsBody">
                                <tr><td colspan="4" class="text-center text-muted">Đang tải...</td></tr>
                            </tbody>
                        </table>
                    </div>
                    <p class="mt-2 text-muted"><small>Số chuyến sẽ hủy: <span id="cancelledCustomerCount">0</span></small></p>
                </div>
            </div>
            <div class="modal-footer">
                <form id="lockCustomerForm" method="post">
                    <input type="hidden" name="action" value="lock">
                    <input type="hidden" name="userID" id="lockCustomerUserID">
                    <input type="hidden" name="type" value="customer">
                    <button type="submit" class="btn btn-danger">Xác nhận khóa và hủy chuyến</button>
                </form>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    var lockCustomerModal = document.getElementById('lockCustomerModal');
    lockCustomerModal.addEventListener('show.bs.modal', function(event) {
        var button = event.relatedTarget;
        var userID = button.getAttribute('data-userid');
        var userName = button.getAttribute('data-username');
        
        document.getElementById('customerName').textContent = userName;
        document.getElementById('lockCustomerUserID').value = userID;
        
        // Fetch bookings to be cancelled
        fetch('${pageContext.request.contextPath}/admin/users?action=getCustomerBookings&userID=' + userID)
            .then(response => response.json())
            .then(data => {
                var tbody = document.getElementById('cancelledCustomerTripsBody');
                var cancelledCount = document.getElementById('cancelledCustomerCount');
                var ongoingInfo = document.getElementById('ongoingCustomerTripInfo');
                var ongoingText = document.getElementById('ongoingCustomerTripText');
                
                if (data.ongoingTrip) {
                    ongoingInfo.style.display = 'block';
                    ongoingText.textContent = 'Đơn #' + data.ongoingTrip.bookingID + ' đang chạy sẽ KHÔNG bị hủy.';
                } else {
                    ongoingInfo.style.display = 'none';
                }
                
                if (data.cancelledTrips && data.cancelledTrips.length > 0) {
                    var html = '';
                    data.cancelledTrips.forEach(function(trip) {
                        html += '<tr>';
                        html += '<td>' + trip.contractCode + '</td>';
                        html += '<td>' + (trip.startDateTime || '') + '</td>';
                        html += '<td>' + (trip.endDateTime || '') + '</td>';
                        html += '<td><span class="badge bg-warning">' + (trip.statusName || 'Booked') + '</span></td>';
                        html += '</tr>';
                    });
                    tbody.innerHTML = html;
                    cancelledCount.textContent = data.cancelledTrips.length;
                } else {
                    tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">Không có chuyến nào cần hủy</td></tr>';
                    cancelledCount.textContent = '0';
                }
            })
            .catch(function(error) {
                console.error('Error:', error);
                tbody.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Lỗi khi tải dữ liệu</td></tr>';
            });
    });
});
</script>
</body>
</html>
