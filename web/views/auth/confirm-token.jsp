<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác nhận mã - Car Rental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.2);
        }
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 15px 15px 0 0 !important;
        }
        .otp-input {
            letter-spacing: 10px;
            font-size: 24px;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card">
                <div class="card-header bg-transparent text-white text-center py-4">
                    <h4 class="mb-0">Xác nhận mã</h4>
                    <p class="mb-0 mt-2 small">Nhập mã xác nhận đã gửi đến email của bạn</p>
                </div>
                <div class="card-body p-4">
                    
                    <div class="text-center mb-4">
                        <p class="text-muted">Mã xác nhận đã được gửi đến:</p>
                        <strong><%= request.getAttribute("resetEmail") != null ? request.getAttribute("resetEmail") : "" %></strong>
                    </div>
                    
                    <% if(request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger" role="alert">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>
                    
                    <form action="confirm-token" method="POST">
                        <div class="mb-4">
                            <label for="token" class="form-label">Mã xác nhận</label>
                            <input type="text" class="form-control otp-input" id="token" name="token" 
                                   maxlength="6" pattern="[0-9]{6}" placeholder="------" required autocomplete="off">
                        </div>
                        
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">Xác nhận</button>
                        </div>
                    </form>
                    
                    <div class="text-center mt-3">
                        <p class="small text-muted">Mã có hiệu lực trong 15 phút</p>
                    </div>
                    
                </div>
                <div class="card-footer text-center py-3">
                    <a href="<%= request.getContextPath() %>/forgot-password" class="text-muted">
                        &larr; Gửi lại mã
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('token').addEventListener('input', function(e) {
        this.value = this.value.replace(/[^0-9]/g, '').slice(0, 6);
    });
</script>
</body>
</html>
