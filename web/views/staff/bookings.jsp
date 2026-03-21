<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff - Booking Execution</title>
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
                <h5 class="mb-0">Booking Execution</h5>
                <small class="text-muted">Xử lý chuyến xe đã đặt: nhận xe, trả xe và cập nhật trạng thái.</small>
            </div>
        </div>
        <div class="content-body">
            <c:if test="${param.started != null}">
                <div class="alert alert-success">Trip started successfully.</div>
            </c:if>
            <c:if test="${param.completed != null}">
                <div class="alert alert-success">Trip completed successfully.</div>
            </c:if>
            <c:if test="${param.error == 'return_condition_required'}">
                <div class="alert alert-danger">Vui lòng nhập ghi chú tình trạng xe khi hoàn tất.</div>
            </c:if>
            <c:if test="${param.error == 'invalid_state'}">
                <div class="alert alert-danger">Trạng thái chuyến không hợp lệ.</div>
            </c:if>
            <c:if test="${param.error == 'kyc_required'}">
                <div class="alert alert-danger">Vui lòng nhập CCCD và đủ URL ảnh trước khi bắt đầu.</div>
            </c:if>
            <c:if test="${param.error == 'kyc_update_failed'}">
                <div class="alert alert-danger">Cập nhật KYC thất bại. Vui lòng thử lại.</div>
            </c:if>
            <c:if test="${param.error == 'detail_update_failed'}">
                <div class="alert alert-danger">Không thể cập nhật trạng thái chuyến. Vui lòng thử lại.</div>
            </c:if>
            <c:if test="${param.error == 'schedule_update_failed'}">
                <div class="alert alert-danger">Không thể cập nhật lịch xe. Vui lòng thử lại.</div>
            </c:if>
            <c:if test="${param.error != null && param.error != 'return_condition_required' && param.error != 'invalid_state' && param.error != 'kyc_required' && param.error != 'kyc_update_failed' && param.error != 'detail_update_failed' && param.error != 'schedule_update_failed'}">
                <div class="alert alert-danger">Action failed. Please check trip status.</div>
            </c:if>

            <!-- Search and Filter Form -->
            <form method="get" action="${pageContext.request.contextPath}/staff/bookings" class="card shadow-sm mb-4">
                <div class="card-body">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label for="search" class="form-label">Search by Contract Code</label>
                            <input type="text" class="form-control" id="search" name="search"
                                   placeholder="Enter contract code..."
                                   value="${currentSearch}">
                        </div>
                        <div class="col-md-4">
                            <label for="status" class="form-label">Filter by Status</label>
                            <select class="form-select" id="status" name="status">
                                <option value="Booked" ${currentStatus == 'Booked' ? 'selected' : ''}>Booked</option>
                                <option value="InUse" ${currentStatus == 'InUse' ? 'selected' : ''}>In Use</option>
                                <option value="Completed" ${currentStatus == 'Completed' ? 'selected' : ''}>Completed</option>
                                <option value="Cancelled" ${currentStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>All</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">Filter</button>
                                <a href="${pageContext.request.contextPath}/staff/bookings" class="btn btn-outline-secondary">Clear</a>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Booking list</div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Contract</th>
                                <th>Customer</th>
                                <th>Car</th>
                                <th>Driver</th>
                                <th>Time</th>
                                <th>Status</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="b" items="${bookings}">
                            <tr>
                                <td>${b.contractCode}</td>
                                <td>${b.customerName}</td>
                                <td>${b.carName}</td>
                                <td>${b.driverName}</td>
                                <td>${b.startDateTime} → ${b.endDateTime}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${b.detailStatus == 'Booked'}">
                                            <span class="badge bg-primary">Booked</span>
                                        </c:when>
                                        <c:when test="${b.detailStatus == 'InUse'}">
                                            <span class="badge bg-warning text-dark">In Use</span>
                                        </c:when>
                                        <c:when test="${b.detailStatus == 'Completed'}">
                                            <span class="badge bg-success">Completed</span>
                                        </c:when>
                                        <c:when test="${b.detailStatus == 'Cancelled'}">
                                            <span class="badge bg-danger">Cancelled</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${b.detailStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <div class="d-flex flex-column gap-2 align-items-end">
                                        <a class="btn btn-sm btn-outline-dark" href="${pageContext.request.contextPath}/staff/booking/detail?id=${b.contractID}">Detail</a>

                                        <%-- Start trip form: only show when status is Booked --%>
                                        <c:if test="${b.detailStatus == 'Booked'}">
                                            <form action="${pageContext.request.contextPath}/staff/trip/start" method="post" class="d-flex flex-column gap-2">
                                                <input type="hidden" name="contractID" value="${b.contractID}">
                                                <input type="hidden" name="customerID" value="${b.customerID}">

                                                <%-- Show KYC fields only when no driver --%>
                                                <c:if test="${empty b.driverID}">
                                                    <input type="text" name="identityCardNumber" class="form-control form-control-sm" placeholder="CCCD/CMND" required />
                                                    <input type="url" name="licenseImageFrontUrl" class="form-control form-control-sm" placeholder="URL ảnh mặt trước" required>
                                                    <input type="url" name="licenseImageBackUrl" class="form-control form-control-sm" placeholder="URL ảnh mặt sau" required>
                                                </c:if>

                                                <button class="btn btn-sm btn-warning" type="submit">
                                                    <c:if test="${empty b.driverID}">Update KYC + Start</c:if>
                                                    <c:if test="${not empty b.driverID}">Start Trip</c:if>
                                                </button>
                                            </form>
                                        </c:if>

                                        <%-- Complete trip form: only show when status is InUse --%>
                                        <c:if test="${b.detailStatus == 'InUse'}">
                                            <button class="btn btn-sm btn-success" type="button" data-bs-toggle="modal" data-bs-target="#completeModal${b.contractID}">Complete</button>
                                            <div class="modal fade" id="completeModal${b.contractID}" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">Hoàn tất chuyến ${b.contractCode}</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <form action="${pageContext.request.contextPath}/staff/trip/complete" method="post">
                                                            <div class="modal-body">
                                                                <input type="hidden" name="contractID" value="${b.contractID}">
                                                                <input type="hidden" name="driverID" value="${b.driverID}">
                                                                <input type="hidden" name="carID" value="${b.carID}">

                                                                <div class="mb-3">
                                                                    <label class="form-label">Địa điểm trả xe</label>
                                                                    <select name="dropOffLocationID" class="form-select" required>
                                                                        <option value="">Chọn địa điểm</option>
                                                                        <c:forEach var="loc" items="${locations}">
                                                                            <option value="${loc.locationID}" ${b.dropOffLocationID == loc.locationID ? 'selected' : ''}>${loc.locationName}</option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </div>

                                                                <div class="mb-3">
                                                                    <label class="form-label">Ghi chú nội bộ (tuỳ chọn)</label>
                                                                    <textarea name="staffNote" class="form-control" rows="2"></textarea>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label class="form-label">Ghi chú tình trạng xe</label>
                                                                    <input type="text" name="returnCondition" class="form-control" placeholder="Tình trạng xe khi trả" required />
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                                                <button type="submit" class="btn btn-success">Xác nhận</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <%-- Cancel button: show for Booked status --%>
                                        <c:if test="${b.detailStatus == 'Booked'}">
                                            <button class="btn btn-sm btn-danger" type="button" data-bs-toggle="modal" data-bs-target="#cancelModal${b.contractID}">Hủy chuyến</button>
                                            <div class="modal fade" id="cancelModal${b.contractID}" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">Hủy chuyến ${b.contractCode}</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <form action="${pageContext.request.contextPath}/staff/booking/cancel" method="post">
                                                            <div class="modal-body">
                                                                <input type="hidden" name="contractID" value="${b.contractID}">
                                                                <div class="mb-3">
                                                                    <label class="form-label">Lý do hủy:</label>
                                                                    <textarea class="form-control" name="cancelReason" rows="3" required></textarea>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                                                                <button type="submit" class="btn btn-danger">Xác nhận hủy</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty bookings}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-3">No bookings to process.</td>
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
</body>
</html>
