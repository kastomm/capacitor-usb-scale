package dev.duma.android.usbscale.exceptions;

public class DeviceNotFoundException extends Exception{
    public DeviceNotFoundException(String device_id) {
        super("Device not found: " + device_id);
    }
}
