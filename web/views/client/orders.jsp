<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">My Orders</h3>
            <p class="text-muted mb-0">Danh sách các chuyến xe đang hoạt động.</p>
        </div>
    </div>

    <c:if test="${param.success == 'reviewed'}">
        <div class="alert alert-success">Cảm ơn bạn! Đánh giá của bạn đã được ghi nhận.</div>
    </c:if>

    <!-- Search and Filter Form -->
    <form method="get" action="${pageContext.request.contextPath}/client/orders" class="card shadow-sm mb-4">
        <div class="card-body">
            <div class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label for="search" class="form-label">Search by Code</label>
                    <input type="text" class="form-control" id="search" name="search"
                           placeholder="Enter contract code..."
                           value="${currentSearch}">
                </div>
                <div class="col-md-4">
                    <label for="status" class="form-label">Filter by Status</label>
                    <select class="form-select" id="status" name="status">
                        <option value="">All Active Orders</option>
                        <option value="active" ${currentStatus == 'active' ? 'selected' : ''}>Active (Booked, InUse)</option>
                        <option value="Booked" ${currentStatus == 'Booked' ? 'selected' : ''}>Booked</option>
                        <option value="InUse" ${currentStatus == 'InUse' ? 'selected' : ''}>In Use</option>
                        <option value="Completed" ${currentStatus == 'Completed' ? 'selected' : ''}>Completed</option>
                        <option value="Cancelled" ${currentStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Filter</button>
                        <a href="${pageContext.request.contextPath}/client/orders" class="btn btn-outline-secondary">Clear</a>
                    </div>
                </div>
            </div>
        </div>
    </form>

    <div class="card shadow-sm">
        <div class="card-header bg-white fw-semibold">Current Orders</div>
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Code</th>
                        <th>Car</th>
                        <th>Driver</th>
                        <th>Start</th>
                        <th>End</th>
                        <th>Status</th>
                        <th class="text-end"></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="o" items="${orders}">
                    <tr>
                        <td>${o.contractCode}</td>
                        <td>${o.carName}</td>
                        <td>${o.driverName}</td>
                        <td>${o.startDateTime}</td>
                        <td>${o.endDateTime}</td>
                        <td>
                            <c:choose>
                                <c:when test="${o.detailStatus == 'Booked'}">
                                    <span class="badge bg-primary">Booked</span>
                                </c:when>
                                <c:when test="${o.detailStatus == 'InUse'}">
                                    <span class="badge bg-warning text-dark">In Use</span>
                                </c:when>
                                <c:when test="${o.detailStatus == 'Completed'}">
                                    <span class="badge bg-success">Completed</span>
                                </c:when>
                                <c:when test="${o.detailStatus == 'Cancelled'}">
                                    <span class="badge bg-danger">Cancelled</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">${o.detailStatus}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <c:if test="${o.driverID != null && canReview_[o.contractID]}">
                                <button class="btn btn-sm btn-warning" data-bs-toggle="modal" data-bs-target="#reviewModal${o.contractID}">
                                    ★ Rate Driver
                                </button>
                            </c:if>
                            <a class="btn btn-sm btn-outline-dark" href="${pageContext.request.contextPath}/client/trip/detail?id=${o.contractID}">Detail</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-3">No orders found.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <%-- Review Modals --%>
    <c:forEach var="o" items="${orders}">
        <c:if test="${o.driverID != null && canReview_[o.contractID]}">
            <div class="modal fade" id="reviewModal${o.contractID}" tabindex="-1" aria-labelledby="reviewModalLabel${o.contractID}" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="reviewModalLabel${o.contractID}">Rate Driver</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form action="${pageContext.request.contextPath}/client/orders" method="post">
                            <input type="hidden" name="action" value="review">
                            <input type="hidden" name="contractID" value="${o.contractID}">
                            <input type="hidden" name="driverID" value="${o.driverID}">
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label class="form-label">Rating</label>
                                    <div class="d-flex gap-2">
                                        <c:forEach begin="1" end="5" var="star">
                                            <input type="radio" class="btn-check" name="rating" id="star${o.contractID}_${star}" value="${star}" ${star == 5 ? 'checked' : ''}>
                                            <label class="btn btn-outline-warning" for="star${o.contractID}_${star}">${star} ★</label>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Comment (optional)</label>
                                    <textarea class="form-control" name="comment" rows="3" placeholder="Share your experience..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-primary">Submit Review</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>
    </c:forEach>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
