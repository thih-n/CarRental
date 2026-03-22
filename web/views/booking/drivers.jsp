<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Wizard - Step 2</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h3 class="mb-1">Booking Wizard</h3>
            <p class="text-muted mb-0">Step 2/3 - Chọn tài xế (tuỳ chọn).</p>
        </div>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/booking/search?carID=${param.carID}&pickUpLocationID=${param.pickUpLocationID}&startDateTime=${param.startDateTime}&endDateTime=${param.endDateTime}">Back to search</a>
    </div>

    <!-- Car and Location Info -->
    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <div class="row">
                <div class="col-md-3"><strong>Xe:</strong> ${car.carName}</div>
                <div class="col-md-3"><strong>Địa điểm:</strong> ${location.locationName}</div>
                <div class="col-md-3"><strong>Nhận xe:</strong> ${param.startDateTime}</div>
                <div class="col-md-3"><strong>Trả xe:</strong> ${param.endDateTime}</div>
            </div>
        </div>
    </div>

    <!-- Driver Search/Filter -->
    <div class="card shadow-sm mb-3">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/booking/drivers" method="get" class="row g-3 align-items-end">
                <input type="hidden" name="carID" value="${param.carID}">
                <input type="hidden" name="pickUpLocationID" value="${param.pickUpLocationID}">
                <input type="hidden" name="startDateTime" value="${param.startDateTime}">
                <input type="hidden" name="endDateTime" value="${param.endDateTime}">
                <input type="hidden" name="rentPrice" value="${param.rentPrice}">
                <div class="col-md-5">
                    <label class="form-label">Tìm kiếm tài xế</label>
                    <input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Nhập tên tài xế...">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Đánh giá tối thiểu</label>
                    <select class="form-select" name="minRating">
                        <option value="">Tất cả</option>
                        <option value="5" ${minRating == '5' ? 'selected' : ''}>5 sao</option>
                        <option value="4" ${minRating == '4' ? 'selected' : ''}>4+ sao</option>
                        <option value="3" ${minRating == '3' ? 'selected' : ''}>3+ sao</option>
                    </select>
                </div>
                <div class="col-md-2 d-grid">
                    <button class="btn btn-dark" type="submit">Tìm kiếm</button>
                </div>
                <div class="col-md-2 d-grid">
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/booking/drivers?carID=${param.carID}&pickUpLocationID=${param.pickUpLocationID}&startDateTime=${param.startDateTime}&endDateTime=${param.endDateTime}&rentPrice=${param.rentPrice}">Xóa filter</a>
                </div>
            </form>
        </div>
    </div>

    <div class="row g-3 mb-3">
        <c:forEach var="d" items="${drivers}">
            <div class="col-md-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body">
                        <h5>${d.fullName}</h5>
                        <div>Rating: ${d.rating} / 5</div>
                        <div>Số chuyến: ${d.totalTrips}</div>
                        <div>Phí/ngày: ${d.dailyRate} VNĐ</div>
                    </div>
                    <div class="card-footer bg-white border-0">
                        <button class="btn btn-dark w-100" type="button" 
                                onclick="selectDriver(${d.driverID}, '${d.fullName}', ${d.dailyRate})">
                            Chọn tài xế này
                        </button>
                    </div>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty drivers}">
            <div class="col-12">
                <div class="alert alert-warning">Không có tài xế nào phù hợp.</div>
            </div>
        </c:if>
    </div>

    <div class="card shadow-sm">
        <div class="card-body d-flex justify-content-between align-items-center">
            <div>Không cần tài xế?</div>
            <button class="btn btn-outline-dark" type="button" onclick="skipDriver()">Tiếp tục không cần tài xế</button>
        </div>
    </div>
</div>

<!-- Confirmation Modal -->
<div class="modal fade" id="driverConfirmModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Xác nhận chọn tài xế</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div id="modalContent"></div>
                <div class="alert alert-info mt-3">
                    <strong>Lưu ý:</strong> Xe và tài xế sẽ được giữ trong 5 phút. Vui lòng hoàn tất đặt xe trong thời gian này.
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-primary" id="confirmBtn">Xác nhận</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
let selectedDriverID = null;
let driverName = null;
let driverRate = null;
let modal = null;

document.addEventListener('DOMContentLoaded', function() {
    modal = new bootstrap.Modal(document.getElementById('driverConfirmModal'));
});

function selectDriver(driverID, name, rate) {
    selectedDriverID = driverID;
    driverName = name;
    driverRate = rate;
    
    document.getElementById('modalContent').innerHTML = 
        '<p><strong>Tài xế:</strong> ' + name + '</p>' +
        '<p><strong>Phí/ngày:</strong> ' + rate + ' VNĐ</p>';
    
    document.getElementById('confirmBtn').onclick = function() {
        submitWithDriver(driverID);
    };
    
    modal.show();
}

function skipDriver() {
    selectedDriverID = null;
    driverName = 'Không có tài xế';
    driverRate = 0;
    
    document.getElementById('modalContent').innerHTML = 
        '<p>Bạn chọn <strong>không cần tài xế</strong></p>';
    
    document.getElementById('confirmBtn').onclick = function() {
        submitWithoutDriver();
    };
    
    modal.show();
}

function submitWithDriver(driverID) {
    const params = new URLSearchParams();
    params.append('carID', '${param.carID}');
    params.append('pickUpLocationID', '${param.pickUpLocationID}');
    params.append('startDateTime', '${param.startDateTime}');
    params.append('endDateTime', '${param.endDateTime}');
    params.append('driverID', driverID);

    fetch('${pageContext.request.contextPath}/booking/drivers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || 'Lỗi server'); });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            modal.hide();
            window.location.href = '${pageContext.request.contextPath}/booking/checkout?carID=${param.carID}&pickUpLocationID=${param.pickUpLocationID}&startDateTime=${param.startDateTime}&endDateTime=${param.endDateTime}&driverID=' + driverID;
        } else {
            alert(data.message || 'Có lỗi xảy ra!');
        }
    })
    .catch(error => {
        alert(error.message || 'Có lỗi xảy ra!');
        console.error(error);
    });
}

function submitWithoutDriver() {
    const params = new URLSearchParams();
    params.append('carID', '${param.carID}');
    params.append('pickUpLocationID', '${param.pickUpLocationID}');
    params.append('startDateTime', '${param.startDateTime}');
    params.append('endDateTime', '${param.endDateTime}');

    fetch('${pageContext.request.contextPath}/booking/drivers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || 'Lỗi server'); });
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            modal.hide();
            window.location.href = '${pageContext.request.contextPath}/booking/checkout?carID=${param.carID}&pickUpLocationID=${param.pickUpLocationID}&startDateTime=${param.startDateTime}&endDateTime=${param.endDateTime}';
        } else {
            alert(data.message || 'Có lỗi xảy ra!');
        }
    })
    .catch(error => {
        alert(error.message || 'Có lỗi xảy ra!');
        console.error(error);
    });
}
</script>
</body>
</html>
