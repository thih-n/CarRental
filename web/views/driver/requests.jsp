<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Driver - Đơn đã gửi</title>
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
        <div class="brand"><i class="bi bi-car-front me-2"></i>Driver Panel</div>
        <nav class="nav flex-column py-3">
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/dashboard">Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/driver/schedule">Lịch trình</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/driver/requests">Đơn đã gửi</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person me-2"></i>My Profile</a>
        </nav>
        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </aside>

    <main class="content">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">Đơn đã gửi</h5>
                <small class="text-muted">Xem các đơn xin nghỉ đã gửi.</small>
            </div>
        </div>
        <div class="content-body">
            <c:if test="${param.cancelled != null}">
                <div class="alert alert-success">Hủy đơn thành công.</div>
            </c:if>
            <c:if test="${param.error == 'cancel_failed'}">
                <div class="alert alert-danger">Hủy đơn thất bại.</div>
            </c:if>

            <div class="card shadow-sm">
                <div class="card-header bg-white fw-semibold">Danh sách đơn nghỉ</div>
                <div class="table-responsive">
                    <table class="table table-hover mb-0 align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>STT</th>
                                <th>Ngày bắt đầu</th>
                                <th>Ngày kết thúc</th>
                                <th>Lý do</th>
                                <th>Trạng thái</th>
                                <th>Ngày gửi</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${requests}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${r.leaveStart}</td>
                                <td>${r.leaveEnd}</td>
                                <td>${r.reason}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.status == 'Pending'}">
                                            <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                        </c:when>
                                        <c:when test="${r.status == 'Approved'}">
                                            <span class="badge bg-success">Đã duyệt</span>
                                        </c:when>
                                        <c:when test="${r.status == 'Rejected'}">
                                            <span class="badge bg-danger">Từ chối</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${r.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${r.createdAt}</td>
                                <td>
                                    <c:if test="${r.status == 'Pending'}">
                                        <form action="${pageContext.request.contextPath}/driver/schedule" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="cancel_leave">
                                            <input type="hidden" name="leaveID" value="${r.leaveID}">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc muốn hủy đơn này?')">Hủy</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty requests}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-3">Bạn chưa gửi đơn nghỉ nào.</td>
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
