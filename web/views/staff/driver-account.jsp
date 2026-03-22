<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff - Quản lý tài xế</title>
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
            <a class="nav-link" href="${pageContext.request.contextPath}/staff/bookings">Booking Execution</a>
            <div class="dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-car-front me-2"></i>Quản lý tài xế
                </a>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/staff/drivers">Quản lý tài khoản tài xế</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/staff/driver/requests">Quản lý yêu cầu tài xế</a></li>
                </ul>
            </div>            <a class="nav-link" href="${pageContext.request.contextPath}/profile">My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Quản lý tài xế</h5>
                <small class="text-muted">Quản lý tài khoản và thông tin tài xế.</small>
            </div>
        </div>
        <div class="content-body">
            <c:if test="${param.updated != null}">
                <div class="alert alert-success">Cập nhật thông tin tài xế thành công.</div>
            </c:if>
            <c:if test="${param.statusChanged != null}">
                <div class="alert alert-success">Cập nhật trạng thái thành công.</div>
            </c:if>
            <c:if test="${param.cancelledBookings != null}">
                <div class="alert alert-info">Đã hủy ${param.cancelledBookings} chuyến do khóa tài xế.</div>
            </c:if>
            <c:if test="${param.error == 'update_failed'}">
                <div class="alert alert-danger">Cập nhật thất bại. Vui lòng thử lại.</div>
            </c:if>

            <!-- Search and Filter Form -->
            <form method="get" action="${pageContext.request.contextPath}/staff/drivers" class="card shadow-sm mb-4">
                <div class="card-body">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label for="search" class="form-label">Tìm kiếm theo tên</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="Nhập tên tài xế..."
                                   value="${currentSearch}">
                        </div>
                        <div class="col-md-3">
                            <label for="status" class="form-label">Lọc theo trạng thái</label>
                            <select class="form-select" id="status" name="status">
                                <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>Tất cả</option>
                                <option value="Available" ${currentStatus == 'Available' ? 'selected' : ''}>Available</option>
                                <option value="OnTrip" ${currentStatus == 'OnTrip' ? 'selected' : ''}>On Trip</option>
                                <option value="Unavailable" ${currentStatus == 'Unavailable' ? 'selected' : ''}>Unavailable</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Lọc</button>
                                <a href="${pageContext.request.contextPath}/staff/drivers" class="btn btn-outline-secondary">Xóa</a>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Danh sách tài xế</div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Tên tài xế</th>
                                <th>Số bằng lái</th>
                                <th>Ngày hết hạn</th>
                                <th>Kinh nghiệm (năm)</th>
                                <th>Đánh giá</th>
                                <th>Chuyến xe</th>
                                <th>Trạng thái</th>
                                <th>Tài khoản</th>
                                <th class="text-end">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="d" items="${drivers}">
                            <tr>
                                <td>${d.fullName}</td>
                                <td>${d.licenseNumber}</td>
                                <td>${d.licenseExpiry}</td>
                                <td>${d.experienceYears}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${d.rating != null}">
                                            <i class="bi bi-star-fill text-warning"></i> ${d.rating}
                                        </c:when>
                                        <c:otherwise>
                                            -
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${d.totalTrips}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${d.driverStatus == 'Available'}">
                                            <span class="badge bg-success">Available</span>
                                        </c:when>
                                        <c:when test="${d.driverStatus == 'OnTrip'}">
                                            <span class="badge bg-warning text-dark">On Trip</span>
                                        </c:when>
                                        <c:when test="${d.driverStatus == 'Unavailable'}">
                                            <span class="badge bg-danger">Unavailable</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${d.driverStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${d.isActive == true}">
                                            <span class="badge bg-success">Hoạt động</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">Bị khóa</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <button class="btn btn-sm btn-outline-dark" type="button" data-bs-toggle="modal" data-bs-target="#detailModal${d.driverID}">Chi tiết</button>
                                    
                                    <c:choose>
                                        <c:when test="${d.isActive == true}">
                                            <button class="btn btn-sm btn-danger" type="button"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#lockDriverModal${d.driverID}">
                                                Khóa
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/staff/driver/toggle-status" method="post" style="display:inline;">
                                                <input type="hidden" name="driverID" value="${d.driverID}">
                                                <input type="hidden" name="lock" value="false">
                                                <button type="submit" class="btn btn-sm btn-success">Mở khóa</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>

                                    <!-- Lock Driver Modal -->
                                    <div class="modal fade" id="lockDriverModal${d.driverID}" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog modal-lg">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Xác nhận khóa tài xế</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Bạn có chắc muốn khóa tài khoản tài xế <strong>${d.fullName}</strong>?</p>
                                                    <div class="alert alert-info" id="ongoingTripInfo${d.driverID}" style="display: none;">
                                                        <i class="bi bi-info-circle me-2"></i>
                                                        <span id="ongoingTripText${d.driverID}"></span>
                                                    </div>
                                                    <div>
                                                        <h6 class="mb-3">Các chuyến sẽ bị hủy:</h6>
                                                        <div class="table-responsive" style="max-height: 300px; overflow-y: auto;">
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
                                                                <tbody id="cancelledTripsBody${d.driverID}">
                                                                    <tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                        <p class="mt-2 text-muted"><small>Số chuyến sẽ hủy: <span id="cancelledCount${d.driverID}">0</span></small></p>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <form action="${pageContext.request.contextPath}/staff/driver/toggle-status" method="post">
                                                        <input type="hidden" name="driverID" value="${d.driverID}">
                                                        <input type="hidden" name="lock" value="true">
                                                        <button type="submit" class="btn btn-danger">Xác nhận khóa và hủy chuyến</button>
                                                    </form>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Detail Modal -->
                                    <div class="modal fade" id="detailModal${d.driverID}" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Chi tiết tài xế: ${d.fullName}</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <ul class="list-unstyled">
                                                        <li><strong>Tên:</strong> ${d.fullName}</li>
                                                        <li><strong>Số bằng lái:</strong> ${d.licenseNumber}</li>
                                                        <li><strong>Ngày hết hạn bằng:</strong> ${d.licenseExpiry}</li>
                                                        <li><strong>Kinh nghiệm:</strong> ${d.experienceYears} năm</li>
                                                        <li><strong>Đánh giá:</strong> ${d.rating}</li>
                                                        <li><strong>Tổng chuyến:</strong> ${d.totalTrips}</li>
                                                        <li><strong>Trạng thái:</strong> ${d.driverStatus}</li>
                                                        <li><strong>Giá theo giờ:</strong> ${d.hourlyRate}</li>
                                                        <li><strong>Giá theo ngày:</strong> ${d.dailyRate}</li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty drivers}">
                            <tr>
                                <td colspan="9" class="text-center text-muted py-3">Không có tài xế nào.</td>
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
document.addEventListener('DOMContentLoaded', function() {
    const modals = document.querySelectorAll('[id^="lockDriverModal"]');
    modals.forEach(function(modal) {
        modal.addEventListener('show.bs.modal', function() {
            const driverID = modal.id.replace('lockDriverModal', '');
            const tbody = document.getElementById('cancelledTripsBody' + driverID);
            const cancelledCount = document.getElementById('cancelledCount' + driverID);
            const ongoingInfo = document.getElementById('ongoingTripInfo' + driverID);
            const ongoingText = document.getElementById('ongoingTripText' + driverID);

            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>';
            cancelledCount.textContent = '0';
            ongoingInfo.style.display = 'none';

            fetch('${pageContext.request.contextPath}/admin/users?action=getDriverBookings&userID=' + driverID)
                .then(response => response.json())
                .then(data => {
                    if (data.ongoingTrip) {
                        ongoingInfo.style.display = 'block';
                        ongoingText.textContent = 'Đơn #' + data.ongoingTrip.bookingID + ' đang chạy sẽ KHÔNG bị hủy.';
                    } else {
                        ongoingInfo.style.display = 'none';
                    }

                    if (data.cancelledTrips && data.cancelledTrips.length > 0) {
                        let html = '';
                        data.cancelledTrips.forEach(trip => {
                            html += '<tr>';
                            html += '<td>#' + trip.bookingID + '</td>';
                            html += '<td>' + (trip.customerName || 'N/A') + '</td>';
                            html += '<td>' + (trip.pickupDate || '') + '</td>';
                            html += '<td>' + (trip.returnDate || '') + '</td>';
                            html += '<td><span class="badge bg-secondary">' + (trip.statusName || '') + '</span></td>';
                            html += '</tr>';
                        });
                        tbody.innerHTML = html;
                        cancelledCount.textContent = data.cancelledTrips.length;
                    } else {
                        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không có chuyến nào cần hủy</td></tr>';
                        cancelledCount.textContent = '0';
                    }
                })
                .catch(() => {
                    tbody.innerHTML = '<tr><td colspan="5" class="text-danger">Lỗi tải dữ liệu</td></tr>';
                });
        });
    });
});
</script>
</body>
</html>
