<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/register.css">
    <title>BC Student Wellness - Registration</title>
</head>
<body>
    <div class="registration-container">
        <div class="header">
            <div class="wellness-icon">
                <i class="fas fa-heartbeat"></i>
            </div>
            <h1>Join BC Wellness</h1>
            <p>Create your account to get started</p>
            
            <%-- Display server-side errors --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            <% } %>
            <% if (request.getAttribute("message") != null) { %>
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${message}
                </div>
            <% } %>
        </div>

        <form id="registrationForm" action="${pageContext.request.contextPath}/register" method="POST" novalidate>
            <div class="form-group">
                <label for="firstName">First Name</label>
                <i class="fas fa-user input-icon"></i>
                <input type="text" id="firstName" name="firstName" 
                       value="${not empty firstName ? firstName : param.firstName}" 
                       class="${not empty firstNameError ? 'error' : ''}" required/>
                <div class="validation-message" id="firstNameError">
                    ${firstNameError}
                </div>
            </div>

            <div class="form-group">
                <label for="lastName">Last Name</label>
                <i class="fas fa-user input-icon"></i>
                <input type="text" id="lastName" name="lastName" 
                       value="${not empty lastName ? lastName : param.lastName}"
                       class="${not empty lastNameError ? 'error' : ''}" required/>
                <div class="validation-message" id="lastNameError">
                    ${lastNameError}
                </div>
            </div>

            <div class="form-group">
                <label for="username">Username</label>
                <i class="fas fa-at input-icon"></i>
                <input type="text" id="username" name="username" 
                       value="${not empty username ? username : param.username}"
                       class="${not empty usernameError ? 'error' : ''}" required/>
                <div class="validation-message" id="usernameError">
                    ${usernameError}
                </div>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <i class="fas fa-envelope input-icon"></i>
                <input type="email" id="email" name="email" 
                       value="${not empty email ? email : param.email}"
                       class="${not empty emailError ? 'error' : ''}" required/>
                <div class="validation-message" id="emailError">
                    ${emailError}
                </div>
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <i class="fas fa-phone input-icon"></i>
                <input type="tel" id="phone" name="phone" 
                       value="${not empty phone ? phone : param.phone}"
                       class="${not empty phoneError ? 'error' : ''}" 
                       placeholder="(123) 456-7890" required/>
                <div class="validation-message" id="phoneError">
                    ${phoneError}
                </div>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <i class="fas fa-lock input-icon"></i>
                <input type="password" id="password" name="password" 
                       class="${not empty passwordError ? 'error' : ''}" required/>
                <i class="fas fa-eye password-toggle" id="passwordToggle"></i>
                <div class="validation-message" id="passwordError">
                    ${passwordError}
                </div>
                <div class="password-strength">
                    <div class="strength-meter">
                        <div class="strength-bar"></div>
                    </div>
                    <div class="strength-text"></div>
                </div>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <i class="fas fa-lock input-icon"></i>
                <input type="password" id="confirmPassword" name="confirmPassword" 
                       class="${not empty confirmPasswordError ? 'error' : ''}" required/>
                <i class="fas fa-eye password-toggle" id="confirmPasswordToggle"></i>
                <div class="validation-message" id="confirmPasswordError">
                    ${confirmPasswordError}
                </div>
            </div>

            <button type="submit" class="submit-btn" id="submitBtn">
                <i class="fas fa-user-plus"></i> Create Account
            </button>
        </form>

        <div class="login-link">
            Already have an account? <a href="login.jsp">Sign In</a>
        </div>
    </div>

    <script>
        // Password toggle functionality
        document.getElementById('passwordToggle').addEventListener('click', function() {
            togglePassword('password', 'passwordToggle');
        });
        
        document.getElementById('confirmPasswordToggle').addEventListener('click', function() {
            togglePassword('confirmPassword', 'confirmPasswordToggle');
        });
        
        function togglePassword(fieldId, toggleId) {
            const input = document.getElementById(fieldId);
            const icon = document.getElementById(toggleId);
            
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }
        
        // Client-side validation
        document.getElementById('registrationForm').addEventListener('submit', function(e) {
            let isValid = true;
            
            // Clear previous errors
            document.querySelectorAll('.error').forEach(el => el.classList.remove('error'));
            document.querySelectorAll('.validation-message').forEach(el => el.textContent = '');
            
            // Validate First Name
            const firstName = document.getElementById('firstName').value.trim();
            if (!firstName) {
                showError('firstName', 'First name is required');
                isValid = false;
            } else if (firstName.length < 2) {
                showError('firstName', 'First name must be at least 2 characters');
                isValid = false;
            }
            
            // Validate Last Name
            const lastName = document.getElementById('lastName').value.trim();
            if (!lastName) {
                showError('lastName', 'Last name is required');
                isValid = false;
            } else if (lastName.length < 2) {
                showError('lastName', 'Last name must be at least 2 characters');
                isValid = false;
            }
            
            // Validate Username
            const username = document.getElementById('username').value.trim();
            if (!username) {
                showError('username', 'Username is required');
                isValid = false;
            } else if (username.length < 4) {
                showError('username', 'Username must be at least 4 characters');
                isValid = false;
            } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
                showError('username', 'Username can only contain letters, numbers and underscores');
                isValid = false;
            }
            
            // Validate Email
            const email = document.getElementById('email').value.trim();
            if (!email) {
                showError('email', 'Email is required');
                isValid = false;
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                showError('email', 'Please enter a valid email address');
                isValid = false;
            }
            
            // Validate Phone
            const phone = document.getElementById('phone').value.trim();
            const cleanedPhone = phone.replace(/\D/g, '');
            if (!phone) {
                showError('phone', 'Phone number is required');
                isValid = false;
            } else if (cleanedPhone.length < 10) {
                showError('phone', 'Please enter a valid 10-digit phone number');
                isValid = false;
            }
            
            // Validate Password
            const password = document.getElementById('password').value;
            if (!password) {
                showError('password', 'Password is required');
                isValid = false;
            } else if (password.length < 8) {
                showError('password', 'Password must be at least 8 characters');
                isValid = false;
            } else if (!/[A-Z]/.test(password)) {
                showError('password', 'Password must contain at least one uppercase letter');
                isValid = false;
            } else if (!/[0-9]/.test(password)) {
                showError('password', 'Password must contain at least one number');
                isValid = false;
            } else if (!/[!@#$%^&*]/.test(password)) {
                showError('password', 'Password must contain at least one special character');
                isValid = false;
            }
            
            // Validate Confirm Password
            const confirmPassword = document.getElementById('confirmPassword').value;
            if (!confirmPassword) {
                showError('confirmPassword', 'Please confirm your password');
                isValid = false;
            } else if (password !== confirmPassword) {
                showError('confirmPassword', 'Passwords do not match');
                isValid = false;
            }
            
            if (!isValid) {
                e.preventDefault();
            }
        });
        
        function showError(fieldId, message) {
            const input = document.getElementById(fieldId);
            const errorElement = document.getElementById(fieldId + 'Error');
            
            input.classList.add('error');
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
        
        // Password strength indicator
        document.getElementById('password').addEventListener('input', function() {
            checkPasswordStrength(this.value);
        });
        
        function checkPasswordStrength(password) {
            const strengthBar = document.querySelector('.strength-bar');
            const strengthText = document.querySelector('.strength-text');
            let strength = 0;
            
            // Length check
            if (password.length >= 8) strength += 1;
            if (password.length >= 12) strength += 1;
            
            // Complexity checks
            if (/[A-Z]/.test(password)) strength += 1;
            if (/[0-9]/.test(password)) strength += 1;
            if (/[!@#$%^&*]/.test(password)) strength += 1;
            
            // Update UI
            switch(strength) {
                case 0:
                case 1:
                    strengthBar.style.width = '25%';
                    strengthBar.style.backgroundColor = '#ff6b6b';
                    strengthText.textContent = 'Weak';
                    strengthText.style.color = '#ff6b6b';
                    break;
                case 2:
                case 3:
                    strengthBar.style.width = '50%';
                    strengthBar.style.backgroundColor = '#feca57';
                    strengthText.textContent = 'Moderate';
                    strengthText.style.color = '#feca57';
                    break;
                case 4:
                    strengthBar.style.width = '75%';
                    strengthBar.style.backgroundColor = '#48dbfb';
                    strengthText.textContent = 'Strong';
                    strengthText.style.color = '#48dbfb';
                    break;
                case 5:
                    strengthBar.style.width = '100%';
                    strengthBar.style.backgroundColor = '#1dd1a1';
                    strengthText.textContent = 'Very Strong';
                    strengthText.style.color = '#1dd1a1';
                    break;
            }
        }
    </script>
</body>
</html>