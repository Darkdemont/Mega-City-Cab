document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("login-form");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const usernameMessage = document.getElementById("username-message");
    const passwordMessage = document.getElementById("password-message");
    const togglePassword = document.getElementById("togglePassword");

    // Toggle password visibility
    togglePassword.addEventListener("click", function () {
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            togglePassword.textContent = "🙈";
        } else {
            passwordInput.type = "password";
            togglePassword.textContent = "👁️";
        }
    });

    // Real-time Validation
    usernameInput.addEventListener("input", function () {
        if (usernameInput.value.trim().length < 3) {
            showMessage(usernameMessage, "Username must be at least 3 characters long!", "error");
            setErrorStyle(usernameInput);
        } else {
            clearMessage(usernameMessage);
            setSuccessStyle(usernameInput);
        }
    });

    passwordInput.addEventListener("input", function () {
        if (passwordInput.value.trim().length < 6) {
            showMessage(passwordMessage, "Password must be at least 6 characters long!", "error");
            setErrorStyle(passwordInput);
        } else {
            clearMessage(passwordMessage);
            setSuccessStyle(passwordInput);
        }
    });

    // Prevent form submission if errors exist
    form.addEventListener("submit", function (event) {
        let valid = true;

        if (usernameInput.value.trim().length < 3) {
            showMessage(usernameMessage, "Username must be at least 3 characters long!", "error");
            setErrorStyle(usernameInput);
            valid = false;
        }

        if (passwordInput.value.trim().length < 6) {
            showMessage(passwordMessage, "Password must be at least 6 characters long!", "error");
            setErrorStyle(passwordInput);
            valid = false;
        }

        if (!valid) {
            event.preventDefault(); // Stop form submission if there are errors
        }
    });

    function showMessage(element, message, type) {
        element.textContent = message;
        element.className = type === "error" ? "error-message show-message" : "success-message show-message";
    }

    function clearMessage(element) {
        element.textContent = "";
        element.classList.remove("show-message");
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