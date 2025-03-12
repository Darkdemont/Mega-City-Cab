package com.megacitycab.servlets;

import com.megacitycab.dao.CarDAO;
import com.megacitycab.models.Car;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/manageCars")
public class CarServlet extends HttpServlet {
    private CarDAO carDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Car> cars = carDAO.getAllCars();
        request.setAttribute("cars", cars);
        request.getRequestDispatcher("manageCars.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int carId = Integer.parseInt(request.getParameter("carId"));
            boolean deleted = carDAO.deleteCar(carId);
            System.out.println("Car Deletion Status: " + deleted);
        } else if (request.getParameter("carId") != null && !request.getParameter("carId").isEmpty()) {
            // Update car details
            int carId = Integer.parseInt(request.getParameter("carId"));
            String carModel = request.getParameter("carModel");
            String carLicensePlate = request.getParameter("carLicensePlate");
            int carCapacity = Integer.parseInt(request.getParameter("carCapacity"));
            String carStatus = request.getParameter("carStatus");

            Car car = new Car(carId, carModel, carLicensePlate, carCapacity, carStatus);
            boolean updated = carDAO.updateCar(car);
            System.out.println("Car Update Status: " + updated);
        } else {
            // Add new car
            String carModel = request.getParameter("carModel");
            String carLicensePlate = request.getParameter("carLicensePlate");
            int carCapacity = Integer.parseInt(request.getParameter("carCapacity"));
            String carStatus = request.getParameter("carStatus");

            Car car = new Car(carModel, carLicensePlate, carCapacity, carStatus);
            boolean added = carDAO.addCar(car);
            System.out.println("Car Addition Status: " + added);
        }

        response.sendRedirect("manageCars");
    }
}
