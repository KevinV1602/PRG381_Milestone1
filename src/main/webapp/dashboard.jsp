<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.*, javax.servlet.*" %>
<%@ page session="true" %>
<%
    String studentName = (String) session.getAttribute("studentName");
    // Removed: Boolean loginSuccess = (Boolean) session.getAttribute("loginSuccess");
    // Removed: if (loginSuccess != null && loginSuccess) { session.removeAttribute("loginSuccess"); }

    if (studentName == null) {
       response.sendRedirect("login.jsp");
       return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - BC Student Wellness</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="icon" type="image/x-icon" href="favicon.ico">
    <%-- Removed: Font Awesome CSS link --%>
</head>
<body>
    <header class="main-header">
        <div class="header-content">
            <a href="index.jsp" class="logo">
                <img src="images/search.png" alt="BC Wellness Logo" class="logo-img">
                <span class="logo-text">BC Wellness</span>
            </a>
            <nav class="main-nav">
                <ul>
                    <%-- Logout button in Navbar --%>
                    <li>
                        <form action="LogoutServlet" method="post" class="nav-logout-form">
                            <input type="submit" value="Logout" class="nav-button nav-logout-btn" />
                        </form>
                    </li>
                </ul>
            </nav>
        </div>
    </header>

    <main class="main-content">
        <div class="container dashboard-content-area">
            <%-- Personalized Welcome Message --%>
            <h1>Welcome, <%= studentName %>!</h1>

            <p class="dashboard-intro">
                You are now logged in to the BC Student Wellness Management System.
                Manage your wellness journey here.
            </p>

            <hr class="separator"> <%-- Visual separator for main action --%>


    </main>

    <footer class="main-footer">
        <div class="footer-content">
            <p>&copy; 2025 Belgium Campus. All rights reserved.</p>
            <p class="attribution-text">
                <img src="images/heart.png" alt="Heart icon" class="logo-img">
            </p>
        </div>
    </footer>
</body>
</html>