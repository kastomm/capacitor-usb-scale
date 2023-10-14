package dev.duma.capacitor.usbscale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

public class UsbPermissionsBroadcastReceiver extends BroadcastReceiver {
    public static final String USB_PERMISSION ="dev.duma.capacitor.usbscale.USB_PERMISSION";
    private final UsbPermissionsBroadcastReceiverCallback callback;
    private final Context activity;

    public UsbPermissionsBroadcastReceiver(UsbPermissionsBroadcastReceiverCallback callback, Context context) {
        this.callback = callback;
        this.activity = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (USB_PERMISSION.equals(action)) {
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                callback.run(true);
            } else {
                callback.run(true);
            }

            activity.unregisterReceiver(this);
        }
    }

    public interface UsbPermissionsBroadcastReceiverCallback {
        void run(boolean status);
    }
}