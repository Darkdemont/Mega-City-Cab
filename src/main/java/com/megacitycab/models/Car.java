package com.megacitycab.models;

public class Car {
    private int carId;
    private String carModel;
    private String carLicensePlate;
    private int carCapacity;
    private String carStatus;


    public Car(int carId, String carModel, String carLicensePlate, int carCapacity, String carStatus) {
        this.carId = carId;
        this.carModel = carModel;
        this.carLicensePlate = carLicensePlate;
        this.carCapacity = carCapacity;
        this.carStatus = carStatus;
    }


    public Car(String carModel, String carLicensePlate, int carCapacity, String carStatus) {
        this.carModel = carModel;
        this.carLicensePlate = carLicensePlate;
        this.carCapacity = carCapacity;
        this.carStatus = carStatus;
    }


    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCarLicensePlate() { return carLicensePlate; }
    public void setCarLicensePlate(String carLicensePlate) { this.carLicensePlate = carLicensePlate; }

    public int getCarCapacity() { return carCapacity; }
    public void setCarCapacity(int carCapacity) { this.carCapacity = carCapacity; }

    public String getCarStatus() { return carStatus; }
    public void setCarStatus(String carStatus) { this.carStatus = carStatus; }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", carModel='" + carModel + '\'' +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", carCapacity=" + carCapacity +
                ", carStatus='" + carStatus + '\'' +
                '}';
    }
}
