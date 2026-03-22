<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty car ? 'Thêm xe mới' : 'Chỉnh sửa xe'} - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { background: #f4f6f9; }
        .admin-wrapper { min-height: 100vh; }
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
        .content-area { flex: 1; }
        .topbar {
            background: #fff;
            border-bottom: 1px solid #e5e7eb;
            padding: 14px 24px;
        }
        .dashboard-body { padding: 24px; }
        .form-section {
            background: #fff;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .form-section-title {
            font-weight: 600;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e5e7eb;
        }
        .image-preview {
            max-width: 200px;
            max-height: 150px;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 5px;
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
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users?type=customer"><i class="bi bi-people me-2"></i>Quản lý người dùng</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/cars"><i class="bi bi-car-front me-2"></i>Quản lý xe</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookings"><i class="bi bi-calendar2-check me-2"></i>Quản lý booking</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/locations"><i class="bi bi-geo-alt me-2"></i>Địa điểm nhận/trả</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reviews"><i class="bi bi-star me-2"></i>Đánh giá khách hàng</a>
<a class="nav-link" href="${pageContext.request.contextPath}/admin/reports"><i class="bi bi-cash-coin me-2"></i>Doanh thu</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/driver-salaries"><i class="bi bi-person-badge me-2"></i>Lương tài xế</a>         </nav>

        <div class="mt-auto p-3">
            <a class="btn btn-outline-light w-100" href="${pageContext.request.contextPath}/logout">
                <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
            </a>
        </div>
    </aside>

    <main class="content-area">
        <div class="topbar d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0">${empty car ? 'Thêm xe mới' : 'Chỉnh sửa thông tin xe'}</h5>
                <small class="text-muted">Quản lý thông tin xe trong hệ thống</small>
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/cars">
                <i class="bi bi-arrow-left me-1"></i>Quay lại
            </a>
        </div>

        <div class="dashboard-body">
            <c:if test="${param.success == 'add'}">
                <div class="alert alert-success">Thêm xe mới thành công.</div>
            </c:if>
            <c:if test="${param.success == 'update'}">
                <div class="alert alert-success">Cập nhật xe thành công.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/cars?action=${empty car ? 'add' : 'update'}" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="${empty car ? 'add' : 'update'}">
                <c:if test="${not empty car}">
                    <input type="hidden" name="carID" value="${car.carID}">
                </c:if>

                <!-- Thông tin cơ bản -->
                <div class="form-section">
                    <div class="form-section-title"><i class="bi bi-car-front me-2"></i>Thông tin xe</div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Tên xe <span class="text-danger">*</span></label>
                            <input type="text" name="carName" class="form-control" value="${car.carName}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Biển số <span class="text-danger">*</span></label>
                            <input type="text" name="plateNumber" class="form-control" value="${car.plateNumber}" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Hãng xe <span class="text-danger">*</span></label>
                            <select name="brandID" class="form-select" required>
                                <option value="">Chọn hãng xe</option>
                                <c:forEach var="brand" items="${brands}">
                                    <option value="${brand.brandID}" ${car.brandID == brand.brandID ? 'selected' : ''}>${brand.brandName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Loại xe <span class="text-danger">*</span></label>
                            <select name="carTypeID" class="form-select" required>
                                <option value="">Chọn loại xe</option>
                                <c:forEach var="type" items="${carTypes}">
                                    <option value="${type.carTypeID}" ${car.typeID == type.carTypeID ? 'selected' : ''}>${type.typeName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Màu sắc <span class="text-danger">*</span></label>
                            <input type="text" name="color" class="form-control" value="${car.color}" placeholder="VD: Đen, Trắng, Đỏ" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Năm sản xuất <span class="text-danger">*</span></label>
                            <input type="number" name="productionYear" class="form-control" value="${car.productionYear}" min="1900" max="2030" required>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Hộp số <span class="text-danger">*</span></label>
                            <select name="transmission" class="form-select" required>
                                <option value="">Chọn hộp số</option>
                                <option value="Số tự động" ${car.transmission == 'Số tự động' ? 'selected' : ''}>Số tự động</option>
                                <option value="Số sàn" ${car.transmission == 'Số sàn' ? 'selected' : ''}>Số sàn</option>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Nhiên liệu <span class="text-danger">*</span></label>
                            <select name="fuelType" class="form-select" required>
                                <option value="">Chọn nhiên liệu</option>
                                <option value="Xăng" ${car.fuelType == 'Xăng' ? 'selected' : ''}>Xăng</option>
                                <option value="Dầu" ${car.fuelType == 'Dầu' ? 'selected' : ''}>Dầu</option>
                                <option value="Điện" ${car.fuelType == 'Điện' ? 'selected' : ''}>Điện</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giá thuê/ngày (VND) <span class="text-danger">*</span></label>
                            <input type="number" name="pricePerDay" class="form-control" value="${car.defaultPricePerDay}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Địa điểm <span class="text-danger">*</span></label>
                            <select name="locationID" class="form-select" required>
                                <option value="">Chọn địa điểm</option>
                                <c:forEach var="location" items="${locations}">
                                    <option value="${location.locationID}" ${car.locationID == location.locationID ? 'selected' : ''}>${location.locationName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Hình ảnh & Mô tả -->
                <div class="form-section">
                    <div class="form-section-title"><i class="bi bi-image me-2"></i>Hình ảnh & Mô tả</div>
                    <div class="row">
                        <div class="col-md-12 mb-3">
                            <label class="form-label">URL Hình ảnh chính</label>
                            <input type="url" name="imageURL" class="form-control" value="${car.imageURL}" placeholder="https://example.com/image.jpg">
                            <c:if test="${not empty car.imageURL}">
                                <div class="mt-2">
                                    <img src="${car.imageURL}" alt="Car preview" class="image-preview" onerror="this.style.display='none'">
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <!-- Multiple Images Upload -->
                    <div class="row mt-3">
                        <div class="col-md-12">
                            <label class="form-label"><i class="bi bi-images me-2"></i>Tập ảnh xe</label>
                            <p class="text-muted small">Thêm nhiều ảnh cho xe (ảnh ngoại thất, nội thất, ...)</p>
                        </div>
                    </div>
                    
                    <!-- Existing Images (for edit mode) -->
                    <c:if test="${not empty carImages}">
                        <div class="row mt-2 mb-3">
                            <div class="col-md-12">
                                <label class="form-label">Ảnh hiện có:</label>
                                <div class="row" id="existingImages">
                                    <c:forEach var="img" items="${carImages}">
                                        <div class="col-md-3 col-sm-4 col-6 mb-2" id="img_${img.imageID}">
                                            <div class="position-relative">
                                                <img src="${img.imageUrl}" alt="Car image" class="img-thumbnail" style="height: 120px; object-fit: cover;" 
                                                     onerror="this.src='https://via.placeholder.com/150?text=No+Image'">
                                                <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0" 
                                                        onclick="deleteImage('${img.imageID}')" title="Xóa ảnh">
                                                    <i class="bi bi-x-lg"></i>
                                                </button>
                                                <input type="hidden" name="existingImageIds" value="${img.imageID}">
                                            </div>
                                            <select name="imageType_${img.imageID}" class="form-select form-select-sm mt-1" onchange="updateImageType('${img.imageID}', this.value)">
                                                <option value="Gallery" ${img.imageType == 'Gallery' ? 'selected' : ''}>Ngoại thất</option>
                                                <option value="Interior" ${img.imageType == 'Interior' ? 'selected' : ''}>Nội thất</option>
                                                <option value="Detail" ${img.imageType == 'Detail' ? 'selected' : ''}>Chi tiết</option>
                                            </select>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    
                    
                    <!-- URL-based Images -->
                    <div class="row mt-3">
                        <div class="col-md-12">
                            <label class="form-label">Thêm ảnh từ URL:</label>
                            <div id="urlImageContainer">
                                <div class="input-group mb-2 url-image-row">
                                    <input type="url" name="imageUrls[]" class="form-control" placeholder="https://example.com/image.jpg">
                                    <select name="imageType_0" class="form-select" style="max-width: 150px;">
                                        <option value="Gallery">Ngoại thất</option>
                                        <option value="Interior">Nội thất</option>
                                        <option value="Detail">Chi tiết</option>
                                    </select>
                                    <input type="number" name="sortOrder_0" class="form-control" placeholder="Thứ tự" style="max-width: 100px;" min="0">
                                    <button type="button" class="btn btn-success" onclick="addUrlImageRow()">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                </div>
                            </div>
                            <button type="button" class="btn btn-outline-primary btn-sm mt-2" onclick="addUrlImageRow()">
                                <i class="bi bi-link-45deg me-1"></i>Thêm từ URL
                            </button>
                        </div>
                    </div>
                    
                    <div class="row mt-3">
                        <div class="col-md-12 mb-3">
                            <label class="form-label">Mô tả</label>
                            <textarea name="description" class="form-control" rows="4">${car.description}</textarea>
                        </div>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-dark">
                        <i class="bi bi-check-lg me-1"></i>${empty car ? 'Thêm xe' : 'Cập nhật'}
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/cars" class="btn btn-secondary">
                        <i class="bi bi-x-lg me-1"></i>Hủy
                    </a>
                </div>
            </form>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
let imageUploadCount = 1;
let urlImageCount = 1;
let deletedImages = [];

function addImageUploadRow() {
    const container = document.getElementById('imageUploadContainer');
    const row = document.createElement('div');
    row.className = 'input-group mb-2 image-upload-row';
    row.innerHTML = `
        <input type="file" name="carImage${imageUploadCount}" class="form-control" accept="image/*">
        <select name="imageType_carImage${imageUploadCount}" class="form-select" style="max-width: 150px;">
            <option value="Gallery">Ngoại thất</option>
            <option value="Interior">Nội thất</option>
            <option value="Detail">Chi tiết</option>
        </select>
        <input type="number" name="sortOrder_carImage${imageUploadCount}" class="form-control" placeholder="Thứ tự" style="max-width: 100px;" min="0">
        <button type="button" class="btn btn-danger" onclick="this.parentElement.remove()">
            <i class="bi bi-x-lg"></i>
        </button>
    `;
    container.appendChild(row);
    imageUploadCount++;
}

function addUrlImageRow() {
    const container = document.getElementById('urlImageContainer');
    const rows = container.querySelectorAll('.url-image-row');
    const count = rows.length;
    
    const row = document.createElement('div');
    row.className = 'input-group mb-2 url-image-row';
    row.innerHTML = `
        <input type="url" name="imageUrls[]" class="form-control" placeholder="https://example.com/image.jpg">
        <select name="imageType_${count}" class="form-select" style="max-width: 150px;">
            <option value="Gallery">Ngoại thất</option>
            <option value="Interior">Nội thất</option>
            <option value="Detail">Chi tiết</option>
        </select>
        <input type="number" name="sortOrder_${count}" class="form-control" placeholder="Thứ tự" style="max-width: 100px;" min="0">
        <button type="button" class="btn btn-danger" onclick="this.parentElement.remove()">
            <i class="bi bi-x-lg"></i>
        </button>
    `;
    container.appendChild(row);
}

function deleteImage(imageId) {
    if (confirm('Bạn có chắc muốn xóa ảnh này?')) {
        deletedImages.push(imageId);
        document.getElementById('img_' + imageId).style.display = 'none';
        
        // Create hidden input to track deleted images
        let hiddenInput = document.querySelector('input[name="deleteImages"]');
        if (!hiddenInput) {
            hiddenInput = document.createElement('input');
            hiddenInput.type = 'hidden';
            hiddenInput.name = 'deleteImages';
            document.querySelector('form').appendChild(hiddenInput);
        }
        hiddenInput.value = deletedImages.join(',');
    }
}

function updateImageType(imageId, imageType) {
    console.log('Update image ' + imageId + ' type to: ' + imageType);
    // Could add AJAX call here to update in real-time
}

document.querySelector('form').addEventListener('submit', function(e) {
    const formData = new FormData(this);
    let debug = 'Form submission:\n';
    for (let [key, value] of formData.entries()) {
        if (value instanceof File && value.name) {
            debug += key + ': [File: ' + value.name + ']\n';
        } else {
            debug += key + ': ' + value + '\n';
        }
    }
    console.log(debug);
});
</script>
</body>
</html>
