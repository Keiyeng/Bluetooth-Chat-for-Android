package com.example.utar.bluetoothchat;

/**
 * Created by Yumiko on 7/1/2016.
 */
public class Device {
    private boolean connected;
    private String deviceName;
    private String deviceAddress;



    public Device(String deviceName, String deviceAddress) {

        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
    }

    public boolean getConnected() {
        return connected;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }




}
