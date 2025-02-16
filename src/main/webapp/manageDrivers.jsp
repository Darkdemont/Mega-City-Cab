<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.Driver" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Drivers</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-4">
<h2 class="mb-4">Manage Drivers</h2>

<!-- Add Driver Form -->
<form action="manageDrivers" method="POST" class="mb-4">
    <div class="row g-3">
        <div class="col-md-3">
            <input type="text" name="driverName" class="form-control" placeholder="Driver Name" required>
        </div>
        <div class="col-md-3">
            <input type="text" name="driverLicense" class="form-control" placeholder="License Number" required>
        </div>
        <div class="col-md-2">
            <input type="text" name="phoneNumber" class="form-control" placeholder="Phone Number" required>
        </div>
        <div class="col-md-2">
            <select name="driverStatus" class="form-select">
                <option value="Available">Available</option>
                <option value="Unavailable">Unavailable</option>
                <option value="On Trip">On Trip</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary">Add Driver</button>
        </div>
    </div>
</form>

<!-- Driver List -->
<h3>Driver List:</h3>
<table class="table table-striped">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>License</th>
        <th>Phone</th>
        <th>Status</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <% List<Driver> drivers = (List<Driver>) request.getAttribute("drivers"); %>
    <% if (drivers != null) { for (Driver driver : drivers) { %>
    <tr>
        <td><%= driver.getDriverId() %></td>
        <td><%= driver.getDriverName() %></td>
        <td><%= driver.getDriverLicense() %></td>
        <td><%= driver.getPhoneNumber() %></td>
        <td><%= driver.getDriverStatus() %></td>
        <td>
            <button class="btn btn-warning btn-sm" onclick="editDriver('<%= driver.getDriverId() %>', '<%= driver.getDriverName() %>', '<%= driver.getDriverLicense() %>', '<%= driver.getPhoneNumber() %>', '<%= driver.getDriverStatus() %>')">Edit</button>
            <form action="manageDrivers" method="POST" style="display:inline;">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="driverId" value="<%= driver.getDriverId() %>">
                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
            </form>
        </td>
    </tr>
    <% } } %>
    </tbody>
</table>

<!-- Edit Driver Modal -->
<div class="modal fade" id="editDriverModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Edit Driver</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="manageDrivers" method="POST">
                <div class="modal-body">
                    <input type="hidden" name="driverId" id="editDriverId">
                    <div class="mb-3">
                        <label>Driver Name</label>
                        <input type="text" name="driverName" id="editDriverName" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label>License Number</label>
                        <input type="text" name="driverLicense" id="editDriverLicense" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label>Phone Number</label>
                        <input type="text" name="phoneNumber" id="editDriverPhoneNumber" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label>Status</label>
                        <select name="driverStatus" id="editDriverStatus" class="form-select">
                            <option value="Available">Available</option>
                            <option value="Unavailable">Unavailable</option>
                            <option value="On Trip">On Trip</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-success">Update Driver</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function editDriver(id, name, license, phone, status) {
        document.getElementById('editDriverId').value = id;
        document.getElementById('editDriverName').value = name;
        document.getElementById('editDriverLicense').value = license;
        document.getElementById('editDriverPhoneNumber').value = phone;
        document.getElementById('editDriverStatus').value = status;
        new bootstrap.Modal(document.getElementById('editDriverModal')).show();
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
