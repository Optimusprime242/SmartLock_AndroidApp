package com.example.hoangkhanh.smartlock;

import java.io.Serializable;

public class BleDevices implements Serializable {
    private String DeviceName;
    private String DeviceAddress;

    private boolean Active;

    public BleDevices(String name, String address)
    {
        this.DeviceName = name;
        this.DeviceAddress = address;
        this.Active = false;
    }

    public String getName()
    {
        return DeviceName;
    }

    public void setName(String name)
    {
        this.DeviceName = name;
    }

    public String getAddress()
    {
        return DeviceAddress;
    }

    public void setAddress(String address)
    {
        this.DeviceAddress = address;
    }

    public boolean isActive()
    {
        return Active;
    }

    public void setActive(boolean active) {
        this.Active = active;
    }

    @Override
    public String toString() {
        return this.DeviceName + this.DeviceAddress;
    }

}
