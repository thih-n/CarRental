<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý xe - Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <style>
            body {
                background: #f4f6f9;
            }
            .admin-wrapper {
                min-height: 100vh;
            }
            .sidebar {
                width: 280px;
                background: #111827;
                color: #fff;
                position: sticky;
                top: 0;
                height: 100vh;
            }
            .sidebar .brand {
                padding: 20px;
                border-bottom: 1px solid rgba(255,255,255,.1);
                font-weight: 700;
                font-size: 1.1rem;
            }
            .sidebar .nav-link {
                color: #cbd5e1;
                border-radius: 10px;
                margin: 4px 12px;
                padding: 10px 14px;
            }
            .sidebar .nav-link:hover,
            .sidebar .nav-link.active {
                background: #1f2937;
                color: #fff;
            }
            .content-area {
                flex: 1;
            }
            .topbar {
                background: #fff;
                border-bottom: 1px solid #e5e7eb;
                padding: 14px 24px;
            }
            .dashboard-body {
                padding: 24px;
            }
            .car-image {
                width: 80px;
                height: 60px;
                object-fit: cover;
                border-radius: 4px;
            }
            .search-box {
                max-width: 300px;
            }
            .filter-status {
                max-width: 150px;
            }
        </style>
    </head>
    <body>
        <div class="d-flex admin-wrapper">
            <aside class="sidebar d-flex flex-column">
                <div class="brand d-flex align-items-center gap-2">
                    <i class="bi bi-speedometer2"></i>
                    <span>Admin Panel</span>
                </div>

                <nav class="nav flex-column py-3">
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-house-door me-2"></i>Dashboard</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/refunds"><i class="bi bi-arrow-return-left me-2"></i>Hoàn tiền</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/users?type=customer"><i class="bi bi-people me-2"></i>Quản lý người dùng</a>
                    <a class="nav-link active" href="${pageContext.request.contextPath}/admin/cars"><i class="bi bi-car-front me-2"></i>Quản lý xe</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings"><i class="bi bi-calendar2-check me-2"></i>Quản lý booking</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/locations"><i class="bi bi-geo-alt me-2"></i>Địa điểm nhận/trả</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/admin/reviews"><i class="bi bi-star me-2"></i>Đánh giá khách hàng</a>
