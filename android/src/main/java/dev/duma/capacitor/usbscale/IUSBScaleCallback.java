package dev.duma.capacitor.usbscale;

import android.hardware.usb.UsbDevice;

public interface IUSBScaleCallback {
    void OnRead(String data, String status, double weight);
    void OnScaleConnected(UsbDevice device);
    void OnScaleDisconnected(UsbDevice device);
}
