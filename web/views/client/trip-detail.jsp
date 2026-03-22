<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trip Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">Trip Detail</h3>
            <p class="text-muted mb-0">Thông tin chi tiết chuyến xe của bạn.</p>
        </div>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/client/orders">Back to orders</a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">Thao tác thành công!</div>
    </c:if>
    <c:if test="${param.success == 'reviewed'}">
        <div class="alert alert-success">Đã gửi đánh giá tài xế.</div>
    </c:if>

    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-6">
                    <p><strong>Contract:</strong> ${trip.contractCode}</p>
                    <p><strong>Car:</strong> ${trip.carName}</p>
                    <p><strong>Pickup Location:</strong> ${trip.pickUpLocationName}</p>
                </div>
                <div class="col-md-6">
                    <p><strong>Start:</strong> <fmt:formatDate value="${trip.startDateTime}" pattern="yyyy-MM-dd HH:mm"/></p>
                    <p><strong>End:</strong> <fmt:formatDate value="${trip.endDateTime}" pattern="yyyy-MM-dd HH:mm"/></p>
                    <p><strong>Status:</strong> 
                        <span class="badge 
                            <c:choose>
                                <c:when test="${trip.detailStatus == 'Booked'}">bg-primary</c:when>
                                <c:when test="${trip.detailStatus == 'InUse'}">bg-warning</c:when>
                                <c:when test="${trip.detailStatus == 'Completed'}">bg-success</c:when>
                                <c:when test="${trip.detailStatus == 'Cancelled'}">bg-danger</c:when>
                            </c:choose>
                        ">${trip.detailStatus}</span>
                    </p>
                </div>
            </div>
            <hr/>
            
            <%-- Driver Information --%>
            <c:choose>
                <c:when test="${not empty driver}">
                    <div class="alert alert-info mb-3">
                        <h6 class="alert-heading fw-bold">Thông tin tài xế</h6>
                        <div class="row">
                            <div class="col-md-6">
                                <p class="mb-1"><strong>Tên:</strong> ${driver.fullName}</p>
                                <p class="mb-1"><strong>Số GPLX:</strong> ${driver.licenseNumber}</p>
                                <p class="mb-1"><strong>Kinh nghiệm:</strong> ${driver.experienceYears} năm</p>
                            </div>
                            <div class="col-md-6">
                                <p class="mb-1"><strong>Rating:</strong> 
                                    <c:choose>
                                        <c:when test="${driver.rating != null}">${driver.rating}/5</c:when>
                                        <c:otherwise>Chưa có đánh giá</c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="mb-1"><strong>Tổng chuyến:</strong> ${driver.totalTrips != null ? driver.totalTrips : 0}</p>
                                <p class="mb-1"><strong>Phí driver:</strong> <fmt:formatNumber value="${driver.dailyRate}" pattern="#,###"/> VND/ngày</p>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning mb-3">
                        <strong>Không chọn tài xế cho chuyến này</strong>
                    </div>
                </c:otherwise>
            </c:choose>
            
            <hr/>
            <div class="d-flex justify-content-between">
                <div>
                    <p class="fw-bold mb-1">Total amount</p>
                    <p class="mb-0"><fmt:formatNumber value="${trip.totalAmount}" pattern="#,###"/> VND</p>
                </div>
                <div>
                    <p class="fw-bold mb-1">Deposit</p>
                    <p class="mb-0"><fmt:formatNumber value="${trip.depositAmount}" pattern="#,###"/> VND</p>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${trip.detailStatus == 'Completed' && not empty driver && !hasDriverReview}">
        <div class="card shadow-sm mb-3">
            <div class="card-header bg-white">
                <h5 class="mb-0">Đánh giá tài xế</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/client/trip/detail" method="post">
                    <input type="hidden" name="action" value="review-driver">
                    <input type="hidden" name="contractID" value="${trip.contractID}">
                    <input type="hidden" name="driverID" value="${driver.driverID}">
                    <div class="mb-3">
                        <label class="form-label">Số sao</label>
                        <select name="rating" class="form-select" required>
                            <option value="">Chọn số sao</option>
                            <option value="5">5 - Rất hài lòng</option>
                            <option value="4">4 - Hài lòng</option>
                            <option value="3">3 - Bình thường</option>
                            <option value="2">2 - Không hài lòng</option>
                            <option value="1">1 - Rất tệ</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Nhận xét</label>
                        <textarea name="comment" class="form-control" rows="3" placeholder="Chia sẻ trải nghiệm của bạn..."></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                </form>
            </div>
        </div>
    </c:if>

    <!-- Actions for Customer -->
    <c:if test="${trip.canCancel()}">
        <div class="card shadow-sm mb-3">
            <div class="card-header bg-danger text-white">
                <h5 class="mb-0">Hủy chuyến</h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/client/trip/detail" method="post">
                    <input type="hidden" name="action" value="cancel">
                    <input type="hidden" name="contractID" value="${trip.contractID}">
                    <div class="mb-3">
                        <label class="form-label">Lý do hủy:</label>
                        <textarea class="form-control" name="cancelReason" rows="2" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-danger">Hủy chuyến</button>
                </form>
            </div>
        </div>
    </c:if>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