<a class="nav-link" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-cash-coin me-2"></i>Doanh thu</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>                </nav>

                <div class="mt-auto p-3">
                    <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                        <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                    </a>
                </div>
            </aside>

            <main class="content-area">
                <div class="topbar d-flex justify-content-between align-items-center">
                    <div>
                        <h5 class="mb-0">Quản lý xe</h5>
                        <small class="text-muted">Quản lý danh sách xe trong hệ thống</small>
                    </div>
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/admin/cars?action=create">
                        <i class="bi bi-plus-lg me-1"></i>Thêm xe mới
                    </a>
                </div>

                <div class="dashboard-body">
                    <!-- Tabs -->
                    <ul class="nav nav-tabs mb-4" id="carManagementTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link ${currentTab == 'cars' || empty currentTab ? 'active' : ''}" id="cars-tab" data-bs-toggle="tab" data-bs-target="#cars-panel" type="button">
                                <i class="bi bi-car-front me-1"></i> Quản lý xe
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link ${currentTab == 'brands' ? 'active' : ''}" id="brands-tab" data-bs-toggle="tab" data-bs-target="#brands-panel" type="button">
                                <i class="bi bi-building me-1"></i> Quản lý hãng xe
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link ${currentTab == 'cartypes' ? 'active' : ''}" id="cartypes-tab" data-bs-toggle="tab" data-bs-target="#cartypes-panel" type="button">
                                <i class="bi bi-grid me-1"></i> Quản lý loại xe
                            </button>
                        </li>
                    </ul>

                    <div class="tab-content" id="carManagementTabsContent">
                        <div class="tab-pane fade ${currentTab == 'cars' || empty currentTab ? 'show active' : ''}" id="cars-panel" role="tabpanel">

                            <c:if test="${param.success == 'add'}">
                                <div class="alert alert-success">Thêm xe mới thành công.</div>
                            </c:if>
                            <c:if test="${param.success == 'update'}">
                                <div class="alert alert-success">Cập nhật xe thành công.</div>
                            </c:if>
                            <c:if test="${param.locked == '1'}">
                                <div class="alert alert-success">Khóa xe thành công. ${param.cancelledBookings} đơn đã bị hủy.</div>
                            </c:if>
                            <c:if test="${param.unlocked == '1'}">
                                <div class="alert alert-success">Mở khóa xe thành công.</div>
                            </c:if>

                            <!-- Search and Filter -->
                            <form method="get" class="row g-3 mb-4">
                                <div class="col-md-3">
                                    <div class="input-group search-box">
                                        <input type="text" name="search" class="form-control" placeholder="Tìm kiếm theo tên xe, biển số..." value="${param.search}">
                                        <button type="submit" class="btn btn-outline-secondary">
                                            <i class="bi bi-search"></i>
                                        </button>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <input type="text" name="brandSearch" class="form-control" placeholder="Tìm theo hãng xe..." value="${param.brandSearch}">
                                </div>
                                <div class="col-md-2">
                                    <select name="status" class="form-select filter-status" onchange="this.form.submit()">
                                        <option value="">Tất cả trạng thái</option>
                                        <c:forEach var="status" items="${carStatuses}">
                                            <option value="${status}" ${param.status == status ? 'selected' : ''}>${status}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <select name="type" class="form-select" onchange="this.form.submit()">
                                        <option value="">Tất cả loại xe</option>
                                        <c:forEach var="type" items="${carTypes}">
                                            <option value="${type.typeName}" ${param.type == type.typeName ? 'selected' : ''}>${type.typeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-2">
                                    <a href="${pageContext.request.contextPath}/admin/cars" class="btn btn-outline-secondary">Xóa filter</a>
                                </div>
                            </form>

                            <div class="card">
                                <div class="card-header bg-white fw-semibold">
                                    Danh sách xe <span class="text-muted fw-normal ms-2">(${cars.size()} xe)</span>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>STT</th>
                                                <th>Hình ảnh</th>
                                                <th>Tên xe</th>
                                                <th>Biển số</th>
                                                <th>Loại xe</th>
                                                <th>Giá thuê/ngày</th>
                                                <th>Trạng thái</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="car" items="${cars}" varStatus="status">
                                                <tr>
                                                    <td>${status.index + 1}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty car.imageURL}">
                                                                <img src="${car.imageURL}" alt="${car.carName}" class="car-image">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="car-image bg-secondary d-flex align-items-center justify-content-center text-white">
                                                                    <i class="bi bi-car-front"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${car.carName}</td>
                                                    <td>${car.plateNumber}</td>
                                                    <td>${car.typeName}</td>
                                                    <td>${car.defaultPricePerDay}đ</td>
                                                    <td>
                                                        <span class="badge ${car.statusName == 'Available' || car.statusName == 'Hoạt động' ? 'bg-success' : car.statusName == 'Maintenance' || car.statusName == 'Bảo trì' ? 'bg-warning' : 'bg-secondary'}">
                                                            ${car.statusName}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/admin/cars?action=edit&id=${car.carID}" class="btn btn-sm btn-outline-dark">
                                                            <i class="bi bi-pencil"></i>
                                                        </a>
                                                        <c:choose>
                                                            <c:when test="${car.statusName == 'Available' || car.statusName == 'Hoạt động'}">
                                                                <button type="button" class="btn btn-sm btn-outline-warning" 
                                                                        data-bs-toggle="modal" data-bs-target="#lockCarModal"
                                                                        data-carid="${car.carID}" data-carname="${car.carName}"
                                                                        onclick="showLockModal(this)">
                                                                    <i class="bi bi-lock"></i>
                                                                </button>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a href="${pageContext.request.contextPath}/admin/cars?action=unlock&id=${car.carID}" 
                                                                   class="btn btn-sm btn-outline-success" onclick="return confirm('Bạn có chắc muốn mở khóa xe này?')">
                                                                    <i class="bi bi-unlock"></i>
                                                                </a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty cars}">
                                                <tr>
                                                    <td colspan="8" class="text-center text-muted py-3">Chưa có xe nào.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                                <!-- Pagination -->
                                <c:if test="${totalCarPages > 1}">
                                    <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                                        <span class="text-muted">Trang ${currentPage} / ${totalCarPages} (${totalCars} xe)</span>
                                        <nav>
                                            <ul class="pagination mb-0">
                                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=${currentPage - 1}&search=${param.search}&status=${param.status}&type=${param.type}&brandSearch=${param.brandSearch}">Trước</a>
                                                </li>
                                                <c:forEach begin="1" end="${totalCarPages}" var="i">
                                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                        <a class="page-link" href="?page=${i}&search=${param.search}&status=${param.status}&type=${param.type}&brandSearch=${param.brandSearch}">${i}</a>
                                                    </li>
                                                </c:forEach>
                                                <li class="page-item ${currentPage == totalCarPages ? 'disabled' : ''}">
                                                    <a class="page-link" href="?page=${currentPage + 1}&search=${param.search}&status=${param.status}&type=${param.type}&brandSearch=${param.brandSearch}">Sau</a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- Brands Panel -->
                        <div class="tab-pane fade ${currentTab == 'brands' ? 'show active' : ''}" id="brands-panel" role="tabpanel">
                            <!-- Search Brands -->
                            <form method="get" class="row g-3 mb-3">
                                <input type="hidden" name="tab" value="brands">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <input type="text" name="brandKeyword" class="form-control" placeholder="Tìm kiếm hãng xe..." value="${brandKeyword}">
                                        <button type="submit" class="btn btn-outline-secondary">
                                            <i class="bi bi-search"></i>
                                        </button>
                                    </div>
                                </div>
                            </form>

                            <div class="card">
                                <div class="card-header bg-white fw-semibold d-flex justify-content-between align-items-center">
                                    <span>Danh sách hãng xe <span class="text-muted fw-normal">(${totalBrands} hãng)</span></span>
                                    <button class="btn btn-dark btn-sm" data-bs-toggle="modal" data-bs-target="#addBrandModal">
                                        <i class="bi bi-plus-lg me-1"></i> Thêm hãng xe
                                    </button>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>STT</th>
                                                <th>Tên hãng xe</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="brand" items="${brands}" varStatus="status">
                                                <tr>
                                                    <td>${(brandPage - 1) * 10 + status.index + 1}</td>
                                                    <td>${brand.brandName}</td>
                                                    <td>
                                                        <button class="btn btn-sm btn-outline-dark" data-bs-toggle="modal" data-bs-target="#editBrandModal"
                                                                data-brand-id="${brand.brandID}" data-brand-name="${brand.brandName}">
                                                            <i class="bi bi-pencil"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty brands}">
                                                <tr>
                                                    <td colspan="3" class="text-center text-muted py-3">Chưa có hãng xe nào.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                                <c:if test="${totalBrandPages > 1}">
                                    <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                                        <span class="text-muted">Trang ${brandPage} / ${totalBrandPages}</span>
                                        <nav>
                                            <ul class="pagination mb-0">
                                                <li class="page-item ${brandPage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="?brandPage=${brandPage - 1}&brandKeyword=${brandKeyword}&tab=brands">Trước</a>
                                                </li>
                                                <c:forEach begin="1" end="${totalBrandPages}" var="i">
                                                    <li class="page-item ${brandPage == i ? 'active' : ''}">
                                                        <a class="page-link" href="?brandPage=${i}&brandKeyword=${brandKeyword}&tab=brands">${i}</a>
                                                    </li>
                                                </c:forEach>
                                                <li class="page-item ${brandPage == totalBrandPages ? 'disabled' : ''}">
                                                    <a class="page-link" href="?brandPage=${brandPage + 1}&brandKeyword=${brandKeyword}&tab=brands">Sau</a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- CarTypes Panel -->
                        <div class="tab-pane fade ${currentTab == 'cartypes' ? 'show active' : ''}" id="cartypes-panel" role="tabpanel">
                            <!-- Search CarTypes -->
                            <form method="get" class="row g-3 mb-3">
                                <input type="hidden" name="tab" value="cartypes">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <input type="text" name="carTypeKeyword" class="form-control" placeholder="Tìm kiếm loại xe..." value="${carTypeKeyword}">
                                        <button type="submit" class="btn btn-outline-secondary">
                                            <i class="bi bi-search"></i>
                                        </button>
                                    </div>
                                </div>
                            </form>

                            <div class="card">
                                <div class="card-header bg-white fw-semibold d-flex justify-content-between align-items-center">
                                    <span>Danh sách loại xe <span class="text-muted fw-normal">(${carTypeKeyword == '' ? totalCarTypes : carTypesList.size()} loại)</span></span>
                                    <button class="btn btn-dark btn-sm" data-bs-toggle="modal" data-bs-target="#addCarTypeModal">
                                        <i class="bi bi-plus-lg me-1"></i> Thêm loại xe
                                    </button>
                                </div>
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>STT</th>
                                                <th>Tên loại xe</th>
                                                <th>Số chỗ</th>
                                                <th>Mô tả</th>
                                                <th>Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="ct" items="${carTypesList}" varStatus="status">
                                                <tr>
                                                    <td>${(carTypePage - 1) * 10 + status.index + 1}</td>
                                                    <td>${ct.typeName}</td>
                                                    <td>${ct.seatCount}</td>
                                                    <td>${ct.description}</td>
                                                    <td>
                                                        <button class="btn btn-sm btn-outline-dark" data-bs-toggle="modal" data-bs-target="#editCarTypeModal"
                                                                data-type-id="${ct.typeID}" data-type-name="${ct.typeName}" 
                                                                data-seat-count="${ct.seatCount}" data-description="${ct.description}">
                                                            <i class="bi bi-pencil"></i>
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            <c:if test="${empty carTypesList}">
                                                <tr>
                                                    <td colspan="5" class="text-center text-muted py-3">Chưa có loại xe nào.</td>
                                                </tr>
                                            </c:if>
                                        </tbody>
                                    </table>
                                </div>
                                <c:if test="${totalCarTypePages > 1}">
                                    <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                                        <span class="text-muted">Trang ${carTypePage} / ${totalCarTypePages}</span>
                                        <nav>
                                            <ul class="pagination mb-0">
                                                <li class="page-item ${carTypePage == 1 ? 'disabled' : ''}">
                                                    <a class="page-link" href="?carTypePage=${carTypePage - 1}&carTypeKeyword=${carTypeKeyword}&tab=cartypes">Trước</a>
                                                </li>
                                                <c:forEach begin="1" end="${totalCarTypePages}" var="i">
                                                    <li class="page-item ${carTypePage == i ? 'active' : ''}">
                                                        <a class="page-link" href="?carTypePage=${i}&carTypeKeyword=${carTypeKeyword}&tab=cartypes">${i}</a>
                                                    </li>
                                                </c:forEach>
                                                <li class="page-item ${carTypePage == totalCarTypePages ? 'disabled' : ''}">
                                                    <a class="page-link" href="?carTypePage=${carTypePage + 1}&carTypeKeyword=${carTypeKeyword}&tab=cartypes">Sau</a>
                                                </li>
                                            </ul>
                                        </nav>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                    </div>
            </main>
        </div>

        <!-- Lock Car Modal -->
        <div class="modal fade" id="lockCarModal" tabindex="-1" aria-labelledby="lockCarModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="lockCarModalLabel">Xác nhận khóa xe</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc muốn khóa xe <strong id="lockCarName"></strong>?</p>

                        <div id="ongoingCarTripInfo" class="alert alert-info" style="display: none;">
                            <i class="bi bi-info-circle me-2"></i>
                            <span id="ongoingCarTripText"></span>
                        </div>

                        <div id="cancelledCarTripsSection">
                            <h6 class="mb-3">Các chuyến sẽ bị hủy:</h6>
                            <div id="cancelledCarTripsList" class="table-responsive" style="max-height: 300px; overflow-y: auto;">
                                <table class="table table-sm table-bordered">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Mã hợp đồng</th>
                                            <th>Khách hàng</th>
                                            <th>Ngày nhận</th>
                                            <th>Ngày trả</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody id="cancelledCarTripsBody">
                                        <tr><td colspan="5" class="text-center text-muted">Đang tải...</td></tr>
                                    </tbody>
                                </table>
                            </div>
                            <p class="mt-2 text-muted"><small>Số chuyến sẽ hủy: <span id="cancelledCarCount">0</span></small></p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <form id="lockCarForm" method="post">
                            <input type="hidden" name="action" value="confirmLock">
                            <input type="hidden" name="id" id="lockCarID">
                            <button type="submit" class="btn btn-warning">Xác nhận khóa và hủy chuyến</button>
                        </form>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Add Brand Modal -->
        <div class="modal fade" id="addBrandModal" tabindex="-1" aria-labelledby="addBrandModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addBrandModalLabel">Thêm hãng xe mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="addBrandForm">
                        <div class="modal-body">
                            <div class="mb-3">
                                <label for="newBrandName" class="form-label">Tên hãng xe</label>
                                <input type="text" class="form-control" id="newBrandName" name="brandName" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-dark">Thêm mới</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Brand Modal -->
        <div class="modal fade" id="editBrandModal" tabindex="-1" aria-labelledby="editBrandModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editBrandModalLabel">Sửa hãng xe</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="editBrandForm">
                        <div class="modal-body">
                            <input type="hidden" id="editBrandID" name="brandID">
                            <div class="mb-3">
                                <label for="editBrandName" class="form-label">Tên hãng xe</label>
                                <input type="text" class="form-control" id="editBrandName" name="brandName" required>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-dark">Lưu thay đổi</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete Brand Modal -->
        <div class="modal fade" id="deleteBrandModal" tabindex="-1" aria-labelledby="deleteBrandModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteBrandModalLabel">Xóa hãng xe</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc muốn xóa hãng xe <strong id="deleteBrandName"></strong>?</p>
                        <p class="text-muted">Lưu ý: Hãng xe đang được sử dụng bởi các xe sẽ không thể xóa.</p>
                        <input type="hidden" id="deleteBrandID">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger" id="confirmDeleteBrand">Xóa</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Add CarType Modal -->
        <div class="modal fade" id="addCarTypeModal" tabindex="-1" aria-labelledby="addCarTypeModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addCarTypeModalLabel">Thêm loại xe mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="addCarTypeForm">
                        <div class="modal-body">
                            <div class="mb-3">
                                <label for="newTypeName" class="form-label">Tên loại xe</label>
                                <input type="text" class="form-control" id="newTypeName" name="typeName" required>
                            </div>
                            <div class="mb-3">
                                <label for="newSeatCount" class="form-label">Số chỗ ngồi</label>
                                <input type="number" class="form-control" id="newSeatCount" name="seatCount" value="5" min="1" max="50">
                            </div>
                            <div class="mb-3">
                                <label for="newDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="newDescription" name="description" rows="2"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-dark">Thêm mới</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit CarType Modal -->
        <div class="modal fade" id="editCarTypeModal" tabindex="-1" aria-labelledby="editCarTypeModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editCarTypeModalLabel">Sửa loại xe</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form id="editCarTypeForm">
                        <div class="modal-body">
                            <input type="hidden" id="editTypeID" name="typeID">
                            <div class="mb-3">
                                <label for="editTypeName" class="form-label">Tên loại xe</label>
                                <input type="text" class="form-control" id="editTypeName" name="typeName" required>
                            </div>
                            <div class="mb-3">
                                <label for="editSeatCount" class="form-label">Số chỗ ngồi</label>
                                <input type="number" class="form-control" id="editSeatCount" name="seatCount" min="1" max="50">
                            </div>
                            <div class="mb-3">
                                <label for="editDescription" class="form-label">Mô tả</label>
                                <textarea class="form-control" id="editDescription" name="description" rows="2"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-dark">Lưu thay đổi</button>
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete CarType Modal -->
        <div class="modal fade" id="deleteCarTypeModal" tabindex="-1" aria-labelledby="deleteCarTypeModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteCarTypeModalLabel">Xóa loại xe</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Bạn có chắc muốn xóa loại xe <strong id="deleteCarTypeName"></strong>?</p>
                        <p class="text-muted">Lưu ý: Loại xe đang được sử dụng bởi các xe sẽ không thể xóa.</p>
                        <input type="hidden" id="deleteTypeID">
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-danger" id="confirmDeleteCarType">Xóa</button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Toast Container -->
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 1100;">
            <div id="successToast" class="toast align-items-center text-white bg-success border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body" id="successToastBody">
                        Thao tác thành công!
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
            <div id="errorToast" class="toast align-items-center text-white bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body" id="errorToastBody">
                        Có lỗi xảy ra!
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>
        <script>
                                                           function showSuccessToast(message) {
                                                               document.getElementById('successToastBody').textContent = message;
                                                               var toast = new bootstrap.Toast(document.getElementById('successToast'));
                                                               toast.show();
                                                           }

                                                           function showErrorToast(message) {
                                                               document.getElementById('errorToastBody').textContent = message;
                                                               var toast = new bootstrap.Toast(document.getElementById('errorToast'));
                                                               toast.show();
                                                           }

                                                           // Show lock modal automatically if there are affected bookings
                                                           document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${not empty affectedBookings}">
                                                               var lockModal = new bootstrap.Modal(document.getElementById('lockCarModal'));
                                                               lockModal.show();
            </c:if>
                                                           });

                                                           function showLockModal(button) {
                                                               var carId = button.getAttribute('data-carid');
                                                               var carName = button.getAttribute('data-carname');

                                                               document.getElementById('lockCarName').textContent = carName;
                                                               document.getElementById('lockCarID').value = carId;

                                                               // Fetch bookings to be cancelled via AJAX
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=getCarBookings&id=' + carId)
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           var tbody = document.getElementById('cancelledCarTripsBody');
                                                                           var cancelledCount = document.getElementById('cancelledCarCount');
                                                                           var ongoingInfo = document.getElementById('ongoingCarTripInfo');
                                                                           var ongoingText = document.getElementById('ongoingCarTripText');

                                                                           if (data.ongoingTrip) {
                                                                               ongoingInfo.style.display = 'block';
                                                                               ongoingText.textContent = 'Đơn #' + data.ongoingTrip.bookingID + ' đang chạy sẽ KHÔNG bị hủy.';
                                                                           } else {
                                                                               ongoingInfo.style.display = 'none';
                                                                           }

                                                                           if (data.cancelledTrips && data.cancelledTrips.length > 0) {
                                                                               var html = '';
                                                                               data.cancelledTrips.forEach(function (trip) {
                                                                                   html += '<tr>';
                                                                                   html += '<td>' + trip.contractCode + '</td>';
                                                                                   html += '<td>' + (trip.customerName || '') + '</td>';
                                                                                   html += '<td>' + (trip.startDateTime || '') + '</td>';
                                                                                   html += '<td>' + (trip.endDateTime || '') + '</td>';
                                                                                   html += '<td><span class="badge bg-warning">' + (trip.statusName || 'Booked') + '</span></td>';
                                                                                   html += '</tr>';
                                                                               });
                                                                               tbody.innerHTML = html;
                                                                               cancelledCount.textContent = data.cancelledTrips.length;
                                                                           } else {
                                                                               tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không có chuyến nào cần hủy</td></tr>';
                                                                               cancelledCount.textContent = '0';
                                                                           }
                                                                       })
                                                                       .catch(function (error) {
                                                                           console.error('Error:', error);
                                                                           document.getElementById('cancelledCarTripsBody').innerHTML = '<tr><td colspan="5" class="text-center text-danger">Lỗi khi tải dữ liệu</td></tr>';
                                                                       });
                                                           }

                                                           // Brand modals
                                                           var editBrandModal = document.getElementById('editBrandModal');
                                                           editBrandModal.addEventListener('show.bs.modal', function (event) {
                                                               var button = event.relatedTarget;
                                                               document.getElementById('editBrandID').value = button.getAttribute('data-brand-id');
                                                               document.getElementById('editBrandName').value = button.getAttribute('data-brand-name');
                                                           });

                                                           var deleteBrandModal = document.getElementById('deleteBrandModal');
                                                           deleteBrandModal.addEventListener('show.bs.modal', function (event) {
                                                               var button = event.relatedTarget;
                                                               document.getElementById('deleteBrandID').value = button.getAttribute('data-brand-id');
                                                               document.getElementById('deleteBrandName').textContent = button.getAttribute('data-brand-name');
                                                           });

                                                           // CarType modals
                                                           var editCarTypeModal = document.getElementById('editCarTypeModal');
                                                           editCarTypeModal.addEventListener('show.bs.modal', function (event) {
                                                               var button = event.relatedTarget;
                                                               document.getElementById('editTypeID').value = button.getAttribute('data-type-id');
                                                               document.getElementById('editTypeName').value = button.getAttribute('data-type-name');
                                                               document.getElementById('editSeatCount').value = button.getAttribute('data-seat-count');
                                                               document.getElementById('editDescription').value = button.getAttribute('data-description') || '';
                                                           });

                                                           var deleteCarTypeModal = document.getElementById('deleteCarTypeModal');
                                                           deleteCarTypeModal.addEventListener('show.bs.modal', function (event) {
                                                               var button = event.relatedTarget;
                                                               document.getElementById('deleteTypeID').value = button.getAttribute('data-type-id');
                                                               document.getElementById('deleteCarTypeName').textContent = button.getAttribute('data-type-name');
                                                           });

                                                           // Form submissions
                                                           document.getElementById('addBrandForm').addEventListener('submit', function (e) {
                                                               e.preventDefault();
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=addBrand&' + new URLSearchParams({
                                                                   brandName: document.getElementById('newBrandName').value
                                                               }), {
                                                                   method: 'GET'
                                                               })
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('addBrandModal')).hide();
                                                                               showSuccessToast(data.message || 'Thêm hãng xe thành công');
                                                                               setTimeout(() => location.reload(), 1500);
                                                                           } else {
                                                                               showErrorToast(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });

                                                           document.getElementById('editBrandForm').addEventListener('submit', function (e) {
                                                               e.preventDefault();
                                                               var formData = new FormData(this);
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=editBrand&' + new URLSearchParams({
                                                                   brandID: document.getElementById('editBrandID').value,
                                                                   brandName: document.getElementById('editBrandName').value
                                                               }), {
                                                                   method: 'GET'
                                                               })
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('editBrandModal')).hide();
                                                                               showSuccessToast(data.message || 'Cập nhật hãng xe thành công');
                                                                               setTimeout(() => location.reload(), 1500);
                                                                           } else {
                                                                               showErrorToast(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });

                                                           document.getElementById('confirmDeleteBrand').addEventListener('click', function () {
                                                               var brandID = document.getElementById('deleteBrandID').value;
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=deleteBrand&brandID=' + brandID)
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('deleteBrandModal')).hide();
                                                                               location.reload();
                                                                           } else {
                                                                               alert(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });

                                                           document.getElementById('addCarTypeForm').addEventListener('submit', function (e) {
                                                               e.preventDefault();
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=addCarType&' + new URLSearchParams({
                                                                   typeName: document.getElementById('newTypeName').value,
                                                                   seatCount: document.getElementById('newSeatCount').value,
                                                                   description: document.getElementById('newDescription').value
                                                               }), {
                                                                   method: 'GET'
                                                               })
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('addCarTypeModal')).hide();
                                                                               showSuccessToast(data.message || 'Thêm loại xe thành công');
                                                                               setTimeout(() => location.reload(), 1500);
                                                                           } else {
                                                                               showErrorToast(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });

                                                           document.getElementById('editCarTypeForm').addEventListener('submit', function (e) {
                                                               e.preventDefault();
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=editCarType&' + new URLSearchParams({
                                                                   typeID: document.getElementById('editTypeID').value,
                                                                   typeName: document.getElementById('editTypeName').value,
                                                                   seatCount: document.getElementById('editSeatCount').value,
                                                                   description: document.getElementById('editDescription').value
                                                               }), {
                                                                   method: 'GET'
                                                               })
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('editCarTypeModal')).hide();
                                                                               showSuccessToast(data.message || 'Cập nhật loại xe thành công');
                                                                               setTimeout(() => location.reload(), 1500);
                                                                           } else {
                                                                               showErrorToast(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });

                                                           document.getElementById('confirmDeleteCarType').addEventListener('click', function () {
                                                               var typeID = document.getElementById('deleteTypeID').value;
                                                               fetch('${pageContext.request.contextPath}/admin/cars?action=deleteCarType&typeID=' + typeID)
                                                                       .then(response => response.json())
                                                                       .then(data => {
                                                                           if (data.success) {
                                                                               bootstrap.Modal.getInstance(document.getElementById('deleteCarTypeModal')).hide();
                                                                               location.reload();
                                                                           } else {
                                                                               alert(data.error || 'Có lỗi xảy ra');
                                                                           }
                                                                       });
                                                           });
        </script>
    </body>
</html>
