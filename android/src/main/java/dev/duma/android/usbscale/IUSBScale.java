package dev.duma.android.usbscale;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import dev.duma.android.usbscale.enums.StatusEnum;
import dev.duma.android.usbscale.exceptions.CantOpenDeviceException;
import dev.duma.android.usbscale.exceptions.DeviceNotFoundException;
import dev.duma.android.usbscale.exceptions.OpenedDeviceEndpointIsNotInputEndpoint;

public interface IUSBScale {
    @NonNull
    ArrayList<DeviceInfo> enumerateDevices();

    void requestPermission(String device, UsbPermissionsBroadcastReceiver.Callback callback) throws DeviceNotFoundException;

    boolean hasPermission(String device_id) throws DeviceNotFoundException;

    @NonNull
    UsbDevice open(String device) throws DeviceNotFoundException, CantOpenDeviceException, OpenedDeviceEndpointIsNotInputEndpoint;

    void close();

    class Factory
    {
        static public IUSBScale make(Context context, Callback callback) {
            return new USBScale(context, callback);
        }
    }

    interface Callback {
        void OnRead(String data, StatusEnum status, double weight);
    }
}
