<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff - Quản lý yêu cầu tài xế</title>
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
                <h5 class="mb-0">Quản lý yêu cầu tài xế</h5>
                <small class="text-muted">Xem và xử lý yêu cầu nghỉ phép của tài xế.</small>
            </div>
        </div>
        <div class="content-body">
            <c:if test="${param.approved != null}">
                <div class="alert alert-success">Duyệt yêu cầu thành công.</div>
            </c:if>
            <c:if test="${param.cancelledBookings != null}">
                <div class="alert alert-info">Đã hủy ${param.cancelledBookings} chuyến trong thời gian nghỉ.</div>
            </c:if>
            <c:if test="${param.rejected != null}">
                <div class="alert alert-success">Từ chối yêu cầu thành công.</div>
            </c:if>
            <c:if test="${param.error == 'action_failed'}">
                <div class="alert alert-danger">Thao tác thất bại. Vui lòng thử lại.</div>
            </c:if>

            <!-- Search and Filter Form -->
            <form method="get" action="${pageContext.request.contextPath}/staff/driver/requests" class="card shadow-sm mb-4">
                <div class="card-body">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label for="search" class="form-label">Tìm kiếm theo tên tài xế</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="Nhập tên tài xế..."
                                   value="${currentSearch}">
                        </div>
                        <div class="col-md-3">
                            <label for="status" class="form-label">Lọc theo trạng thái</label>
                            <select class="form-select" id="status" name="status">
                                <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>Tất cả</option>
                                <option value="Pending" ${currentStatus == 'Pending' ? 'selected' : ''}>Chờ duyệt</option>
                                <option value="Approved" ${currentStatus == 'Approved' ? 'selected' : ''}>Đã duyệt</option>
                                <option value="Rejected" ${currentStatus == 'Rejected' ? 'selected' : ''}>Đã từ chối</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Lọc</button>
                                <a href="${pageContext.request.contextPath}/staff/driver/requests" class="btn btn-outline-secondary">Xóa</a>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Danh sách yêu cầu nghỉ phép</div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Tài xế</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Lý do</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th class="text-end">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${requests}">
                            <tr>
                                <td>${r.driverName}</td>
                                <td>${r.leaveStart}</td>
                                <td>${r.leaveEnd}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.reason.length() > 50}">
                                            ${r.reason.substring(0, 50)}...
                                        </c:when>
                                        <c:otherwise>
                                            ${r.reason}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.status == 'Pending'}">
                                            <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                        </c:when>
                                        <c:when test="${r.status == 'Approved'}">
                                            <span class="badge bg-success">Đã duyệt</span>
                                        </c:when>
                                        <c:when test="${r.status == 'Rejected'}">
                                            <span class="badge bg-danger">Đã từ chối</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${r.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${r.createdAt}</td>
                                <td class="text-end">
                                    <c:if test="${r.status == 'Pending'}">
                                        <button class="btn btn-sm btn-success" type="button" data-bs-toggle="modal" data-bs-target="#approveModal${r.leaveID}">Duyệt</button>
                                        <button class="btn btn-sm btn-danger" type="button" data-bs-toggle="modal" data-bs-target="#rejectModal${r.leaveID}">Từ chối</button>
                                        
                                        <!-- Approve Modal -->
                                        <div class="modal fade" id="approveModal${r.leaveID}" data-driverid="${r.driverID}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title">Duyệt yêu cầu nghỉ phép</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/staff/driver/leave/approve" method="post">
                                                        <div class="modal-body">
                                                            <input type="hidden" name="leaveID" value="${r.leaveID}">
                                                            <p>Bạn có chắc chắn muốn duyệt yêu cầu nghỉ phép của tài xế <strong>${r.driverName}</strong>?</p>
                                                            <p class="text-muted">Thời gian nghỉ: ${r.leaveStart} đến ${r.leaveEnd}</p>

                                                            <div class="alert alert-info" id="leaveOngoingInfo${r.leaveID}" style="display: none;">
                                                                <i class="bi bi-info-circle me-2"></i>
                                                                <span id="leaveOngoingText${r.leaveID}"></span>
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
                                                                        <tbody id="leaveTripsBody${r.leaveID}">
                                                                            <tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>
                                                                        </tbody>
                                                                    </table>
                                                                </div>
                                                                <p class="mt-2 text-muted"><small>Số chuyến sẽ hủy: <span id="leaveCancelledCount${r.leaveID}">0</span></small></p>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                            <button type="submit" class="btn btn-success">Duyệt</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <!-- Reject Modal -->
                                        <div class="modal fade" id="rejectModal${r.leaveID}" tabindex="-1" aria-hidden="true">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title">Từ chối yêu cầu nghỉ phép</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/staff/driver/leave/reject" method="post">
                                                        <div class="modal-body">
                                                            <input type="hidden" name="leaveID" value="${r.leaveID}">
                                                            <div class="mb-3">
                                                                <label class="form-label">Lý do từ chối</label>
                                                                <textarea class="form-control" name="rejectReason" rows="3" required placeholder="Nhập lý do từ chối..."></textarea>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                                            <button type="submit" class="btn btn-danger">Từ chối</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <button class="btn btn-sm btn-outline-dark" type="button" data-bs-toggle="modal" data-bs-target="#detailModal${r.leaveID}">Chi tiết</button>
                                    
                                    <!-- Detail Modal -->
                                    <div class="modal fade" id="detailModal${r.leaveID}" tabindex="-1" aria-hidden="true">
                                        <div class="modal-dialog modal-dialog-centered">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Chi tiết yêu cầu nghỉ phép</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <ul class="list-unstyled">
                                                        <li><strong>Tài xế:</strong> ${r.driverName}</li>
                                                        <li><strong>Ngày bắt đầu:</strong> ${r.leaveStart}</li>
                                                        <li><strong>Ngày kết thúc:</strong> ${r.leaveEnd}</li>
                                                        <li><strong>Lý do:</strong> ${r.reason}</li>
                                                        <li><strong>Trạng thái:</strong> ${r.status}</li>
                                                        <li><strong>Ngày tạo:</strong> ${r.createdAt}</li>
                                                        <c:if test="${not empty r.approvedAt}">
                                                            <li><strong>Ngày duyệt:</strong> ${r.approvedAt}</li>
                                                        </c:if>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-3">Không có yêu cầu nào.</td>
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
    const approveModals = document.querySelectorAll('[id^="approveModal"]');
    approveModals.forEach(function(modal) {
        modal.addEventListener('show.bs.modal', function() {
            const leaveID = modal.id.replace('approveModal', '');
            const driverID = modal.getAttribute('data-driverid') || ''; 

            const tripsBody = document.getElementById('leaveTripsBody' + leaveID);
            const cancelledCount = document.getElementById('leaveCancelledCount' + leaveID);
            const ongoingInfo = document.getElementById('leaveOngoingInfo' + leaveID);
            const ongoingText = document.getElementById('leaveOngoingText' + leaveID);

            tripsBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>';
            cancelledCount.textContent = '0';
            ongoingInfo.style.display = 'none';

            fetch('${pageContext.request.contextPath}/staff/driver/leave/preview?leaveID=' + leaveID)
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
                            html += '<td>' + (trip.contractCode || ('#' + trip.bookingID)) + '</td>';
                            html += '<td>' + (trip.customerName || 'N/A') + '</td>';
                            html += '<td>' + (trip.startDateTime || '') + '</td>';
                            html += '<td>' + (trip.endDateTime || '') + '</td>';
                            html += '<td><span class="badge bg-secondary">' + (trip.statusName || '') + '</span></td>';
                            html += '</tr>';
                        });
                        tripsBody.innerHTML = html;
                        cancelledCount.textContent = data.cancelledTrips.length;
                    } else {
                        tripsBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không có chuyến nào cần hủy</td></tr>';
                        cancelledCount.textContent = '0';
                    }
                })
                .catch(() => {
                    tripsBody.innerHTML = '<tr><td colspan="5" class="text-danger">Lỗi tải dữ liệu</td></tr>';
                });
        });
    });
});
</script>
</body>
</html>
