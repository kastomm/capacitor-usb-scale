package dev.duma.android.usbscale;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import java.util.Objects;

public class UsbPermissionsBroadcastReceiver {
    public static final String USB_PERMISSION ="dev.duma.capacitor.usbscale.USB_PERMISSION";

    private final Callback callback;
    private final Context context;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        if (!Objects.requireNonNull(intent.getAction()).equals(USB_PERMISSION)) {
            return;
        }

        callback.run(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false));

        context.unregisterReceiver(receiver);
        }
    };

    public UsbPermissionsBroadcastReceiver(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @SuppressLint({"UnspecifiedRegisterReceiverFlag", "UnspecifiedImmutableFlag"})
    public PendingIntent register() {
        IntentFilter filter = new IntentFilter(USB_PERMISSION);

        context.registerReceiver(receiver, filter);

        return PendingIntent.getBroadcast(context, 0, new Intent(USB_PERMISSION), 0);
    }


    public interface Callback {
        void run(boolean status);
    }
}