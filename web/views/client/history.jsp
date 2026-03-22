<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">History</h3>
            <p class="text-muted mb-0">Các chuyến đã hoàn thành hoặc huỷ.</p>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-white fw-semibold">Trip History</div>
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Code</th>
                        <th>Car</th>
                        <th>Start</th>
                        <th>End</th>
                        <th>Total</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="h" items="${history}">
                    <tr>
                        <td>${h.contractCode}</td>
                        <td>${h.carName}</td>
                        <td>${h.startDateTime}</td>
                        <td>${h.endDateTime}</td>
                        <td>${h.totalAmount}</td>
                        <td><span class="badge bg-secondary">${h.detailStatus}</span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty history}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-3">No history yet.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
