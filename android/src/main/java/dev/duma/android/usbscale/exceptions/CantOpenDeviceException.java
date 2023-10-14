package dev.duma.android.usbscale.exceptions;

public class CantOpenDeviceException extends Exception {
    public CantOpenDeviceException() {
        super("Could not open device, check if user has granted permission to access the device");
    }
}
