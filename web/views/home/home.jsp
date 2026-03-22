<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home - Car Rental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h3 class="mb-1">Thuê xe nhanh chóng & an toàn</h3>
                    <p class="text-muted mb-0">Tìm xe phù hợp, đặt ngay trong vài phút.</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/booking/search">Book now</a>
                </div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/cars" method="get" class="row g-3 align-items-end" novalidate>
                <div class="col-md-4">
                    <label class="form-label">Pickup time</label>
                    <input type="datetime-local" class="form-control" name="startDateTime" id="homeStartTime" value="${startDateTime}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Return time</label>
                    <input type="datetime-local" class="form-control" name="endDateTime" id="homeEndTime" value="${endDateTime}" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Location</label>
                    <select class="form-select" name="pickUpLocationID" id="homeLocation" required>
                        <c:forEach var="loc" items="${locations}">
                            <option value="${loc.locationID}" ${loc.locationID == 1 ? 'selected' : ''}>${loc.locationName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1 d-grid">
                    <button class="btn btn-dark" type="submit">Search</button>
                </div>
            </form>
        </div>
    </div>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="mb-0">Featured Cars</h4>
        <a class="btn btn-outline-dark btn-sm" href="${pageContext.request.contextPath}/cars">View all</a>
    </div>
    <div class="row g-3 mb-4">
        <c:forEach var="car" items="${cars}">
            <div class="col-md-4">
                <div class="card h-100 shadow-sm">
                    <c:choose>
                        <c:when test="${not empty car.imageURL}">
                            <img src="${car.imageURL}" class="card-img-top" alt="${car.carName}" style="height: 180px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                        </c:when>
                        <c:otherwise>
                            <div class="card-img-top bg-light d-flex align-items-center justify-content-center" style="height: 180px;">
                                <span class="text-muted">No Image</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="card-body">
                        <h5 class="card-title">${car.carName}</h5>
                        <p class="mb-1">Brand: ${car.brandName}</p>
                        <p class="mb-1">Type: ${car.typeName}</p>
                        <p class="fw-bold text-primary mb-0">${car.defaultPricePerDay} / day</p>
                    </div>
                    <div class="card-footer bg-white border-0">
                        <a href="${pageContext.request.contextPath}/car/detail?id=${car.carID}" class="btn btn-sm btn-outline-dark">View detail</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="row g-4">
        <div class="col-lg-6">
            <div class="card shadow-sm">
                <div class="card-header bg-white"><strong>Top Drivers</strong></div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="d" items="${topDrivers}">
                        <li class="list-group-item d-flex justify-content-between">
                            <span>${d.fullName} (${d.experienceYears} years)</span>
                            <span>⭐ ${d.rating}</span>
                        </li>
                    </c:forEach>
                    <c:if test="${empty topDrivers}">
                        <li class="list-group-item text-muted">No drivers yet.</li>
                    </c:if>
                </ul>
            </div>
        </div>
        <div class="col-lg-6">
            <div class="card shadow-sm">
                <div class="card-header bg-white"><strong>Reviews</strong></div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="r" items="${latestReviews}">
                        <li class="list-group-item">
                            <div class="d-flex justify-content-between">
                                <strong>${r.reviewerName}</strong>
                                <span>⭐ ${r.rating}</span>
                            </div>
                            <small>${r.comment}</small>
                        </li>
                    </c:forEach>
                    <c:if test="${empty latestReviews}">
                        <li class="list-group-item text-muted">No reviews yet.</li>
                    </c:if>
                </ul>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form[action$="/cars"]');
    const startInput = document.getElementById('homeStartTime');
    const endInput = document.getElementById('homeEndTime');
    const locationInput = document.getElementById('homeLocation');

    function formatDateTimeLocal(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return year + '-' + month + '-' + day + 'T' + hours + ':' + minutes;
    }

    // Function to set min based on selected date
    function setStartMin() {
        const now = new Date();
        
        if (startInput.value) {
            const selectedDate = new Date(startInput.value);
            const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
            const selectedDay = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate());
            
            if (selectedDay.getTime() === today.getTime()) {
                const minTime = new Date(now.getTime() + 60 * 1000);
                minTime.setSeconds(0);
                minTime.setMilliseconds(0);
                startInput.min = formatDateTimeLocal(minTime);
            } else if (selectedDay > today) {
                startInput.removeAttribute('min');
            }
        } else {
            const minTime = new Date(now.getTime() + 60 * 1000);
            minTime.setSeconds(0);
            minTime.setMilliseconds(0);
            startInput.min = formatDateTimeLocal(minTime);
        }
    }

    // Set default values if empty
    if (!startInput.value || startInput.value === '') {
        const now = new Date();
        now.setMinutes(0, 0, 0);
        now.setHours(now.getHours() + 1);
        startInput.value = formatDateTimeLocal(now);

        const endDate = new Date(now);
        endDate.setHours(endDate.getHours() + 4);
        endInput.value = formatDateTimeLocal(endDate);
    }

    if (!endInput.value || endInput.value === '') {
        const sDate = new Date(startInput.value);
        const endDate2 = new Date(sDate);
        endDate2.setHours(endDate2.getHours() + 4);
        endInput.value = formatDateTimeLocal(endDate2);
    }

    // Update end min when start changes
    function updateEndMin() {
        const startDate = new Date(startInput.value);
        if (!isNaN(startDate.getTime())) {
            const minEnd = new Date(startDate.getTime() + 60 * 60 * 1000);
            endInput.min = formatDateTimeLocal(minEnd);
        }
    }

    updateEndMin();

    startInput.addEventListener('change', function() {
        setStartMin();
        const sDate = new Date(this.value);
        if (!isNaN(sDate.getTime())) {
            const newEndDate = new Date(sDate);
            newEndDate.setHours(newEndDate.getHours() + 4);
            endInput.value = formatDateTimeLocal(newEndDate);
            updateEndMin();
        }
    });

    endInput.addEventListener('change', function() {
        updateEndMin();
    });

    // Form validation
    if (form) {
        form.addEventListener('submit', function(e) {
            const startVal = startInput.value;
            const endVal = endInput.value;
            const locationVal = locationInput.value;

            if (!startVal || !endVal || !locationVal) {
                alert('Vui lòng chọn đầy đủ thời gian nhận xe, trả xe và địa điểm!');
                e.preventDefault();
                return false;
            }

            const startDate = new Date(startVal);
            const endDate = new Date(endVal);

            // Check start date is not in the past
            const now = new Date();
            if (startDate < now) {
                alert('Thời gian nhận xe không được trong quá khứ!');
                e.preventDefault();
                return false;
            }

            // Check end date is after start date
            if (endDate <= startDate) {
                alert('Thời gian trả xe phải sau thời gian nhận xe!');
                e.preventDefault();
                return false;
            }

            // Check minimum 1 hour difference
            const hoursDiff = (endDate - startDate) / (1000 * 60 * 60);
            if (hoursDiff < 1) {
                alert('Thời gian thuê xe tối thiểu là 1 tiếng!');
                e.preventDefault();
                return false;
            }
        });
    }
});
</script>
</body>
</html>
