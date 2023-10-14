package dev.duma.capacitor.usbscale;

import android.hardware.usb.UsbDevice;

import dev.duma.android.usbscale.enums.StatusEnum;

public interface IUSBScaleCallback {
    void OnRead(String data, StatusEnum status, double weight);
    void OnScaleConnected(UsbDevice device);
    void OnScaleDisconnected(UsbDevice device);
}
