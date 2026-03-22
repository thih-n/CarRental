<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">CarRental</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/cars">Cars</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/booking/search">Booking</a></li>

                <c:if test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 5}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/client/orders">My Trips</a></li>
                </c:if>
                <c:if test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 3}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/staff/dashboard">Staff</a></li>
                </c:if>
                <c:if test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 4}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/driver/dashboard">Driver</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">My Profile</a></li>
                </c:if>
                <c:if test="${sessionScope.USER_SESSION != null && sessionScope.USER_SESSION.roleID == 1}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></li>
                </c:if>

                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/profile">Profile</a></li>
            </ul>

            <ul class="navbar-nav align-items-center">
                <c:choose>
                    <c:when test="${sessionScope.USER_SESSION != null}">
                        <li class="nav-item me-2">
                            <span class="navbar-text text-light">Hi, ${sessionScope.USER_SESSION.fullName}</span>
                        </li>
                        <li class="nav-item"><a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item me-2"><a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login">Login</a></li>
                        <li class="nav-item"><a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}/register">Register</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
