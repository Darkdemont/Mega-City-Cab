<%@ page import="java.util.List" %>
<%@ page import="com.megacitycab.models.Car" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Manage Cars</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-4">
<h2 class="mb-4">Manage Cars</h2>

<!-- Add Car Form -->
<form action="manageCars" method="POST" class="mb-4">
  <div class="row g-3">
    <div class="col-md-3">
      <input type="text" name="carModel" class="form-control" placeholder="Car Model" required>
    </div>
    <div class="col-md-3">
      <input type="text" name="carLicensePlate" class="form-control" placeholder="License Plate" required>
    </div>
    <div class="col-md-2">
      <input type="number" name="carCapacity" class="form-control" placeholder="Capacity" required>
    </div>
    <div class="col-md-2">
      <select name="carStatus" class="form-select">
        <option value="Available">Available</option>
        <option value="Unavailable">Unavailable</option>
        <option value="In Service">In Service</option>
      </select>
    </div>
    <div class="col-md-2">
      <button type="submit" class="btn btn-primary">Add Car</button>
    </div>
  </div>
</form>

<!-- Car List -->
<h3>Car List:</h3>
<table class="table table-striped">
  <thead>
  <tr>
    <th>ID</th>
    <th>Model</th>
    <th>License Plate</th>
    <th>Capacity</th>
    <th>Status</th>
    <th>Actions</th>
  </tr>
  </thead>
  <tbody>
  <% List<Car> cars = (List<Car>) request.getAttribute("cars"); %>
  <% if (cars != null) { for (Car car : cars) { %>
  <tr>
    <td><%= car.getCarId() %></td>
    <td><%= car.getCarModel() %></td>
    <td><%= car.getCarLicensePlate() %></td>
    <td><%= car.getCarCapacity() %></td>
    <td><%= car.getCarStatus() %></td>
    <td>
      <button class="btn btn-warning btn-sm" onclick="editCar('<%= car.getCarId() %>', '<%= car.getCarModel() %>', '<%= car.getCarLicensePlate() %>', '<%= car.getCarCapacity() %>', '<%= car.getCarStatus() %>')">Edit</button>
      <form action="manageCars" method="POST" style="display:inline;">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="carId" value="<%= car.getCarId() %>">
        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
      </form>
    </td>
  </tr>
  <% } } %>
  </tbody>
</table>

<!-- Edit Car Modal -->
<div class="modal fade" id="editCarModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Edit Car</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <form action="manageCars" method="POST">
        <div class="modal-body">
          <input type="hidden" name="carId" id="editCarId">
          <div class="mb-3">
            <label>Car Model</label>
            <input type="text" name="carModel" id="editCarModel" class="form-control" required>
          </div>
          <div class="mb-3">
            <label>License Plate</label>
            <input type="text" name="carLicensePlate" id="editCarLicensePlate" class="form-control" required>
          </div>
          <div class="mb-3">
            <label>Capacity</label>
            <input type="number" name="carCapacity" id="editCarCapacity" class="form-control" required>
          </div>
          <div class="mb-3">
            <label>Status</label>
            <select name="carStatus" id="editCarStatus" class="form-select">
              <option value="Available">Available</option>
              <option value="Unavailable">Unavailable</option>
              <option value="In Service">In Service</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button type="submit" class="btn btn-success">Update Car</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
  function editCar(id, model, plate, capacity, status) {
    document.getElementById('editCarId').value = id;
    document.getElementById('editCarModel').value = model;
    document.getElementById('editCarLicensePlate').value = plate;
    document.getElementById('editCarCapacity').value = capacity;
    document.getElementById('editCarStatus').value = status;
    new bootstrap.Modal(document.getElementById('editCarModal')).show();
  }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
