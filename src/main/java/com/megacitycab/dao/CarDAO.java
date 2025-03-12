package com.megacitycab.dao;

import com.megacitycab.models.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarDAO {
    private static final Logger logger = Logger.getLogger(CarDAO.class.getName());
    private Connection conn;

    public CarDAO() {
        conn = DatabaseConnection.getConnection();
    }


    public boolean addCar(Car car) {
        String query = "INSERT INTO cars (car_model, car_license_plate, car_capacity, car_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, car.getCarModel());
            stmt.setString(2, car.getCarLicensePlate());
            stmt.setInt(3, car.getCarCapacity());
            stmt.setString(4, car.getCarStatus());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in addCar: ", e);
        }
        return false;
    }


    public List<Car> getAllCars() {
        String query = "SELECT * FROM cars";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAllCars: ", e);
        }
        return cars;
    }


    public List<Car> getAvailableCars() {
        String query = "SELECT * FROM cars WHERE car_status = 'Available'";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getAvailableCars: ", e);
        }
        logger.info("✅ Available Cars Fetched: " + cars.size());
        return cars;
    }


    public Car getCarByModel(String model) {
        String query = "SELECT * FROM cars WHERE car_model = ? AND car_status = 'Available' LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, model);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCar(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in getCarByModel: ", e);
        }
        logger.warning("🚨 No available car found for model: " + model);
        return null; // 🚨 Return null if no car found
    }


    public boolean deleteCar(int carId) {
        String query = "DELETE FROM cars WHERE car_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, carId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Error in deleteCar: ", e);
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
            logger.log(Level.SEVERE, "❌ Error in updateCar: ", e);
        }
        return false;
    }


    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("car_id"),
                rs.getString("car_model"),
                rs.getString("car_license_plate"),
                rs.getInt("car_capacity"),
                rs.getString("car_status")
        );
    }
}
