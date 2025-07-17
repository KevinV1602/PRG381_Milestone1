<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <div class="wellness-icon">
                <i class="fas fa-heartbeat"></i>
            </div>
            <h2>Welcome Back</h2>
            <p>Sign in to your wellness journey</p>
        </div>

        <form action="login" method="post" class="login-form" id="loginForm">
            <div class="form-section">
                <h3 class="form-section-title">Account Information</h3>

                <div class="form-group">
                    <label for="username" class="form-label">Username</label>
                    <i class="fas fa-user input-icon"></i>
                    <input type="text" id="username" name="username" class="form-control ${not empty error ? 'is-invalid' : ''}" 
                           placeholder="Enter your username" required />
                    <div class="invalid-feedback">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </div>

                <div class="form-group">
                    <label for="password" class="form-label">Password</label>
                    <i class="fas fa-lock input-icon"></i>
                    <input type="password" id="password" name="password" class="form-control ${not empty error ? 'is-invalid' : ''}" 
                           placeholder="Enter your password" required />
                    <div class="password-toggle" onclick="togglePassword()">
                        <i class="fas fa-eye" id="password-toggle-icon"></i>
                    </div>
                    <div class="invalid-feedback">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </div>
            </div>

            <button type="submit" class="login-btn">
                <i class="fas fa-sign-in-alt"></i> Sign In to Wellness System
            </button>
        </form>

        <%-- System error messages --%>
        <% if (request.getAttribute("systemError") != null) { %>
            <div class="alert alert-danger mt-3">
                <i class="fas fa-exclamation-triangle"></i>
                System error: ${systemError}
            </div>
        <% } %>
    </div>

    <script>
        // Password toggle functionality
        function togglePassword() {
            const passwordInput = document.getElementById('password');
            const icon = document.getElementById('password-toggle-icon');
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }

        // Client-side validation
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();
            let isValid = true;
            
            // Clear previous errors
            document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
            
            // Username validation
            if (!username) {
                showError('username', 'Username is required');
                isValid = false;
            } else if (username.length < 4) {
                showError('username', 'Username must be at least 4 characters');
                isValid = false;
            }
            
            // Password validation
            if (!password) {
                showError('password', 'Password is required');
                isValid = false;
            } else if (password.length < 6) {
                showError('password', 'Password must be at least 6 characters');
                isValid = false;
            }
            
            if (!isValid) {
                e.preventDefault();
            }
        });
        
        function showError(fieldId, message) {
            const input = document.getElementById(fieldId);
            const errorDiv = input.nextElementSibling;
            
            input.classList.add('is-invalid');
            errorDiv.textContent = `\u00A0${message}`; // \u00A0 is non-breaking space
            errorDiv.style.display = 'block';
        }
    </script>
</body>
</html>
