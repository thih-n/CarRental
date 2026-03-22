<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Wizard - Step 1</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3 class="mb-1">Booking Wizard</h3>
            <p class="text-muted mb-0">Step 1/3 - Tìm xe khả dụng</p>
        </div>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/home">Back Home</a>
    </div>

    <!-- Search Form - Top Section (col-md-8) -->
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/booking/search" method="get" class="row g-3 align-items-end" novalidate>
                <div class="col-md-4">
                    <label class="form-label">Pickup time</label>
                    <input type="datetime-local" class="form-control" name="startDateTime" id="searchStartTime" 
                           value="${startDateTime}" min="" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Return time</label>
                    <input type="datetime-local" class="form-control" name="endDateTime" id="searchEndTime" 
                           value="${endDateTime}" min="" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Location</label>
                    <select name="pickUpLocationID" id="searchLocation" class="form-select" required>
                        <c:forEach var="loc" items="${locations}">
                            <option value="${loc.locationID}" ${(empty param.pickUpLocationID && loc.locationID == 1) || param.pickUpLocationID == loc.locationID ? 'selected' : ''}>${loc.locationName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1 d-grid">
                    <button class="btn btn-dark" type="submit">Search</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Toggle Filters Button -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-outline-dark" type="button" data-bs-toggle="collapse" data-bs-target="#filterSection">
            ☰  Filters
        </button>
        <div class="text-muted">Total: ${totalItems} cars found</div>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="alert alert-info mb-4">
        Lưu ý: Quý khách vui lòng đến sớm 15-30 phút để hoàn tất bàn giao xe và xác thực giấy tờ.
    </div>

    <!-- Main Content: Filter Sidebar + Car List -->
    <div class="row g-4">
        <!-- Filter Sidebar - Left -->
        <div class="col-md-3">
            <div class="card shadow-sm collapse show" id="filterSection">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/booking/search" method="get" class="row g-3">
                        <input type="hidden" name="startDateTime" value="${startDateTime}">
                        <input type="hidden" name="endDateTime" value="${endDateTime}">
                        <input type="hidden" name="pickUpLocationID" value="${pickUpLocationID}">
                        
                        <!-- Brand Filter -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Brand</label>
                            <div class="d-flex flex-column gap-1" style="max-height: 150px; overflow-y: auto;">
                                <c:forEach var="b" items="${brands}">
                                    <label class="form-check">
                                        <input class="form-check-input" type="checkbox" name="brandID" value="${b.brandID}"
                                               ${brandIDs != null && brandIDs.contains(b.brandID) ? 'checked' : ''}>
                                        <span class="form-check-label">${b.brandName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <!-- Car Type Filter -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Car Type</label>
                            <div class="d-flex flex-column gap-1" style="max-height: 150px; overflow-y: auto;">
                                <c:forEach var="t" items="${carTypes}">
                                    <label class="form-check">
                                        <input class="form-check-input" type="checkbox" name="typeID" value="${t.typeID}"
                                               ${typeIDs != null && typeIDs.contains(t.typeID) ? 'checked' : ''}>
                                        <span class="form-check-label">${t.typeName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <!-- Amenities Filter -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Amenities</label>
                            <div class="d-flex flex-column gap-1" style="max-height: 150px; overflow-y: auto;">
                                <c:forEach var="a" items="${amenities}">
                                    <label class="form-check">
                                        <input class="form-check-input" type="checkbox" name="amenityID" value="${a.amenityID}"
                                               ${amenityIDs != null && amenityIDs.contains(a.amenityID) ? 'checked' : ''}>
                                        <span class="form-check-label">${a.amenityName}</span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        
                        <!-- Price Range Filter -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Price Range</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="priceRange" value="" ${empty priceRange ? 'checked' : ''}>
                                <label class="form-check-label">All</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="priceRange" value="under_500" ${priceRange == 'under_500' ? 'checked' : ''}>
                                <label class="form-check-label">Under 500k/day</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="priceRange" value="500_1000" ${priceRange == '500_1000' ? 'checked' : ''}>
                                <label class="form-check-label">500k - 1,000k/day</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="priceRange" value="over_1000" ${priceRange == 'over_1000' ? 'checked' : ''}>
                                <label class="form-check-label">Over 1,000k/day</label>
                            </div>
                        </div>
                        
                        <!-- Sort Order -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Sort By Price</label>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="sortOrder" value="asc" ${sortOrder == 'asc' ? 'checked' : ''}>
                                <label class="form-check-label">Low to High</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="sortOrder" value="desc" ${sortOrder == 'desc' ? 'checked' : ''}>
                                <label class="form-check-label">High to Low</label>
                            </div>
                        </div>
                        
                        <!-- Keyword -->
                        <div class="col-12">
                            <label class="form-label fw-bold">Keyword</label>
                            <input class="form-control" type="text" name="keyword" value="${keyword}" placeholder="Car name, plate...">
                        </div>
                        
                        <!-- Apply Button -->
                        <div class="col-12">
                            <button class="btn btn-dark w-100" type="submit">Apply Filters</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Car List - Right -->
        <div class="col-md-9">
            <div class="row g-3">
                <c:forEach var="car" items="${cars}">
                    <div class="col-lg-4 col-md-6">
                        <div class="card h-100 shadow-sm ${car.carID == carID ? 'border-primary border-2' : ''}">
                            <c:if test="${car.carID == carID}">
                                <div class="card-header bg-primary text-white">Xe bạn đã chọn</div>
                            </c:if>
                            <c:choose>
                                <c:when test="${not empty car.imageURL}">
                                    <img src="${car.imageURL}" class="card-img-top" alt="${car.carName}" style="height: 160px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                                </c:when>
                                <c:otherwise>
                                    <div class="card-img-top bg-light d-flex align-items-center justify-content-center" style="height: 160px;">
                                        <span class="text-muted">No Image</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <div class="card-body">
                                <h5>${car.carName}</h5>
                                <div>Brand: ${car.brandName}</div>
                                <div>Type: ${car.typeName}</div>
                                <div>Transmission: ${car.transmission}</div>
                                <div class="fw-bold text-primary mt-2">${car.defaultPricePerDay} / day</div>
                            </div>
                            <div class="card-footer bg-white border-0">
                                <form action="${pageContext.request.contextPath}/booking/drivers" method="get" class="d-grid">
                                    <input type="hidden" name="carID" value="${car.carID}">
                                    <input type="hidden" name="pickUpLocationID" value="${pickUpLocationID}">
                                    <input type="hidden" name="startDateTime" value="${startDateTime}">
                                    <input type="hidden" name="endDateTime" value="${endDateTime}">
                                    <button class="btn btn-outline-dark" type="submit">Next: Choose driver</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty cars}">
                    <div class="col-12">
                        <div class="alert alert-warning">No cars found.</div>
                    </div>
                </c:if>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="mt-4 d-flex justify-content-between align-items-center">
                    <div>Page ${page} / ${totalPages}</div>
                    <nav>
                        <ul class="pagination mb-0">
                            <c:if test="${page > 1}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${page-1}&startDateTime=${startDateTime}&endDateTime=${endDateTime}&pickUpLocationID=${pickUpLocationID}<c:forEach var='b' items='${brandIDs}'>&brandID=${b}</c:forEach><c:forEach var='t' items='${typeIDs}'>&typeID=${t}</c:forEach><c:forEach var='a' items='${amenityIDs}'>&amenityID=${a}</c:forEach>${priceRange != null ? '&priceRange='.concat(priceRange) : ''}${sortOrder != null ? '&sortOrder='.concat(sortOrder) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">Previous</a>
                                </li>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${i == page ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}&startDateTime=${startDateTime}&endDateTime=${endDateTime}&pickUpLocationID=${pickUpLocationID}<c:forEach var='b' items='${brandIDs}'>&brandID=${b}</c:forEach><c:forEach var='t' items='${typeIDs}'>&typeID=${t}</c:forEach><c:forEach var='a' items='${amenityIDs}'>&amenityID=${a}</c:forEach>${priceRange != null ? '&priceRange='.concat(priceRange) : ''}${sortOrder != null ? '&sortOrder='.concat(sortOrder) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">${i}</a>
                                </li>
                            </c:forEach>
                            <c:if test="${page < totalPages}">
                                <li class="page-item">
                                    <a class="page-link" href="?page=${page+1}&startDateTime=${startDateTime}&endDateTime=${endDateTime}&pickUpLocationID=${pickUpLocationID}<c:forEach var='b' items='${brandIDs}'>&brandID=${b}</c:forEach><c:forEach var='t' items='${typeIDs}'>&typeID=${t}</c:forEach><c:forEach var='a' items='${amenityIDs}'>&amenityID=${a}</c:forEach>${priceRange != null ? '&priceRange='.concat(priceRange) : ''}${sortOrder != null ? '&sortOrder='.concat(sortOrder) : ''}${keyword != null ? '&keyword='.concat(keyword) : ''}">Next</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
            </c:if>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function() {
    var startInput = document.getElementById('searchStartTime');
    var endInput = document.getElementById('searchEndTime');
    var form = document.querySelector('form[action$="/booking/search"]:not(#filterForm)');
    var locationInput = document.getElementById('searchLocation');

    function formatDateTimeLocal(date) {
        var year = date.getFullYear();
        var month = String(date.getMonth() + 1).padStart(2, '0');
        var day = String(date.getDate()).padStart(2, '0');
        var hours = String(date.getHours()).padStart(2, '0');
        var minutes = String(date.getMinutes()).padStart(2, '0');
        return year + '-' + month + '-' + day + 'T' + hours + ':' + minutes;
    }

    // Function to set min based on selected date
    function setStartMin() {
        var now = new Date();
        var today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        
        if (startInput.value) {
            var selectedDate = new Date(startInput.value);
            var selectedDateOnly = new Date(selectedDate.getFullYear(), selectedDate.getMonth(), selectedDate.getDate());
            var todayOnly = new Date(today.getFullYear(), today.getMonth(), today.getDate());
            
            // Only set min if selected date is today
            if (selectedDateOnly.getTime() === todayOnly.getTime()) {
                var minTime = new Date(now.getTime() + 60 * 1000); // +1 minute
                minTime.setSeconds(0);
                minTime.setMilliseconds(0);
                startInput.min = formatDateTimeLocal(minTime);
            } else if (selectedDateOnly > todayOnly) {
                // Future date - no min restriction
                startInput.removeAttribute('min');
            }
        } else {
            // No value yet - set default min to now + 1 minute
            var minTime = new Date(now.getTime() + 60 * 1000);
            minTime.setSeconds(0);
            minTime.setMilliseconds(0);
            startInput.min = formatDateTimeLocal(minTime);
        }
    }

    // Set end min based on start
    function setEndMin() {
        if (startInput.value) {
            var startDate = new Date(startInput.value);
            if (!isNaN(startDate.getTime())) {
                var minEnd = new Date(startDate.getTime() + 60 * 60 * 1000);
                endInput.setAttribute('min', formatDateTimeLocal(minEnd));
            }
        }
    }

    setStartMin();
    setEndMin();

    // Event listeners
    startInput.addEventListener('input', function() {
        setStartMin();
        setEndMin();
    });
    startInput.addEventListener('change', function() {
        setStartMin();
        setEndMin();
    });
    endInput.addEventListener('input', setEndMin);
    endInput.addEventListener('change', setEndMin);

    // Form validation on submit
    if (form) {
        form.addEventListener('submit', function(e) {
            var startVal = startInput.value;
            var endVal = endInput.value;
            var locationVal = locationInput.value;

            if (!startVal || !endVal || !locationVal) {
                alert('Vui lòng chọn đầy đủ thời gian nhận xe, trả xe và địa điểm!');
                e.preventDefault();
                return false;
            }

            var startDate = new Date(startVal);
            var endDate = new Date(endVal);

            var nowCheck = new Date();
            if (startDate < nowCheck) {
                alert('Thời gian nhận xe không được trong quá khứ!');
                e.preventDefault();
                return false;
            }

            if (endDate <= startDate) {
                alert('Thời gian trả xe phải sau thời gian nhận xe!');
                e.preventDefault();
                return false;
            }

            var hoursDiff = (endDate - startDate) / (1000 * 60 * 60);
            if (hoursDiff < 1) {
                alert('Thời gian thuê xe tối thiểu là 1 tiếng!');
                e.preventDefault();
                return false;
            }
        });
    }
})();
</script>
</body>
</html>
