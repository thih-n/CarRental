<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Driver Schedule</title>
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
        
        .calendar-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .calendar-grid {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            gap: 4px;
        }
        .calendar-day-header {
            text-align: center;
            font-weight: 600;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 4px;
        }
        .calendar-day {
            min-height: 100px;
            padding: 8px;
            background: #fff;
            border-radius: 4px;
            border: 1px solid #e5e7eb;
            cursor: pointer;
            transition: all 0.2s;
        }
        .calendar-day:hover {
            border-color: #0f172a;
        }
        .calendar-day.today {
            background: #dbeafe;
            border-color: #2563eb;
        }
        .calendar-day.has-trip {
            background: #fef3c7;
            border-color: #f59e0b;
        }
        .calendar-day.has-leave {
            background: #fee2e2;
            border-color: #ef4444;
        }
        .day-number {
            font-weight: 600;
            margin-bottom: 4px;
        }
        .day-indicator {
            font-size: 10px;
            display: flex;
            align-items: center;
            gap: 2px;
        }
        
        .trip-list {
            max-height: 300px;
            overflow-y: auto;
        }
        .trip-item {
            padding: 8px;
            margin-bottom: 8px;
            border-radius: 4px;
            background: #f8f9fa;
            border-left: 3px solid #0f172a;
        }
        .trip-item.status-scheduled {
            border-left-color: #3b82f6;
        }
        .trip-item.status-pickedup {
            border-left-color: #22c55e;
        }
    </style>
</head>
<body>
<div class="d-flex layout">
    <aside class="sidebar d-flex flex-column">
        <div class="brand"><i class="bi bi-car-front me-2"></i>Driver Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/dashboard">Dashboard</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/driver/schedule">Lịch trình</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/requests">Đơn đã gửi</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Lịch trình</h5>
                <small class="text-muted">Xem lịch làm việc và đăng ký nghỉ.</small>
            </div>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#leaveModal">
                <i class="bi bi-plus-lg me-1"></i>Đăng ký nghỉ
            </button>
        </div>
        <div class="content-body">
            <c:if test="${param.success != null}">
                <div class="alert alert-success">Gửi đơn nghỉ thành công!</div>
            </c:if>
            <c:if test="${param.error == 'missing_fields'}">
                <div class="alert alert-danger">Vui lòng điền đầy đủ thông tin.</div>
            </c:if>
            <c:if test="${param.error == 'conflict'}">
                <div class="alert alert-warning">
                    Có lịch nghỉ trong khoảng thời gian này!
                    <c:if test="${not empty sessionScope.leaveConflicts}">
                        <ul class="mb-0 mt-2">
                            <c:forEach var="conflict" items="${sessionScope.leaveConflicts}">
                                <li>${conflict.leaveStart} - ${conflict.leaveEnd} (${conflict.status})</li>
                            </c:forEach>
                        </ul>
                    </c:if>
                </div>
                <c:remove var="leaveConflicts" scope="session"/>
            </c:if>

            <div class="card shadow-sm">
                <div class="card-body">
                    <div class="calendar-header">
                        <div>
                            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/driver/schedule?year=${year}&month=${month - 1}">
                                <i class="bi bi-chevron-left"></i>
                            </a>
                            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/driver/schedule?year=${year}&month=${month + 1}">
                                <i class="bi bi-chevron-right"></i>
                            </a>
                            <span class="ms-3 fw-bold">Tháng ${month} / ${year}</span>
                        </div>
                        <div>
                            <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/driver/schedule">Hôm nay</a>
                        </div>
                    </div>
                    
                    <div class="calendar-grid">
                        <div class="calendar-day-header">CN</div>
                        <div class="calendar-day-header">T2</div>
                        <div class="calendar-day-header">T3</div>
                        <div class="calendar-day-header">T4</div>
                        <div class="calendar-day-header">T5</div>
                        <div class="calendar-day-header">T6</div>
                        <div class="calendar-day-header">T7</div>
                        
                        <c:forEach begin="1" end="${firstDayOfWeek}">
                            <div class="calendar-day" style="background: transparent; border: none;"></div>
                        </c:forEach>
                        
                        <c:forEach var="day" items="${days}">
                            <div class="calendar-day ${day.today ? 'today' : ''} ${not empty day.trips ? 'has-trip' : ''}" 
                                 data-bs-toggle="modal" data-bs-target="#dayModal${day.date}">
                                <div class="day-number">${day.date}</div>
                                <div class="day-indicator">
                                    <c:if test="${not empty day.trips}">
                                        <span class="badge bg-warning text-dark"><i class="bi bi-car-front"></i> ${day.trips.size()}</span>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    
                    <div class="mt-3">
                        <small class="text-muted">
                            <span class="badge bg-warning text-dark me-2">Lịch trình</span>
                            <span class="badge bg-primary me-2">Hôm nay</span>
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<c:forEach var="day" items="${days}">
    <div class="modal fade" id="dayModal${day.date}" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Ngày ${day.date}/${month}/${year}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <c:choose>
                        <c:when test="${not empty day.trips}">
                            <div class="trip-list">
                                <c:forEach var="trip" items="${day.trips}">
                                    <div class="trip-item status-${trip.detailStatus eq 'Scheduled' ? 'scheduled' : 'pickedup'}">
                                        <div class="fw-bold">${trip.contractCode}</div>
                                        <div class="small">Khách: ${trip.customerName}</div>
                                        <div class="small">Xe: ${trip.carName}</div>
                                        <div class="small">Giờ: ${trip.startDateTime} - ${trip.endDateTime}</div>
                                        <div class="small">
                                            <c:choose>
                                                <c:when test="${trip.detailStatus == 'Scheduled'}">
                                                    <span class="badge bg-primary">Chờ</span>
                                                </c:when>
                                                <c:when test="${trip.detailStatus == 'PickedUp'}">
                                                    <span class="badge bg-success">Đang chạy</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${trip.detailStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted text-center">Không có lịch trình trong ngày này.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</c:forEach>

<div class="modal fade" id="leaveModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Đăng ký nghỉ</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/driver/schedule" method="post">
                <input type="hidden" name="action" value="create_leave">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label">Loại nghỉ</label>
                        <select class="form-select" id="leaveType" name="leaveType" onchange="toggleLeaveFields()">
                            <option value="single">Một ngày</option>
                            <option value="range">Khoảng ngày</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Ngày bắt đầu</label>
                        <input type="date" class="form-control" name="startDate" required>
                    </div>
                    <div class="mb-3" id="endDateField" style="display: none;">
                        <label class="form-label">Ngày kết thúc</label>
                        <input type="date" class="form-control" name="endDate">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Lý do nghỉ</label>
                        <textarea class="form-control" name="reason" rows="3" required></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Gửi đơn</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
function toggleLeaveFields() {
    var leaveType = document.getElementById('leaveType').value;
    var endDateField = document.getElementById('endDateField');
    
    if (leaveType === 'range') {
        endDateField.style.display = 'block';
    } else {
        endDateField.style.display = 'none';
    }
}
</script>
</body>
</html>
