package dev.duma.capacitor.usbscale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public class UsbConnectionBroadcastReceiver extends BroadcastReceiver {
    private final IUSBScaleCallback callback;

    public UsbConnectionBroadcastReceiver(IUSBScaleCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device == null) {
                return;
            }

            callback.OnScaleConnected(device);
        }
    }
}