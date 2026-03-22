<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Wizard - Step 3: Xác nhận đặt xe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">Booking Wizard</h3>
            <p class="text-muted mb-0">Step 3/3 - Xác nhận thông tin đặt xe.</p>
        </div>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/booking/drivers?carID=${param.carID}&pickUpLocationID=${param.pickUpLocationID}&startDateTime=${startDateTime}&endDateTime=${endDateTime}">Back to drivers</a>
    </div>

    <div class="alert alert-info">
        Vui lòng kiểm tra kỹ thông tin trước khi xác nhận đặt xe.
    </div>

    <form action="${pageContext.request.contextPath}/booking/checkout" method="post" class="row g-3">
        <input type="hidden" name="carID" value="${param.carID}">
        <input type="hidden" name="pickUpLocationID" value="${param.pickUpLocationID}">
        <input type="hidden" name="startDateTime" value="${startDateTime}">
        <input type="hidden" name="endDateTime" value="${endDateTime}">
        <c:if test="${not empty driver}">
            <input type="hidden" name="driverID" value="${driver.driverID}">
        </c:if>
        <c:if test="${not empty holdingID}">
            <input type="hidden" name="holdingID" value="${holdingID}">
        </c:if>

        <!-- Thông tin xe -->
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0">Thông tin xe</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6"><strong>Tên xe:</strong></div>
                        <div class="col-6">${car.carName}</div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-6"><strong>Biển số:</strong></div>
                        <div class="col-6">${car.plateNumber}</div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-6"><strong>Hộp số:</strong></div>
                        <div class="col-6">${car.transmission}</div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-6"><strong>Nhiên liệu:</strong></div>
                        <div class="col-6">${car.fuelType}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thông tin địa điểm -->
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0">Thông tin địa điểm</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6"><strong>Địa điểm nhận xe:</strong></div>
                        <div class="col-6">${pickUpLocation.locationName}</div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-6"><strong>Địa chỉ:</strong></div>
                        <div class="col-6">${pickUpLocation.address}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thông tin thời gian -->
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0">Thông tin thời gian</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-6"><strong>Ngày nhận xe:</strong></div>
                        <div class="col-6">${startDateTime}</div>
                    </div>
                    <div class="row mt-2">
                        <div class="col-6"><strong>Ngày trả xe:</strong></div>
                        <div class="col-6">${endDateTime}</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Thông tin tài xế -->
        <div class="col-md-6">
            <div class="card shadow-sm h-100">
                <div class="card-header bg-dark text-white">
                    <h5 class="mb-0">Thông tin tài xế</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty driver}">
                            <div class="row">
                                <div class="col-6"><strong>Tên tài xế:</strong></div>
                                <div class="col-6">${driver.fullName}</div>
                            </div>
                            <div class="row mt-2">
                                <div class="col-6"><strong>Đánh giá:</strong></div>
                                <div class="col-6">${driver.rating} / 5</div>
                            </div>
                            <div class="row mt-2">
                                <div class="col-6"><strong>Số chuyến:</strong></div>
                                <div class="col-6">${driver.totalTrips} chuyến</div>
                            </div>
                            <div class="row mt-2">
                                <div class="col-6"><strong>Phí tài xế/ngày:</strong></div>
                                <div class="col-6"><fmt:formatNumber value="${driver.dailyRate}" pattern="#,###" /> VNĐ</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-muted">Không có tài xế</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Thông tin giá tiền -->
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">Thông tin thanh toán</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-4">
                            <div><strong>Tiền thuê xe:</strong></div>
                            <div class="text-muted small">Giá cơ bản theo ngày</div>
                        </div>
                        <div class="col-md-8 text-end">
                            <fmt:formatNumber value="${rentPrice}" pattern="#,###" /> VNĐ
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-md-4">
                            <div><strong>Phí tài xế:</strong></div>
                            <div class="text-muted small">Phí dịch vụ tài xế</div>
                        </div>
                        <div class="col-md-8 text-end">
                            <c:choose>
                                <c:when test="${not empty driver && driverFee > 0}">
                                    <fmt:formatNumber value="${driverFee}" pattern="#,###" /> VNĐ
                                </c:when>
                                <c:otherwise>
                                    0 VNĐ
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-md-4">
                            <div><strong>Tổng cộng:</strong></div>
                        </div>
                        <div class="col-md-8 text-end fs-5 fw-bold text-primary">
                            <fmt:formatNumber value="${totalAmount}" pattern="#,###" /> VNĐ
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-md-4">
                            <div><strong>Tiền cọc (30%):</strong></div>
                            <div class="text-muted small">Thanh toán trước để xác nhận đặt xe</div>
                        </div>
                        <div class="col-md-8 text-end fs-5 fw-bold text-success">
                            <fmt:formatNumber value="${depositAmount}" pattern="#,###" /> VNĐ
                        </div>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="col-md-4">
                            <div><strong>Còn lại (70%):</strong></div>
                            <div class="text-muted small">Thanh toán khi nhận xe</div>
                        </div>
                        <div class="col-md-8 text-end fs-5 fw-bold text-warning">
                            <fmt:formatNumber value="${totalAmount - depositAmount}" pattern="#,###" /> VNĐ
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Phương thức thanh toán cọc -->
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-header bg-success text-white">
                    <h5 class="mb-0">Phương thức thanh toán cọc (30%)</h5>
                </div>
                <div class="card-body">
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payVNPay" value="VNPay" checked>
                        <label class="form-check-label fw-bold" for="payVNPay">
                            Thanh toán trực tuyến VNPay
                        </label>
                    </div>
                    <div class="form-check mb-2">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payMomo" value="Momo">
                        <label class="form-check-label fw-bold" for="payMomo">
                            Thanh toán qua ví Momo
                        </label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payBank" value="BankTransfer">
                        <label class="form-check-label fw-bold" for="payBank">
                            Chuyển khoản ngân hàng
                        </label>
                    </div>
                </div>
            </div>
        </div>

        <!-- Ghi chú -->
        <div class="col-12">
            <div class="card shadow-sm">
                <div class="card-body">
                    <label class="form-label"><strong>Ghi chú thêm (tuỳ chọn):</strong></label>
                    <textarea class="form-control" name="notes" rows="3" placeholder="Nhập ghi chú nếu có..."></textarea>
                </div>
            </div>
        </div>

        <!-- Countdown timer -->
        <div class="col-12">
            <div class="alert alert-warning" role="alert">
                <strong>Thời gian giữ chỗ còn lại:</strong> <span id="countdown" class="fs-5 fw-bold"></span>
                <div class="small text-muted mt-1">Vui lòng hoàn tất thanh toán trong thời gian này.</div>
            </div>
        </div>

        <!-- Nút xác nhận -->
        <div class="col-12">
            <div class="d-flex justify-content-between">
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/cars">Hủy</a>
                <button class="btn btn-success btn-lg" type="submit">Xác nhận đặt xe</button>
            </div>
        </div>
    </form>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
<c:if test="${not empty holdingExpiry}">
(function() {
    const expiryTime = new Date('${holdingExpiry}').getTime();
    
    function updateCountdown() {
        const now = new Date().getTime();
        const distance = expiryTime - now;
        
        if (distance < 0) {
            document.getElementById('countdown').innerHTML = 'Hết thời gian giữ chỗ!';
            document.getElementById('countdown').classList.add('text-danger');
            document.querySelector('button[type="submit"]').disabled = true;
            return;
        }
        
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        document.getElementById('countdown').innerHTML = 
            (minutes < 10 ? '0' : '') + minutes + ':' + 
            (seconds < 10 ? '0' : '') + seconds;
    }
    
    updateCountdown();
    setInterval(updateCountdown, 1000);
})();
</c:if>
</body>
</html>
