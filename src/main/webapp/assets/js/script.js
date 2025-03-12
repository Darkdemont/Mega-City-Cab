document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("signup-form");
    const usernameInput = document.getElementById("username");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const usernameMessage = document.getElementById("username-message");
    const emailMessage = document.getElementById("email-message");
    const passwordMessage = document.getElementById("password-message");

    // Real-time validation
    usernameInput.addEventListener("input", function () {
        if (usernameInput.value.trim().length < 3) {
            showInlineMessage(usernameMessage, "Username must be at least 3 characters long!", "error");
            setErrorStyle(usernameInput);
        } else {
            clearInlineMessage(usernameMessage);
            setSuccessStyle(usernameInput);
        }
    });

    emailInput.addEventListener("input", function () {
        if (!validateEmail(emailInput.value.trim())) {
            showInlineMessage(emailMessage, "Invalid email format!", "error");
            setErrorStyle(emailInput);
        } else {
            clearInlineMessage(emailMessage);
            setSuccessStyle(emailInput);
        }
    });

    passwordInput.addEventListener("input", function () {
        if (passwordInput.value.trim().length < 6) {
            showInlineMessage(passwordMessage, "Password must be at least 6 characters long!", "error");
            setErrorStyle(passwordInput);
        } else {
            showInlineMessage(passwordMessage, "Password strength is good!", "success");
            setSuccessStyle(passwordInput);
        }
    });

    function validateEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    function showInlineMessage(element, message, type = "error") {
        element.textContent = message;
        element.className = type === "error" ? "error-message" : "success-message";
        element.style.display = "block";
    }

    function clearInlineMessage(element) {
        element.textContent = "";
        element.style.display = "none";
    }

    function setErrorStyle(inputElement) {
        inputElement.classList.add("input-error");
        inputElement.classList.remove("input-success");
    }

    function setSuccessStyle(inputElement) {
        inputElement.classList.add("input-success");
        inputElement.classList.remove("input-error");
    }
});
