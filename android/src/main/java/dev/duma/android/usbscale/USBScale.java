package dev.duma.android.usbscale;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import dev.duma.android.usbscale.exceptions.CantOpenDeviceException;
import dev.duma.android.usbscale.exceptions.DeviceNotFoundException;
import dev.duma.android.usbscale.exceptions.OpenedDeviceEndpointIsNotInputEndpoint;
import dev.duma.capacitor.usbscale.UsbPermissionsBroadcastReceiver;

public class USBScale implements IUSBScale {
    private final Context context;
    private final UsbManager usbManager;
    private final Callback callback;
    private ConnectedUSBScaleReader reader = null;

    public USBScale(Context context, Callback callback) {
        this.context = context;
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.callback = callback;
    }

    @NonNull
    @Override
    public ArrayList<DeviceInfo> enumerateDevices() {
        ArrayList<DeviceInfo> deviceList = new ArrayList<>();

        for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
            if (!checkIfUsbDeviceIsSupported(usbDevice))
                continue;

            deviceList.add(new DeviceInfo(
                usbDevice.getDeviceName(),
                usbDevice.getVendorId(),
                usbDevice.getProductId(),
                usbDevice.getSerialNumber(),
                usbDevice.getManufacturerName(),
                usbDevice.getProductName()
            ));
        }

        return deviceList;
    }

    public void requestPermission(String device_id, UsbPermissionsBroadcastReceiver.Callback callback) throws DeviceNotFoundException {
        UsbDevice usbDevice = getUsbDeviceFromId(device_id);

        UsbPermissionsBroadcastReceiver usbReceiver = new UsbPermissionsBroadcastReceiver(callback, context);
        PendingIntent mPermissionIntent = usbReceiver.register();

        usbManager.requestPermission(usbDevice, mPermissionIntent);
    }

    @NonNull
    @Override
    public UsbDevice open(String device_id) throws DeviceNotFoundException, CantOpenDeviceException, OpenedDeviceEndpointIsNotInputEndpoint {
        if (reader != null) {
            throw new RuntimeException("Connection is already open");
        }

        UsbDevice usbDevice = getUsbDeviceFromId(device_id);

        UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
        if (connection == null) {
            throw new CantOpenDeviceException();
        }

        reader = ConnectedUSBScaleReader.make(usbDevice, connection, (data, status, weight) -> callback.OnRead(data, status, weight));

        return usbDevice;
    }

    @Override
    public void close() {
        if (reader == null)
            return;

        reader.close();
        reader = null;
    }

    private boolean checkIfUsbDeviceIsSupported(UsbDevice device) {
        int vid = device.getVendorId();
        int pid = device.getProductId();

        return vid == 2338 && pid == 32771;
    }

    @NonNull
    private UsbDevice getUsbDeviceFromId(String device_id) throws DeviceNotFoundException {
        UsbDevice usbDevice = usbManager.getDeviceList().get(device_id);

        if (usbDevice == null) {
            throw new DeviceNotFoundException(device_id);
        }

        return usbDevice;
    }
}
