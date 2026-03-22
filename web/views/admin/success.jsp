<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Success</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/views/common/header.jsp"/>
<div class="container py-5">
    <div class="card shadow-sm mx-auto" style="max-width: 520px;">
        <div class="card-body text-center">
            <h3 class="text-success">Booking Success</h3>
            <p class="text-muted">Hệ thống đã tạo hợp đồng thành công.</p>
            <p class="fw-bold">Contract ID: ${param.contractId}</p>
            <div class="d-grid gap-2 mt-3">
                <a class="btn btn-dark" href="${pageContext.request.contextPath}/client/orders">View my orders</a>
                <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/home">Back to home</a>
            </div>
        </div>
    </div>

    <div class="modal fade" id="bookingSuccessModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Đặt xe thành công</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Vui lòng đến sớm 15-30 phút để bàn giao xe.</p>
                    <p>Chuẩn bị giấy tờ tuỳ thân và giấy phép lái xe để xác thực.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-dark" data-bs-dismiss="modal">Đã hiểu</button>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/views/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const modal = new bootstrap.Modal(document.getElementById('bookingSuccessModal'));
    modal.show();
</script>
</body>
</html>
