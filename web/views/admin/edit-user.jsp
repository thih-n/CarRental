<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty user ? 'Tạo tài khoản' : 'Chỉnh sửa thông tin'} - Admin</title>
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/admin/refunds"><i class="bi bi-arrow-return-left me-2"></i>Hoàn tiền</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/users"><i class="bi bi-people me-2"></i>Quản lý người dùng</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/cars"><i class="bi bi-car-front me-2"></i>Quản lý xe</a>
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
                <h5 class="mb-0">${empty user ? 'Tạo tài khoản mới' : 'Chỉnh sửa thông tin'}</h5>
                <small class="text-muted">
                    <c:choose>
                        <c:when test="${param.type == 'staff'}">Nhân viên</c:when>
                        <c:when test="${param.type == 'driver'}">Tài xế</c:when>
                        <c:when test="${param.type == 'admin'}">Admin</c:when>
                        <c:otherwise>Khách hàng</c:otherwise>
                    </c:choose>
                </small>
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/users?type=${param.type}">
                <i class="bi bi-arrow-left me-1"></i>Quay lại
            </a>
        </div>

        <div class="dashboard-body">
            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Cập nhật thông tin thành công.</div>
            </c:if>
            <c:if test="${param.created == 'true'}">
                <div class="alert alert-success">Tạo tài khoản thành công.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/users" method="post" enctype="multipart/form-data">
                <input type="hidden" name="action" value="${empty user ? 'create' : 'update'}">
                <input type="hidden" name="type" value="${param.type}">
                <c:if test="${not empty user}">
                    <input type="hidden" name="userID" value="${user.userID}">
                </c:if>

                <!-- Thông tin cơ bản -->
                <div class="form-section">
                    <div class="form-section-title"><i class="bi bi-person me-2"></i>Thông tin cơ bản</div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                            <input type="text" name="fullName" class="form-control ${errors.fullName != null ? 'is-invalid' : ''}" 
                                   value="${not empty user ? user.fullName : (not empty fullName ? fullName : '')}" required>
                            <c:if test="${errors.fullName != null}">
                                <div class="invalid-feedback">${errors.fullName}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email" name="email" class="form-control ${errors.email != null ? 'is-invalid' : ''}" 
                                   value="${not empty user ? user.email : (not empty email ? email : '')}" ${not empty user ? 'readonly' : ''} required>
                            <c:if test="${errors.email != null}">
                                <div class="invalid-feedback">${errors.email}</div>
                            </c:if>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Số điện thoại</label>
                            <input type="text" name="phoneNumber" class="form-control ${errors.phoneNumber != null ? 'is-invalid' : ''}" 
                                   value="${not empty user ? user.phoneNumber : (not empty phoneNumber ? phoneNumber : '')}">
                            <c:if test="${errors.phoneNumber != null}">
                                <div class="invalid-feedback">${errors.phoneNumber}</div>
                            </c:if>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Địa chỉ</label>
                            <input type="text" name="address" class="form-control" 
                                   value="${not empty user ? user.address : (not empty address ? address : '')}">
                        </div>
                    </div>
                    
                    <!-- Password for create or admin/staff update -->
                    <c:if test="${empty user || param.type == 'staff' || param.type == 'admin'}">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <c:choose>
                                        <c:when test="${empty user}">Mật khẩu <span class="text-danger">*</span></c:when>
                                        <c:otherwise>Mật khẩu mới (để trống nếu không đổi)</c:otherwise>
                                    </c:choose>
                                </label>
                                <input type="password" name="password" class="form-control ${errors.password != null ? 'is-invalid' : ''}" ${empty user ? 'required' : ''}>
                                <c:if test="${errors.password != null}">
                                    <div class="invalid-feedback">${errors.password}</div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                </div>

                <!-- Thông tin giấy tờ (Customer & Driver) -->
                <c:if test="${param.type == 'customer' || param.type == 'driver'}">
                    <div class="form-section">
                        <div class="form-section-title"><i class="bi bi-card-text me-2"></i>Thông tin giấy tờ</div>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Số CMND/CCCD</label>
                                <c:choose>
                                    <c:when test="${param.type == 'customer'}">
                                        <input type="text" class="form-control" value="${user.identityCardNumber}" readonly>
                                        <input type="hidden" name="identityCardNumber" value="${user.identityCardNumber}">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" name="identityCardNumber" class="form-control" value="${user.identityCardNumber}">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Driver License Fields -->
                        <c:if test="${param.type == 'driver'}">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Số GPLX</label>
                                    <input type="text" name="licenseNumber" class="form-control" value="${driverProfile.licenseNumber}">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ngày hết hạn GPLX</label>
                                    <input type="date" name="licenseExpiry" class="form-control" value="${driverProfile.licenseExpiry}">
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Số năm kinh nghiệm</label>
                                    <input type="number" name="experienceYears" class="form-control" value="${driverProfile.experienceYears}">
                                </div>
                            </div>
                        </c:if>

                        <!-- License Images -->
                        <div class="row mt-3">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <c:choose>
                                        <c:when test="${param.type == 'customer'}">Ảnh CMND mặt trước (chỉ xem)</c:when>
                                        <c:otherwise>Ảnh CMND/GPLX mặt trước</c:otherwise>
                                    </c:choose>
                                </label>
                                <c:if test="${not empty user.licenseImageFront}">
                                    <div class="mb-2">
                                        <c:choose>
                                            <c:when test="${user.licenseImageFront.startsWith('http') || user.licenseImageFront.startsWith('//')}">
                                                <img src="${user.licenseImageFront}" class="image-preview" alt="License Front" onerror="this.style.display='none'">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/${user.licenseImageFront}" class="image-preview" alt="License Front" onerror="this.style.display='none'">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                                <c:choose>
                                    <c:when test="${param.type == 'customer'}">
                                        <input type="text" class="form-control" value="${user.licenseImageFront}" readonly>
                                        <input type="hidden" name="existingLicenseFront" value="${user.licenseImageFront}">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="url" name="licenseImageFront" class="form-control" value="${user.licenseImageFront}" placeholder="https://example.com/cmnd_front.jpg">
                                        <input type="hidden" name="existingLicenseFront" value="${user.licenseImageFront}">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <c:choose>
                                        <c:when test="${param.type == 'customer'}">Ảnh CMND mặt sau (chỉ xem)</c:when>
                                        <c:otherwise>Ảnh CMND/GPLX mặt sau</c:otherwise>
                                    </c:choose>
                                </label>
                                <c:if test="${not empty user.licenseImageBack}">
                                    <div class="mb-2">
                                        <c:choose>
                                            <c:when test="${user.licenseImageBack.startsWith('http') || user.licenseImageBack.startsWith('//')}">
                                                <img src="${user.licenseImageBack}" class="image-preview" alt="License Back" onerror="this.style.display='none'">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/${user.licenseImageBack}" class="image-preview" alt="License Back" onerror="this.style.display='none'">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
                                <c:choose>
                                    <c:when test="${param.type == 'customer'}">
                                        <input type="text" class="form-control" value="${user.licenseImageBack}" readonly>
                                        <input type="hidden" name="existingLicenseBack" value="${user.licenseImageBack}">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="url" name="licenseImageBack" class="form-control" value="${user.licenseImageBack}" placeholder="https://example.com/cmnd_back.jpg">
                                        <input type="hidden" name="existingLicenseBack" value="${user.licenseImageBack}">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-lg me-1"></i>
                        <c:choose>
                            <c:when test="${empty user}">Tạo tài khoản</c:when>
                            <c:otherwise>Lưu thay đổi</c:otherwise>
                        </c:choose>
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/users?type=${param.type}" class="btn btn-secondary">Hủy</a>
                </div>
            </form>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
