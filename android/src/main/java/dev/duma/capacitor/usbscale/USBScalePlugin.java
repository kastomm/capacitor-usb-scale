package dev.duma.capacitor.usbscale;

import android.hardware.usb.UsbDevice;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import dev.duma.android.usbscale.DeviceInfo;
import dev.duma.android.usbscale.enums.StatusEnum;
import dev.duma.android.usbscale.exceptions.CantOpenDeviceException;
import dev.duma.android.usbscale.exceptions.DeviceNotFoundException;
import dev.duma.android.usbscale.exceptions.OpenedDeviceEndpointIsNotInputEndpoint;
import dev.duma.capacitor.usbscale.exceptions.NoConnectedUSBScaleException;

@CapacitorPlugin(name = "USBScale")
public class USBScalePlugin extends Plugin {
    private USBScale implementation;

    final AtomicReference<Double> lastWeight = new AtomicReference<>((double) 0);
    final AtomicReference<StatusEnum> lastStatus = new AtomicReference<>(null);

    final IUSBScaleCallback callback = new IUSBScaleCallback() {
        @Override
        public void OnRead(String data, StatusEnum status, double weight) {
            if(weight == lastWeight.get() && Objects.equals(status, lastStatus.get()))
                return;

            lastStatus.set(status);
            lastWeight.set(weight);

            JSObject ret = new JSObject();
            ret.put("data", data);
            ret.put("weight", weight);
            ret.put("status", status.getName());

            notifyListeners("onRead", ret);
        }

        @Override
        public void OnScaleConnected(UsbDevice device) {
            JSObject product = new JSObject();
            product.put("manufacturer", device.getManufacturerName());
            product.put("name", device.getProductName());

            JSObject dev = new JSObject();
            dev.put("id", device.getDeviceName());
            dev.put("vid", device.getVendorId());
            dev.put("pid", device.getProductId());
            dev.put("serial", device.getSerialNumber());
            dev.put("product", product);

            JSObject response = new JSObject();
            response.put("device", dev);

            notifyListeners("onScaleConnected", response);
        }

        @Override
        public void OnScaleDisconnected(UsbDevice device) {
            JSObject product = new JSObject();
            product.put("manufacturer", device.getManufacturerName());
            product.put("name", device.getProductName());

            JSObject dev = new JSObject();
            dev.put("id", device.getDeviceName());
            dev.put("vid", device.getVendorId());
            dev.put("pid", device.getProductId());
            dev.put("serial", device.getSerialNumber());
            dev.put("product", product);

            JSObject response = new JSObject();
            response.put("device", dev);

            notifyListeners("onScaleDisconnected", response);

            implementation.close();
        }
    };

    @Override
    public void load() {
        implementation = new USBScale(this.getActivity(), callback);
        implementation.register();
    }

    @PluginMethod
    public void enumerateDevices(PluginCall call) throws JSONException {
        JSONArray devicesArray = new JSONArray();

        for (DeviceInfo device : implementation.enumerateDevices()) {
            JSONObject product = new JSONObject();
            product.put("manufacturer", device.getManufacturer());
            product.put("name", device.getName());

            JSONObject obj = new JSONObject();
            obj.put("id", device.getId());
            obj.put("vid", device.getVid());
            obj.put("pid", device.getPid());
            obj.put("serial", device.getSerial());
            obj.put("product", product);

            devicesArray.put(obj);
        }

        JSObject ret = new JSObject();
        ret.put("devices", devicesArray);

        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) {
        try {
            String device = getDeviceOrDefault(call.getString("device_id"));

            this.execute(() -> {
                try {
                    implementation.requestPermission(device, status -> {
                        if(!status){
                            call.reject("Permission denied!");
                            return;
                        }

                        call.resolve();
                    });
                } catch (DeviceNotFoundException e) {
                    call.reject(e.getMessage());
                }
            });
        } catch (NoConnectedUSBScaleException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void open(PluginCall call) {
        try {
            String device = getDeviceOrDefault(call.getString("device_id"));

            lastWeight.set((double) 0);
            lastStatus.set(null);

            this.execute(() -> {
                try {
                    implementation.open(device);
                    call.resolve();
                } catch (DeviceNotFoundException | CantOpenDeviceException | OpenedDeviceEndpointIsNotInputEndpoint e) {
                    call.reject(e.getMessage());
                }
            });
        } catch (NoConnectedUSBScaleException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod()
    public void close(PluginCall call) {
        implementation.close();
        call.resolve();
    }

    @NonNull
    private String getDeviceOrDefault(String device) throws NoConnectedUSBScaleException {
        if(device != null) {
            return device;
        }

        ArrayList<DeviceInfo> enumerateDevices = implementation.enumerateDevices();
        if(enumerateDevices.size() == 0){
            throw new NoConnectedUSBScaleException();
        }

        return enumerateDevices.get(0).getId();
    }
}
