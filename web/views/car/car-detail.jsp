<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Car Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .calendar-weekdays {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            text-align: center;
            font-size: 0.75rem;
            letter-spacing: 0.05em;
            text-transform: uppercase;
            color: #6c757d;
            margin-top: 1rem;
        }
        .calendar-grid {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            gap: 6px;
            margin-top: 0.5rem;
        }
        .calendar-day {
            border-radius: 0.85rem;
            border: 1px dashed #dee2e6;
            min-height: 62px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            font-weight: 600;
            color: #495057;
            background-color: #fff;
            cursor: pointer;
            transition: all 0.15s ease;
        }
        .calendar-day.placeholder {
            border: none;
            cursor: default;
            pointer-events: none;
            background: transparent;
        }
        .calendar-day--today {
            border-color: #1c7ed6;
            color: #1c7ed6;
        }
        .calendar-day--blocked {
            color: #c92a2a;
        }
        .calendar-day--has-schedule {
            background-color: rgba(252, 178, 71, 0.15);
            border-color: #f59f00;
        }
        .calendar-day--has-schedule::after {
            content: '';
            position: absolute;
            bottom: 4px;
            left: 50%;
            transform: translateX(-50%);
            width: 6px;
            height: 6px;
            background-color: #f59f00;
            border-radius: 50%;
        }
        .calendar-day--range {
            background-color: rgba(32, 156, 109, 0.15);
            color: #0f5132;
        }
        .calendar-day--start,
        .calendar-day--end {
            color: #fff;
        }
        .calendar-day--start {
            background: linear-gradient(135deg, #2f9e44, #60b044);
        }
        .calendar-day--end {
            background: linear-gradient(135deg, #f76707, #ff922b);
        }
        .calendar-day--active {
            box-shadow: 0 0 0 2px rgba(32, 156, 109, 0.25);
        }
        .calendar-day span.blocked-dot {
            position: absolute;
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: #c92a2a;
            bottom: 6px;
            right: 6px;
        }
        .schedule-list {
            max-height: 420px;
            overflow-y: auto;
        }
    </style>
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <a class="btn btn-link mb-3" href="${pageContext.request.contextPath}/cars?startDateTime=${startDateTime}&endDateTime=${endDateTime}&pickUpLocationID=${pickUpLocationID}<c:if test="${not empty param.brandID}">&brandID=${param.brandID}</c:if><c:if test="${not empty param.typeID}">&typeID=${param.typeID}</c:if><c:if test="${not empty param.priceRange}">&priceRange=${param.priceRange}</c:if><c:if test="${not empty param.sortOrder}">&sortOrder=${param.sortOrder}</c:if><c:if test="${not empty param.page}">&page=${param.page}</c:if>">← Back to list</a>

    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/booking/drivers" method="get" class="row g-3 align-items-end" onsubmit="return validateBookingForm()" novalidate>
                <input type="hidden" name="carID" value="${car.carID}">
                <input type="hidden" name="rentPrice" value="${car.defaultPricePerDay}">
                <input type="hidden" name="pickUpLocationID" value="${pickUpLocationID}">
                <div class="col-md-4">
                    <label class="form-label">Pickup time</label>
                    <input type="datetime-local" class="form-control" name="startDateTime" id="carDetailStartTime" value="${startDateTime}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Return time</label>
                    <input type="datetime-local" class="form-control" name="endDateTime" id="carDetailEndTime" value="${endDateTime}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Location</label>
                    <input type="text" class="form-control" value="${locationName}" readonly>
                </div>
                <div class="col-md-12 d-flex flex-wrap gap-2 justify-content-end">
                    <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#carScheduleModal">Lịch xe</button>
                    <button class="btn btn-dark flex-fill" style="min-width: 180px;" type="submit">Book this car</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-body">
            <div class="row g-4">
                <div class="col-md-6">
                    <c:set var="hasMainImage" value="${not empty car.imageURL}" />
                    <c:set var="hasGalleryImages" value="${not empty carImages and carImages.size() > 0}" />
                    <c:choose>
                        <c:when test="${hasMainImage or hasGalleryImages}">
                            <div id="carImageCarousel" class="carousel slide" data-bs-ride="carousel">
                                <div class="carousel-indicators">
                                    <c:set var="imgIndex" value="0" />
                                    <c:if test="${hasMainImage}">
                                        <button type="button" data-bs-target="#carImageCarousel" data-bs-slide-to="0" class="active"></button>
                                        <c:set var="imgIndex" value="${imgIndex + 1}" />
                                    </c:if>
                                    <c:if test="${hasGalleryImages}">
                                        <c:forEach var="img" items="${carImages}" varStatus="status">
                                            <c:if test="${not empty img.imageUrl}">
                                                <button type="button" data-bs-target="#carImageCarousel" data-bs-slide-to="${imgIndex + status.index}" class="${not hasMainImage and status.first ? 'active' : ''}"></button>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </div>
                                <div class="carousel-inner rounded">
                                    <c:if test="${hasMainImage}">
                                        <div class="carousel-item active">
                                            <img src="${car.imageURL}" class="d-block w-100" alt="${car.carName}" style="height: 350px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/600x400?text=No+Image'">
                                            <div class="carousel-caption d-none d-md-block bg-dark bg-opacity-50 rounded">
                                                <small>Ảnh chính</small>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${hasGalleryImages}">
                                        <c:forEach var="img" items="${carImages}" varStatus="status">
                                            <c:if test="${not empty img.imageUrl}">
                                                <div class="carousel-item ${not hasMainImage and status.first ? 'active' : ''}">
                                                    <img src="${img.imageUrl}" class="d-block w-100" alt="${car.carName}" style="height: 350px; object-fit: cover;" onerror="this.src='https://via.placeholder.com/600x400?text=No+Image'">
                                                    <c:if test="${not empty img.imageType}">
                                                        <div class="carousel-caption d-none d-md-block bg-dark bg-opacity-50 rounded">
                                                            <small>${img.imageType == 'Gallery' ? 'Ngoại thất' : (img.imageType == 'Interior' ? 'Nội thất' : 'Chi tiết')}</small>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </div>
                                <button class="carousel-control-prev" type="button" data-bs-target="#carImageCarousel" data-bs-slide="prev">
                                    <span class="carousel-control-prev-icon bg-dark rounded-circle p-3" aria-hidden="true"></span>
                                    <span class="visually-hidden">Previous</span>
                                </button>
                                <button class="carousel-control-next" type="button" data-bs-target="#carImageCarousel" data-bs-slide="next">
                                    <span class="carousel-control-next-icon bg-dark rounded-circle p-3" aria-hidden="true"></span>
                                    <span class="visually-hidden">Next</span>
                                </button>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="bg-light border rounded d-flex align-items-center justify-content-center" style="height: 350px;">
                                <span class="text-muted">No Image</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="col-md-6">
                    <h3>${car.carName}</h3>
                    <p class="mb-1">Plate: ${car.plateNumber}</p>
                    <p class="mb-1">Transmission: ${car.transmission}</p>
                    <p class="mb-1">Fuel: ${car.fuelType}</p>
                    <p class="mb-1">Year: ${car.productionYear}</p>
                    <p class="mb-3">Color: ${car.color}</p>
                    <h4 class="text-primary">${car.defaultPricePerDay} / day</h4>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="carScheduleModal" tabindex="-1" aria-labelledby="carScheduleModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
            <div class="modal-header border-0">
                <div>
                    <h5 class="modal-title" id="carScheduleModalLabel">Lịch xe ${car.carName}</h5>
                    <p class="text-muted small mb-0">Chọn ngày nhận/trả dựa trên lịch thực tế của xe.</p>
                    <p class="text-info small mb-0 mt-2"><strong>Lưu ý:</strong> Có thể nhận xe sau 1 tiếng so với giờ trả của lịch trước, và phải trả xe trước 2 tiếng so với giờ nhận của lịch tiếp theo.</p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <div class="row g-4">
                    <div class="col-lg-8">
                        <div class="card border-0 shadow-sm">
                            <div class="card-body">
                                <div class="d-flex align-items-center justify-content-between">
                                    <button type="button" class="btn btn-sm btn-outline-secondary" id="carSchedulePrevMonth" aria-label="Tháng trước">&lsaquo;</button>
                                    <div class="text-center">
                                        <div class="small text-muted">Tháng</div>
                                        <div class="h5 mb-0" id="carScheduleMonthLabel"></div>
                                    </div>
                                    <button type="button" class="btn btn-sm btn-outline-secondary" id="carScheduleNextMonth" aria-label="Tháng sau">&rsaquo;</button>
                                </div>
                                <div class="calendar-weekdays">
                                    <div>CN</div>
                                    <div>T2</div>
                                    <div>T3</div>
                                    <div>T4</div>
                                    <div>T5</div>
                                    <div>T6</div>
                                    <div>T7</div>
                                </div>
                                <div id="carScheduleCalendarGrid" class="calendar-grid"></div>
                            </div>
                        </div>
                        <div class="d-flex gap-3 align-items-center flex-wrap mt-3">
                            <div class="card flex-fill border-0 shadow-sm">
                                <div class="card-body">
                                    <small class="text-muted">Giờ nhận</small>
                                    <div class="d-flex align-items-center justify-content-between">
                                        <strong id="modalStartDisplay">07:00</strong>
                                        <input type="time" id="carScheduleStartTime" class="form-control form-control-sm w-auto" value="07:00" step="3600">
                                    </div>
                                </div>
                            </div>
                            <div class="card flex-fill border-0 shadow-sm">
                                <div class="card-body">
                                    <small class="text-muted">Giờ trả</small>
                                    <div class="d-flex align-items-center justify-content-between">
                                        <strong id="modalEndDisplay">11:00</strong>
                                        <input type="time" id="carScheduleEndTime" class="form-control form-control-sm w-auto" value="11:00">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mt-2 d-flex justify-content-between align-items-start flex-wrap gap-2">
                            <div id="modalSelectedRangeLabel" class="fw-semibold text-muted">Chưa chọn ngày</div>
                            <div id="modalConflictHint" class="small text-danger"></div>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="card h-100 border shadow-sm">
                            <div class="card-body d-flex flex-column">
                                <div class="d-flex align-items-baseline justify-content-between mb-3">
                                    <h6 class="mb-0">Lịch ngày <span id="modalScheduleDayLabel"></span></h6>
                                    <small id="modalScheduleDateHint" class="text-muted"></small>
                                </div>
                                <div class="schedule-list list-group list-group-flush" id="modalScheduleDetails">
                                    <div class="text-center text-muted small py-3">Chưa có lịch.</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Đóng</button>
                <button type="button" class="btn btn-success" id="applyScheduleSelection" disabled>Xác nhận lịch</button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
const carScheduleJsonData = <c:out value="${carScheduleJson}" escapeXml="false"/>;

// Global schedule data accessible to validateBookingForm
const rawEntries = Array.isArray(carScheduleJsonData) ? carScheduleJsonData : [];
const globalNormalizedSchedules = rawEntries
    .map((entry) => {
        if (!entry || !entry.start || !entry.end) {
            return null;
        }
        const start = new Date(entry.start);
        const end = new Date(entry.end);
        if (isNaN(start.getTime()) || isNaN(end.getTime())) {
            return null;
        }
        return {
            start,
            end,
            status: entry.status || 'Booked',
        };
    })
    .filter(Boolean)
    .sort((a, b) => a.start - b.start);

// Helper function to check same day
function isSameDayGlobal(a, b) {
    return a && b && a.getFullYear() === b.getFullYear()
        && a.getMonth() === b.getMonth()
        && a.getDate() === b.getDate();
}

// Helper function to format time range
function formatTimeGlobal(start, end) {
    const timeFormatter = new Intl.DateTimeFormat('vi', { hour: '2-digit', minute: '2-digit', hour12: false });
    const formatTime = (date) => {
        if (!date || isNaN(date.getTime())) {
            return '';
        }
        return timeFormatter.format(date);
    };
    const startStr = formatTime(start);
    const endStr = formatTime(end);
    if (!startStr || !endStr) {
        return 'Giờ không xác định';
    }
    return startStr + ' - ' + endStr;
}

function validateBookingForm() {
    const startDateTime = document.getElementById('carDetailStartTime').value;
    const endDateTime = document.getElementById('carDetailEndTime').value;

    if (!startDateTime || !endDateTime) {
        alert('Vui lòng chọn thời gian nhận và trả xe!');
        return false;
    }

    const selectedStart = new Date(startDateTime);
    const selectedEnd = new Date(endDateTime);

    if (selectedStart >= selectedEnd) {
        alert('Thời gian trả xe phải lớn hơn thời gian nhận.');
        return false;
    }

    // Check for schedule conflicts
    if (globalNormalizedSchedules && globalNormalizedSchedules.length > 0) {
        for (const schedule of globalNormalizedSchedules) {
            const scheduleStart = new Date(schedule.start);
            const scheduleEnd = new Date(schedule.end);

            // Check direct overlap
            if (selectedStart < scheduleEnd && selectedEnd > scheduleStart) {
                alert('Xe đã có lịch từ ' + formatTimeGlobal(schedule.start, schedule.end) + ' (' + schedule.status + '). Vui lòng chọn thời gian khác.');
                // Open the schedule modal
                const modalElement = document.getElementById('carScheduleModal');
                if (modalElement) {
                    const modal = new bootstrap.Modal(modalElement);
                    modal.show();
                }
                return false;
            }

            // Check 1 hour buffer after previous schedule
            const scheduleStartDay = new Date(scheduleStart);
            scheduleStartDay.setHours(0, 0, 0, 0);
            const scheduleEndDay = new Date(scheduleEnd);
            scheduleEndDay.setHours(0, 0, 0, 0);
            const selectedStartDay = new Date(selectedStart);
            selectedStartDay.setHours(0, 0, 0, 0);
            const selectedEndDay = new Date(selectedEnd);
            selectedEndDay.setHours(0, 0, 0, 0);

            // Check if schedule is before selected range (need 1 hour buffer)
            if (scheduleEndDay < selectedStartDay || (isSameDayGlobal(scheduleEndDay, selectedStartDay) && scheduleEnd < selectedStart)) {
                const hoursAfterPrev = (selectedStart - scheduleEnd) / (1000 * 60 * 60);
                if (hoursAfterPrev < 1) {
                    alert('Cần cách ít nhất 1 tiếng sau lịch trước. Xe đang có lịch đến ' + formatTimeGlobal(schedule.start, schedule.end) + '.');
                    const modalElement = document.getElementById('carScheduleModal');
                    if (modalElement) {
                        const modal = new bootstrap.Modal(modalElement);
                        modal.show();
                    }
                    return false;
                }
            }

            // Check if schedule is after selected range (need 2 hours buffer)
            if (scheduleStartDay > selectedEndDay || (isSameDayGlobal(scheduleStartDay, selectedEndDay) && scheduleStart > selectedEnd)) {
                const hoursBeforeNext = (scheduleStart - selectedEnd) / (1000 * 60 * 60);
                if (hoursBeforeNext < 2) {
                    alert('Cần ít nhất 2 tiếng trước lịch tiếp theo. Xe có lịch tiếp theo từ ' + formatTimeGlobal(schedule.start, schedule.end) + '.');
                    const modalElement = document.getElementById('carScheduleModal');
                    if (modalElement) {
                        const modal = new bootstrap.Modal(modalElement);
                        modal.show();
                    }
                    return false;
                }
            }
        }
    }

    return true;
}

document.addEventListener('DOMContentLoaded', function() {
    setupDetailDateInputs();
    initCarScheduleCalendar();
});

function setupDetailDateInputs() {
    const startInput = document.getElementById('carDetailStartTime');
    const endInput = document.getElementById('carDetailEndTime');
    if (!startInput || !endInput) {
        return;
    }

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

    if (!startInput.value) {
        const now = new Date();
        now.setMinutes(0, 0, 0);
        now.setHours(now.getHours() + 1);
        startInput.value = formatDateTimeLocal(now);

        const endDate = new Date(now);
        endDate.setHours(endDate.getHours() + 4);
        endInput.value = formatDateTimeLocal(endDate);
    }

    if (!endInput.value) {
        const sDate = new Date(startInput.value);
        const endDate2 = new Date(sDate);
        endDate2.setHours(endDate2.getHours() + 4);
        endInput.value = formatDateTimeLocal(endDate2);
    }

    function updateEndMin() {
        const startDate = new Date(startInput.value);
        if (!isNaN(startDate.getTime())) {
            const minEnd = new Date(startDate.getTime() + 60 * 60 * 1000);
            endInput.min = formatDateTimeLocal(minEnd);
        }
    }

    setStartMin();
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
}

function initCarScheduleCalendar() {
    const modalElement = document.getElementById('carScheduleModal');
    if (!modalElement) {
        return;
    }

    const detailStartInput = document.getElementById('carDetailStartTime');
    const detailEndInput = document.getElementById('carDetailEndTime');
    const startTimeInput = document.getElementById('carScheduleStartTime');
    const endTimeInput = document.getElementById('carScheduleEndTime');
    const startDisplay = document.getElementById('modalStartDisplay');
    const endDisplay = document.getElementById('modalEndDisplay');
    const rangeLabel = document.getElementById('modalSelectedRangeLabel');
    const conflictHint = document.getElementById('modalConflictHint');
    const scheduleDetails = document.getElementById('modalScheduleDetails');
    const scheduleDayLabel = document.getElementById('modalScheduleDayLabel');
    const scheduleDateHint = document.getElementById('modalScheduleDateHint');
    const calendarGrid = document.getElementById('carScheduleCalendarGrid');
    const monthLabel = document.getElementById('carScheduleMonthLabel');
    const prevMonthBtn = document.getElementById('carSchedulePrevMonth');
    const nextMonthBtn = document.getElementById('carScheduleNextMonth');
    const applyButton = document.getElementById('applyScheduleSelection');

    const rawEntries = Array.isArray(carScheduleJsonData) ? carScheduleJsonData : [];
    const normalizedSchedules = rawEntries
        .map((entry) => {
            if (!entry || !entry.start || !entry.end) {
                return null;
            }
            const start = new Date(entry.start);
            const end = new Date(entry.end);
            if (isNaN(start.getTime()) || isNaN(end.getTime())) {
                return null;
            }
            return {
                start,
                end,
                status: entry.status || 'Booked',
            };
        })
        .filter(Boolean)
        .sort((a, b) => a.start - b.start);

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const monthFormatter = new Intl.DateTimeFormat('vi', { month: 'long', year: 'numeric' });
    const weekdayFormatter = new Intl.DateTimeFormat('vi', { weekday: 'long' });
    const dateFormatter = new Intl.DateTimeFormat('vi', { day: '2-digit', month: '2-digit', year: 'numeric' });
    const timeFormatter = new Intl.DateTimeFormat('vi', { hour: '2-digit', minute: '2-digit', hour12: false });

    let selectionStartDate = null;
    let selectionEndDate = null;
    let activeCalendarDate = new Date(today);
    let currentMonthDate = new Date(today.getFullYear(), today.getMonth(), 1);

    function isSameDay(a, b) {
        return a && b && a.getFullYear() === b.getFullYear()
            && a.getMonth() === b.getMonth()
            && a.getDate() === b.getDate();
    }

    function isDateBlocked(date) {
        // Don't completely block dates - allow selection and let conflict check handle buffer rules
        // This gives users more flexibility to choose dates with potential time slots
        return false;
    }

    function combineDateWithTime(date, timeValue) {
        if (!date || !timeValue) {
            return null;
        }
        const [hours, minutes] = timeValue.split(':').map(Number);
        if (!Number.isFinite(hours) || !Number.isFinite(minutes)) {
            return null;
        }
        const result = new Date(date);
        result.setHours(hours, minutes, 0, 0);
        return result;
    }

    function findScheduleConflict() {
        if (!selectionStartDate || !selectionEndDate || !startTimeInput || !endTimeInput) {
            return null;
        }
        const selectedStartTime = combineDateWithTime(selectionStartDate, startTimeInput.value || '07:00');
        const selectedEndTime = combineDateWithTime(selectionEndDate, endTimeInput.value || '11:00');
        if (!selectedStartTime || !selectedEndTime || selectedEndTime <= selectedStartTime) {
            return null;
        }
        // Check each schedule for conflicts with buffer rules
        for (const schedule of normalizedSchedules) {
            const scheduleStart = new Date(schedule.start);
            const scheduleEnd = new Date(schedule.end);
            const scheduleStartDay = new Date(scheduleStart);
            scheduleStartDay.setHours(0, 0, 0, 0);
            const scheduleEndDay = new Date(scheduleEnd);
            scheduleEndDay.setHours(0, 0, 0, 0);
            const selectedStartDay = new Date(selectedStartTime);
            selectedStartDay.setHours(0, 0, 0, 0);
            const selectedEndDay = new Date(selectedEndTime);
            selectedEndDay.setHours(0, 0, 0, 0);

            // Case 1: Direct overlap (schedules within selected range)
            if (scheduleStart < selectedEndTime && scheduleEnd > selectedStartTime) {
                return schedule;
            }

            // Case 2: Schedule is after selected range (next booking)
            if (scheduleStartDay > selectedEndDay || (isSameDay(scheduleStartDay, selectedEndDay) && scheduleStart > selectedEndTime)) {
                // Need 2 hours buffer before next schedule
                const hoursBeforeNext = (scheduleStart - selectedEndTime) / (1000 * 60 * 60);
                if (hoursBeforeNext < 2) {
                    return schedule;
                }
            }

            // Case 3: Schedule is before selected range (previous booking)
            if (scheduleEndDay < selectedStartDay || (isSameDay(scheduleEndDay, selectedStartDay) && scheduleEnd < selectedStartTime)) {
                // Need 1 hour buffer after previous schedule
                const hoursAfterPrev = (selectedStartTime - scheduleEnd) / (1000 * 60 * 60);
                if (hoursAfterPrev < 1) {
                    return schedule;
                }
            }
        }
        return null;
    }

    function formatDisplayDate(date) {
        return dateFormatter.format(date);
    }

    function formatTime(date) {
        if (!date || isNaN(date.getTime())) {
            return '';
        }
        return timeFormatter.format(date);
    }

    function formatTimeRange(start, end) {
        const startStr = formatTime(start);
        const endStr = formatTime(end);
        if (!startStr || !endStr) {
            return 'Giờ không xác định';
        }
        return startStr + ' - ' + endStr;
    }

    function renderScheduleDetails(date) {
        const focusDate = date && !isNaN(date.getTime()) ? new Date(date) : new Date(today);
        focusDate.setHours(0, 0, 0, 0);
        scheduleDayLabel.textContent = formatDisplayDate(focusDate);
        scheduleDateHint.textContent = weekdayFormatter.format(focusDate);
        const dayStart = new Date(focusDate);
        const dayEnd = new Date(dayStart);
        dayEnd.setDate(dayEnd.getDate() + 1);
        const daySchedules = normalizedSchedules.filter((schedule) => schedule.start < dayEnd && schedule.end > dayStart);
        const conflict = findScheduleConflict();
        scheduleDetails.innerHTML = '';
        if (!daySchedules.length) {
            scheduleDetails.innerHTML = '<div class="text-center text-muted small py-3">Không có lịch trong ngày.</div>';
            return;
        }
        daySchedules.forEach((schedule) => {
            const item = document.createElement('div');
            item.className = 'list-group-item border-0 px-0 py-2';
            if (conflict && schedule === conflict) {
                item.classList.add('border', 'border-danger', 'rounded');
            }
            // Get the actual date parts from schedule start/end
            const schedStartDate = new Date(schedule.start);
            schedStartDate.setHours(0, 0, 0, 0);
            const schedEndDate = new Date(schedule.end);
            schedEndDate.setHours(0, 0, 0, 0);

            const isFirstDay = isSameDay(focusDate, schedStartDate);
            const isMiddleDay = focusDate > schedStartDate && focusDate < schedEndDate;
            const isLastDay = isSameDay(focusDate, schedEndDate);

            let effectiveStart = new Date(focusDate);
            let effectiveEnd = new Date(focusDate);

            if (isFirstDay) {
                // First day: from schedule start time to end of day
                effectiveStart = new Date(schedule.start);
                effectiveEnd.setHours(23, 59, 59, 999);
            } else if (isMiddleDay) {
                // Middle day: full day
                effectiveEnd.setHours(23, 59, 59, 999);
            } else if (isLastDay) {
                // Last day: from start of day to schedule end time
                effectiveEnd = new Date(schedule.end);
            } else {
                // Fallback - shouldn't happen if filter is correct
                effectiveStart = new Date(schedule.start);
                effectiveEnd = new Date(schedule.end);
            }
            item.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="fw-semibold">\${formatTimeRange(effectiveStart, effectiveEnd)}</div>
                        <div class="small text-muted">\${schedule.status}</div>
                    </div>
                </div>
            `;
            scheduleDetails.appendChild(item);
        });
    }

    function renderCalendar() {
        if (!calendarGrid) {
            return;
        }
        calendarGrid.innerHTML = '';
        const firstDay = new Date(currentMonthDate);
        firstDay.setDate(1);
        const firstWeekday = firstDay.getDay();
        for (let i = 0; i < firstWeekday; i += 1) {
            const placeholder = document.createElement('div');
            placeholder.className = 'calendar-day placeholder';
            calendarGrid.appendChild(placeholder);
        }
        const daysInMonth = new Date(currentMonthDate.getFullYear(), currentMonthDate.getMonth() + 1, 0).getDate();
        for (let day = 1; day <= daysInMonth; day += 1) {
            const date = new Date(currentMonthDate.getFullYear(), currentMonthDate.getMonth(), day);
            const cell = document.createElement('button');
            cell.type = 'button';
            cell.className = 'calendar-day';
            cell.textContent = day;
            // Only block past dates, not dates with schedules (buffer rules will handle availability)
            const isPast = date < today;
            const dayStart = new Date(date);
            dayStart.setHours(0, 0, 0, 0);
            const dayEnd = new Date(dayStart);
            dayEnd.setDate(dayEnd.getDate() + 1);
            const hasSchedule = normalizedSchedules.some((schedule) => schedule.start < dayEnd && schedule.end > dayStart);
            if (isPast) {
                cell.classList.add('calendar-day--blocked');
                cell.setAttribute('aria-disabled', 'true');
            } else if (hasSchedule) {
                // Add indicator for dates with existing schedules
                cell.classList.add('calendar-day--has-schedule');
            }
            if (isSameDay(date, today)) {
                cell.classList.add('calendar-day--today');
            }
            if (selectionStartDate && isSameDay(date, selectionStartDate)) {
                cell.classList.add('calendar-day--start');
            }
            if (selectionEndDate && isSameDay(date, selectionEndDate)) {
                cell.classList.add('calendar-day--end');
            }
            if (selectionStartDate && selectionEndDate && date > selectionStartDate && date < selectionEndDate) {
                cell.classList.add('calendar-day--range');
            }
            if (isSameDay(date, activeCalendarDate)) {
                cell.classList.add('calendar-day--active');
            }
            cell.addEventListener('click', () => handleCalendarDayClick(date));
            calendarGrid.appendChild(cell);
        }
        if (monthLabel) {
            monthLabel.textContent = monthFormatter.format(currentMonthDate);
        }
        if (prevMonthBtn) {
            const minMonth = new Date(today.getFullYear(), today.getMonth(), 1);
            prevMonthBtn.disabled = currentMonthDate.getTime() <= minMonth.getTime();
        }
    }

    function handleCalendarDayClick(date) {
        const normalized = new Date(date);
        normalized.setHours(0, 0, 0, 0);
        activeCalendarDate = new Date(normalized);
        // Don't block dates - allow selection, buffer rules handle conflict
        if (normalized < today) {
            refreshModalView();
            return;
        }
        // First click: start date (ngày nhận), Second click: end date (ngày trả)
        if (!selectionStartDate || (selectionStartDate && selectionEndDate)) {
            // Start new selection: first click = start date
            selectionStartDate = new Date(normalized);
            selectionEndDate = null;
        } else if (normalized.getTime() < selectionStartDate.getTime()) {
            // Clicked before start date, reset with new start date
            selectionStartDate = new Date(normalized);
            selectionEndDate = null;
        } else {
            // Second click after start date = end date
            selectionEndDate = new Date(normalized);
        }
        refreshModalView();
    }

    function updateRangeAndConflict() {
        const startTimeValue = startTimeInput ? (startTimeInput.value || '07:00') : '07:00';
        const endTimeValue = endTimeInput ? (endTimeInput.value || '11:00') : '11:00';
        if (startDisplay) {
            startDisplay.textContent = startTimeValue;
        }
        if (endDisplay) {
            endDisplay.textContent = endTimeValue;
        }
        if (selectionStartDate && selectionEndDate) {
            if (isSameDay(selectionStartDate, selectionEndDate)) {
                rangeLabel.textContent = formatDisplayDate(selectionStartDate) + ' (cùng ngày)';
            } else {
                rangeLabel.textContent = formatDisplayDate(selectionStartDate) + ' → ' + formatDisplayDate(selectionEndDate);
            }
        } else if (selectionStartDate) {
            rangeLabel.textContent = 'Chọn ngày trả sau ' + formatDisplayDate(selectionStartDate);
        } else {
            rangeLabel.textContent = 'Chưa chọn ngày';
        }

        const conflict = findScheduleConflict();
        if (conflict) {
            const conflictDate = new Date(conflict.start);
            conflictDate.setHours(0, 0, 0, 0);
            activeCalendarDate = conflictDate;
            currentMonthDate = new Date(conflictDate.getFullYear(), conflictDate.getMonth(), 1);
            conflictHint.textContent = 'Xe đang có lịch ' + formatTimeRange(conflict.start, conflict.end) + ' (' + conflict.status + ')';
            if (applyButton) {
                applyButton.disabled = true;
            }
        } else {
            if (conflictHint) {
                conflictHint.textContent = '';
            }
            if (applyButton) {
                // Enable button only when both start and end dates are selected
                applyButton.disabled = !(selectionStartDate && selectionEndDate);
            }
        }
    }

    function refreshModalView() {
        updateRangeAndConflict();
        renderCalendar();
        renderScheduleDetails(activeCalendarDate);
    }

    function parseDatePart(value) {
        if (!value) {
            return null;
        }
        const parsed = new Date(value);
        if (isNaN(parsed.getTime())) {
            return null;
        }
        parsed.setHours(0, 0, 0, 0);
        return parsed;
    }

    function parseTimePart(value) {
        if (!value || !value.includes('T')) {
            return null;
        }
        return value.split('T')[1].slice(0, 5);
    }

    function formatInputDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return year + '-' + month + '-' + day;
    }

    function syncSelectionFromForm() {
        selectionStartDate = parseDatePart(detailStartInput ? detailStartInput.value : null);
        selectionEndDate = parseDatePart(detailEndInput ? detailEndInput.value : null);
        const startTimeValue = parseTimePart(detailStartInput ? detailStartInput.value : null);
        const endTimeValue = parseTimePart(detailEndInput ? detailEndInput.value : null);
        if (startTimeValue && startTimeInput) {
            startTimeInput.value = startTimeValue;
        }
        if (endTimeValue && endTimeInput) {
            endTimeInput.value = endTimeValue;
        }
        if (selectionStartDate && !selectionEndDate) {
            activeCalendarDate = new Date(selectionStartDate);
        } else if (selectionEndDate) {
            activeCalendarDate = new Date(selectionEndDate);
        } else {
            activeCalendarDate = new Date(today);
        }
        if (selectionStartDate) {
            currentMonthDate = new Date(selectionStartDate.getFullYear(), selectionStartDate.getMonth(), 1);
        } else if (selectionEndDate) {
            currentMonthDate = new Date(selectionEndDate.getFullYear(), selectionEndDate.getMonth(), 1);
        } else {
            currentMonthDate = new Date(today.getFullYear(), today.getMonth(), 1);
        }
        refreshModalView();
    }

    if (startTimeInput) {
        startTimeInput.addEventListener('change', refreshModalView);
    }
    if (endTimeInput) {
        endTimeInput.addEventListener('change', refreshModalView);
    }
    if (prevMonthBtn) {
        prevMonthBtn.addEventListener('click', () => {
            const minMonth = new Date(today.getFullYear(), today.getMonth(), 1);
            if (currentMonthDate.getTime() <= minMonth.getTime()) {
                return;
            }
            currentMonthDate = new Date(currentMonthDate.getFullYear(), currentMonthDate.getMonth() - 1, 1);
            refreshModalView();
        });
    }
    if (nextMonthBtn) {
        nextMonthBtn.addEventListener('click', () => {
            currentMonthDate = new Date(currentMonthDate.getFullYear(), currentMonthDate.getMonth() + 1, 1);
            refreshModalView();
        });
    }
    if (applyButton) {
        applyButton.addEventListener('click', () => {
            if (!selectionStartDate || !selectionEndDate || applyButton.disabled) {
                return;
            }
            const startTimeValue = startTimeInput ? (startTimeInput.value || '07:00') : '07:00';
            const endTimeValue = endTimeInput ? (endTimeInput.value || '11:00') : '11:00';
            if (detailStartInput) {
                detailStartInput.value = formatInputDate(selectionStartDate) + 'T' + startTimeValue;
            }
            if (detailEndInput) {
                detailEndInput.value = formatInputDate(selectionEndDate) + 'T' + endTimeValue;
            }
            if (typeof bootstrap !== 'undefined') {
                const modalInstance = bootstrap.Modal.getInstance(modalElement);
                if (modalInstance) {
                    modalInstance.hide();
                }
            }
        });
    }

    modalElement.addEventListener('show.bs.modal', syncSelectionFromForm);
    syncSelectionFromForm();
}
</script>
</body>
</html>
