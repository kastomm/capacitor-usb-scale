package dev.duma.android.usbscale;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import java.util.Arrays;

import dev.duma.android.usbscale.exceptions.OpenedDeviceEndpointIsNotInputEndpoint;

public class ConnectedUSBScaleReader {
    private final UsbDevice usbDevice;
    private final UsbDeviceConnection connection;
    private final IUSBScale.Callback callback;

    protected static ConnectedUSBScaleReader make(UsbDevice usbDevice, UsbDeviceConnection connection, IUSBScale.Callback callback) throws OpenedDeviceEndpointIsNotInputEndpoint {
        ConnectedUSBScaleReader reader = new ConnectedUSBScaleReader(usbDevice, connection, callback);

        reader.open();

        return reader;
    }

    private ConnectedUSBScaleReader(UsbDevice usbDevice, UsbDeviceConnection connection, IUSBScale.Callback callback) {
        this.usbDevice = usbDevice;
        this.connection = connection;
        this.callback = callback;
    }

    private UsbInterface usbInterface;
    private USBThreadDataReceiver usbThreadDataReceiver;

    private void open() throws OpenedDeviceEndpointIsNotInputEndpoint {
        usbInterface = usbDevice.getInterface(0);

        UsbEndpoint endpoint = usbInterface.getEndpoint(0);

        boolean forceClaim = true;
        connection.claimInterface(usbInterface, forceClaim);

        if (UsbConstants.USB_DIR_IN != endpoint.getDirection()) {
            connection.releaseInterface(usbInterface);
            connection.close();

            throw new OpenedDeviceEndpointIsNotInputEndpoint();
        }

        usbThreadDataReceiver = new USBThreadDataReceiver(endpoint);
        usbThreadDataReceiver.start();
    }

    protected void close() {
        if(usbThreadDataReceiver == null)
            return;

        usbThreadDataReceiver.stopThis();

        connection.releaseInterface(usbInterface);
        connection.close();
    }

    private class USBThreadDataReceiver extends Thread {
        private final int maxPacketSize;
        private final UsbEndpoint endpoint;

        private volatile boolean isStopped = false;

        public USBThreadDataReceiver(UsbEndpoint endpoint) {
            this.maxPacketSize = endpoint.getMaxPacketSize();
            this.endpoint = endpoint;
        }

        @Override
        public void run() {
            try {
                if (connection != null && endpoint != null) {
                    while (!isStopped) {
                        final byte[] buffer = new byte[maxPacketSize];
                        final int status = connection.bulkTransfer(endpoint, buffer, maxPacketSize, 100);

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
