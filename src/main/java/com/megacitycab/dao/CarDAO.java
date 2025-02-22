package com.megacitycab.dao;

import com.megacitycab.models.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    private Connection conn;

    public CarDAO() {
        conn = DatabaseConnection.getConnection();
    }

    // Add a new car
    public boolean addCar(Car car) {
        String query = "INSERT INTO cars (car_model, car_license_plate, car_capacity, car_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, car.getCarModel());
            stmt.setString(2, car.getCarLicensePlate());
            stmt.setInt(3, car.getCarCapacity());
            stmt.setString(4, car.getCarStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all cars
    public List<Car> getAllCars() {
        String query = "SELECT * FROM cars";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(new Car(
                        rs.getInt("car_id"),
                        rs.getString("car_model"),
                        rs.getString("car_license_plate"),
                        rs.getInt("car_capacity"),
                        rs.getString("car_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    // Get available cars
    public List<Car> getAvailableCars() {
        String query = "SELECT * FROM cars WHERE car_status = 'Available'";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(new Car(
                        rs.getInt("car_id"),
                        rs.getString("car_model"),
                        rs.getString("car_license_plate"),
                        rs.getInt("car_capacity"),
                        rs.getString("car_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Available Cars Fetched: " + cars.size());
        return cars;
    }


    public boolean deleteCar(int carId) {
        String query = "DELETE FROM cars WHERE car_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, carId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateCar(Car car) {
        String query = "UPDATE cars SET car_model = ?, car_license_plate = ?, car_capacity = ?, car_status = ? WHERE car_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, car.getCarModel());
            stmt.setString(2, car.getCarLicensePlate());
            stmt.setInt(3, car.getCarCapacity());
            stmt.setString(4, car.getCarStatus());
            stmt.setInt(5, car.getCarId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
