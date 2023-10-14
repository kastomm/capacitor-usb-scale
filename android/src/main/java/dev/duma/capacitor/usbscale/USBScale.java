package dev.duma.capacitor.usbscale;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import dev.duma.capacitor.usbscale.UsbPermissionsBroadcastReceiver.UsbPermissionsBroadcastReceiverCallback;


public class USBScale {
    private UsbManager manager;
    private IUSBScaleCallback callback;
    private AppCompatActivity activity;

    public USBScale(AppCompatActivity activity, IUSBScaleCallback callback) {
        this.activity = activity;
        this.manager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);

        this.callback = callback;

        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        UsbConnectionBroadcastReceiver usbConnectReceiver = new UsbConnectionBroadcastReceiver(callback);
        activity.registerReceiver(usbConnectReceiver, filter);
    }

    private PendingIntent mPermissionIntent;



    private Byte[] bytes;
    private static int TIMEOUT = 0;
    private boolean forceClaim = true;

    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    UsbDisconnectionBroadcastReceiver usbDisconnectReceiver;
    private USBThreadDataReceiver usbThreadDataReceiver;
    private UsbEndpoint endPointRead;
    private UsbDevice usedDevice;

    private int packetSize;



    public JSONArray enumerateDevices() throws JSONException {
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        JSONArray result = new JSONArray();
        for (UsbDevice usbDevice : deviceList.values()) {
            int vid = usbDevice.getVendorId();
            int pid = usbDevice.getProductId();

            if (vid != 2338 || pid != 32771)
                continue;

            JSONObject product = new JSONObject();
            product.put("manufacturer", usbDevice.getManufacturerName());
            product.put("name", usbDevice.getProductName());

            JSONObject obj = new JSONObject();
            obj.put("id", usbDevice.getDeviceName());
            obj.put("vid", vid);
            obj.put("pid", pid);
            obj.put("serial", usbDevice.getSerialNumber());
            obj.put("product", product);

            result.put(obj);
        }

        return result;
    }

    public void requestPermission(String device, UsbPermissionsBroadcastReceiverCallback callback) throws IOException {
        UsbDevice d = manager.getDeviceList().get(device);
        if (d == null) {
            throw new IOException(String.format("Unknown Device: %s", device));
        }

        mPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(UsbPermissionsBroadcastReceiver.USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(UsbPermissionsBroadcastReceiver.USB_PERMISSION);
        UsbPermissionsBroadcastReceiver usbReceiver = new UsbPermissionsBroadcastReceiver(callback, activity);
        activity.registerReceiver(usbReceiver, filter);

        manager.requestPermission(d, mPermissionIntent);
    }

    public void open(String device) throws IOException {
        UsbDevice d = manager.getDeviceList().get(device);

        if (d == null) {
            throw new IOException(String.format("Unknown Device: %s", device));
        }

        if (connection != null) {
            throw new IOException("Already open");
        }

        UsbDeviceConnection c = manager.openDevice(d);
        if (c == null) {
            throw new IOException("Could not open device, check if user has granted permission to access the device");
        }

        connection = c;
        usedDevice = d;
        this.usbInterface = d.getInterface(0);
        UsbEndpoint endpoint = usbInterface.getEndpoint(0);
        connection.claimInterface(usbInterface, forceClaim);
        if (UsbConstants.USB_DIR_IN == endpoint.getDirection()) {
            endPointRead = endpoint;
            packetSize = endPointRead.getMaxPacketSize();
        }

        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDisconnectReceiver = new UsbDisconnectionBroadcastReceiver(callback, activity, usedDevice);
        activity.registerReceiver(usbDisconnectReceiver, filter);

        usbThreadDataReceiver = new USBThreadDataReceiver();
        usbThreadDataReceiver.start();
    }

    public void stop() {
        if (connection == null)
            return;

        usbThreadDataReceiver.stopThis();
        connection.releaseInterface(usbInterface);
        connection.close();
        usbDisconnectReceiver.unregister();
        connection = null;
    }

    private class USBThreadDataReceiver extends Thread {
        private volatile boolean isStopped;

        public USBThreadDataReceiver() { }

        @Override
        public void run() {
            try {
                if (connection != null && endPointRead != null) {
                    while (!isStopped) {
                        final byte[] buffer = new byte[packetSize];
                        final int status = connection.bulkTransfer(endPointRead, buffer, packetSize, 100);

                        if (buffer[0] != 3)
                            continue;

                        String stringStatus = switch (buffer[1]) {
                            case 1 -> "Fault";
                            case 2 -> "Zero";
                            case 3 -> "InMotion";
                            case 4 -> "Stable";
                            case 5 -> "UnderZero";
                            case 6 -> "OverWeight";
                            case 7 -> "NeedCalibration";
                            case 8 -> "NeedZeroing";
                            default -> "Unknown";
                        };

                        double weight = (Byte.toUnsignedInt(buffer[4]) + Byte.toUnsignedInt(buffer[5]) * 256) * Math.pow(10, buffer[3]);
                        switch (buffer[2])
                        {
                            case 2:  // Scale reading in g
                                break;
                            case 11: // Ounces
                                weight *= 28.34952;
                                weight = Math.round(weight * 100.0) / 100.0;
                                break;
                        }

                        callback.OnRead(Arrays.toString(buffer), stringStatus, weight);
                    }
                }
            } catch (Exception e) {
                Log.e("USBThreadDataReceiver", "Error in receive thread", e);
            }
        }

        public void stopThis() {
            isStopped = true;
        }
    }
}
