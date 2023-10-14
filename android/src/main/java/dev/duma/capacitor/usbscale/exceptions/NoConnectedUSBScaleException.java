package dev.duma.capacitor.usbscale.exceptions;

public class NoConnectedUSBScaleException extends Exception {
    public NoConnectedUSBScaleException() {
        super("No connected USB Scale found!");
    }
}
