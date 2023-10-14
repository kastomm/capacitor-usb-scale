package dev.duma.capacitor.usbscale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import org.json.JSONObject;

public class UsbDisconnectionBroadcastReceiver extends BroadcastReceiver {
    private final IUSBScaleCallback callback;
    private final Activity activity;
    private UsbDevice usedDevice;

    public UsbDisconnectionBroadcastReceiver(IUSBScaleCallback callback, Activity activity, UsbDevice usedDevice) {
        this.callback = callback;
        this.activity = activity;
        this.usedDevice = usedDevice;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device == null || !usedDevice.getDeviceName().equals(device.getDeviceName())) {
                return;
            }

            callback.OnScaleDisconnected(device);
            unregister();
        }
    }

    private boolean registered = true;
    public void unregister() {
        if(!registered)
            return;

        activity.unregisterReceiver(this);
        registered = false;
    }
}