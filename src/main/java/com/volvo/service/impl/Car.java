package com.volvo.service.impl;

import com.volvo.service.Vehicle;

public class Car implements Vehicle {
    private String type;
    public Car(String type){
        this.type = type;
    }
    public String getVehicleType() {
        return type;
    }

}
