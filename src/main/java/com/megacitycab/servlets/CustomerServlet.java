package com.megacitycab.servlets;

import com.megacitycab.dao.CarDAO;
import com.megacitycab.dao.DriverDAO;
import com.megacitycab.models.Car;
import com.megacitycab.models.Driver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/CustomerServlet")
public class CustomerServlet extends HttpServlet {
    private CarDAO carDAO;
    private DriverDAO driverDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
        driverDAO = new DriverDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Car> availableCars = carDAO.getAvailableCars();
        if (availableCars == null) availableCars = new ArrayList<>();
        request.setAttribute("availableCars", availableCars);

        System.out.println("Fetched Available Cars: " + availableCars.size());
        for (Car car : availableCars) {
            System.out.println("Car: " + car.getCarModel() + " - " + car.getCarLicensePlate());
        }


        List<Driver> availableDrivers = driverDAO.getAvailableDrivers();
        if (availableDrivers == null) availableDrivers = new ArrayList<>();
        request.setAttribute("availableDrivers", availableDrivers);

        System.out.println("Fetched Available Drivers: " + availableDrivers.size());
        for (Driver driver : availableDrivers) {
            System.out.println("Driver: " + driver.getDriverName() + " - " + driver.getPhoneNumber());
        }


        request.getRequestDispatcher("customerIndex.jsp").forward(request, response);
    }
}
