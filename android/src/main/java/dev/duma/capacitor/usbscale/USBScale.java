package dev.duma.capacitor.usbscale;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dev.duma.android.usbscale.DeviceInfo;
import dev.duma.android.usbscale.IUSBScale;
import dev.duma.android.usbscale.broadcastreceiver.IUSBScaleBroadcastReceiver;
import dev.duma.android.usbscale.exceptions.CantOpenDeviceException;
import dev.duma.android.usbscale.exceptions.DeviceNotFoundException;
import dev.duma.android.usbscale.exceptions.OpenedDeviceEndpointIsNotInputEndpoint;
import dev.duma.capacitor.usbscale.UsbPermissionsBroadcastReceiver.Callback;


public class USBScale {
    /** @noinspection FieldCanBeLocal*/
    private final IUSBScaleBroadcastReceiver.Callback broadcastReceiverCallback = new IUSBScaleBroadcastReceiver.Callback() {
        @Override
        public void OnScaleConnected(UsbDevice device) {
            callback.OnScaleConnected(device);
        }

        @Override
        public void OnOpenedScaleDisconnected(UsbDevice device) {
            callback.OnScaleDisconnected(device);
        }

        @Override
        public void OnScaleDisconnected(UsbDevice device) {
            // Do nothing
        }
    };

    private final IUSBScaleCallback callback;
    private final IUSBScaleBroadcastReceiver broadcastReceiver;
    private final IUSBScale usbScale;

    public USBScale(AppCompatActivity activity, IUSBScaleCallback callback) {
        this.callback = callback;

        broadcastReceiver = IUSBScaleBroadcastReceiver.Factory.make(activity, broadcastReceiverCallback);
        usbScale = IUSBScale.Factory.make(activity, (data, status, weight) -> callback.OnRead(data, status, weight));
    }

    public void register() {
        broadcastReceiver.register();
    }

    public void unregister() {
        broadcastReceiver.unregister();
    }

    @NonNull
    public ArrayList<DeviceInfo> enumerateDevices() {
        return usbScale.enumerateDevices();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void requestPermission(String device, Callback callback) throws DeviceNotFoundException {
        usbScale.requestPermission(device, callback);
    }

    public void open(String device) throws DeviceNotFoundException, CantOpenDeviceException, OpenedDeviceEndpointIsNotInputEndpoint {
        UsbDevice usbDevice = usbScale.open(device);
        broadcastReceiver.setCurrentlyOpenedDevice(usbDevice);
    }

    public void stop() {
        usbScale.close();
        broadcastReceiver.setCurrentlyOpenedDevice(null);
    }
}
