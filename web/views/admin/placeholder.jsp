<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta charset="UTF-8"><title>Admin Module</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"></head>
<body class="bg-light"><jsp:include page="/views/common/header.jsp"/>
<div class="container py-4">
    <h3>Module đang phát triển</h3>
    <p>Trang này là placeholder cho chức năng quản trị.</p>
    <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/admin/dashboard">Back to dashboard</a>
</div>
<jsp:include page="/views/common/footer.jsp"/>
</body></html>
