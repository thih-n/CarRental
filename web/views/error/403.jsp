<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>403 - Không có quyền</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="card shadow-sm mx-auto" style="max-width: 560px;">
        <div class="card-body text-center">
            <h3 class="text-danger">403 - Không có quyền truy cập</h3>
            <p class="text-muted">Bạn không có quyền truy cập chức năng này.</p>
            <c:choose>
                <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 5}">
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/home">Quay về trang Home</a>
                </c:when>
                <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 1}">
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/admin/dashboard">Quay về Admin Dashboard</a>
                </c:when>
                <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 3}">
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/staff/dashboard">Quay về Staff Dashboard</a>
                </c:when>
                <c:when test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 4}">
                    <a class="btn btn-dark" href="${pageContext.request.contextPath}/driver/dashboard">Quay về Driver Dashboard</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/login">Đăng nhập lại</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
