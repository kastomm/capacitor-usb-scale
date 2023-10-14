package dev.duma.android.usbscale.broadcastreceiver;

import android.content.Context;
import android.hardware.usb.UsbDevice;

public interface IUSBScaleBroadcastReceiver {
    void register();

    void unregister();

    void setCurrentlyOpenedDevice(UsbDevice currentlyOpenedDevice);

    interface Callback {
        void OnScaleConnected(UsbDevice device);
        void OnOpenedScaleDisconnected(UsbDevice device);
        void OnScaleDisconnected(UsbDevice device);
    }

    class Factory
    {
        static public IUSBScaleBroadcastReceiver make(Context context, Callback callback) {
            return new USBScaleBroadcastReceiver(context, callback);
        }
    }
}
