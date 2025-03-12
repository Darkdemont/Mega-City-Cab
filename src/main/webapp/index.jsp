<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MegaCityCab - Home</title>


    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">


    <link rel="stylesheet" href="assets/css/home.css">
</head>
<body>


<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">🚖 MegaCityCab</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="index.jsp">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="about.jsp">About</a></li>
                <li class="nav-item"><a class="nav-link" href="services.jsp">Services</a></li>
                <li class="nav-item"><a class="nav-link" href="manageBookings.jsp">Manage Bookings</a></li>
                <li class="nav-item"><a class="nav-link" href="payment.jsp">Payments</a></li>
                <li class="nav-item"><a class="nav-link" href="contact.jsp">Contact</a></li>
                <li class="nav-item">
                    <a class="nav-link btn btn-warning text-dark ms-2" href="signup.jsp">Sign Up</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link btn btn-light text-dark ms-2" href="login.jsp">Login</a>
                </li>
            </ul>
        </div>
    </div>
</nav>


<header class="hero">
    <video autoplay loop muted playsinline class="hero-video">
        <source src="assets/Images/Home video.mp4" type="video/mp4">
        Your browser does not support the video tag.
    </video>
    <div class="overlay"></div>
    <div class="container">
        <h1 class="display-4">Welcome to MegaCityCab</h1>
        <p class="lead">Your Reliable and Fast Cab Service in Colombo</p>
        <a href="signup.jsp" class="btn btn-warning btn-lg mt-3">Book a Ride Now</a>
    </div>
</header>


<section class="features py-5">
    <div class="container">
        <div class="row text-center">
            <div class="col-md-4">
                <h3>🚗 Easy Booking</h3>
                <p>Book a cab anytime, anywhere with just a few clicks.</p>
            </div>
            <div class="col-md-4">
                <h3>💰 Affordable Fares</h3>
                <p>Transparent pricing with no hidden charges.</p>
            </div>
            <div class="col-md-4">
                <h3>👨‍✈️ Trusted Drivers</h3>
                <p>Licensed and professional drivers for your safety.</p>
            </div>
        </div>
    </div>
</section>


<section class="about bg-light py-5">
    <div class="container text-center">
        <h2>Why Choose MegaCityCab?</h2>
        <p class="lead">MegaCityCab is the most trusted cab service in Colombo, ensuring timely and safe rides for thousands of customers every month.</p>
        <a href="signup.jsp" class="btn btn-primary mt-3">Get Started</a>
    </div>
</section>


<footer class="footer bg-dark text-white text-center py-3">
    <div class="container">
        <p>&copy; 2025 MegaCityCab. All Rights Reserved.</p>
        <p>📍 Colombo, Sri Lanka | 📞 +94 77 123 4567 | ✉️ support@megacitycab.com</p>
    </div>
</footer>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
