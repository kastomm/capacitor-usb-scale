package dev.duma.android.usbscale.exceptions;

public class OpenedDeviceEndpointIsNotInputEndpoint extends Exception {
    public OpenedDeviceEndpointIsNotInputEndpoint() {
        super("Endpoint is not an input endpoint");
    }
}
