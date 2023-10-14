package dev.duma.capacitor.usbscale;

import android.hardware.usb.UsbDevice;

import androidx.annotation.Nullable;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CapacitorPlugin(name = "USBScale")
public class USBScalePlugin extends Plugin {
    private USBScale implementation;

    AtomicReference<Double> lastWeight = new AtomicReference<>((double) 0);
    AtomicReference<String> lastStatus = new AtomicReference<>("");
    IUSBScaleCallback callback = new IUSBScaleCallback() {
        @Override
        public void OnRead(String data, String status, double weight) {
            if(weight == lastWeight.get() && Objects.equals(status, lastStatus.get()))
                return;

            lastStatus.set(status);
            lastWeight.set(weight);

            JSObject ret = new JSObject();
            ret.put("data", data);
            ret.put("weight", weight);
            ret.put("status", status);

            notifyListeners("onRead", ret);
        }

        @Override
        public void OnScaleConnected(UsbDevice device) {
            int vid = device.getVendorId();
            int pid = device.getProductId();

            JSObject product = new JSObject();
            product.put("manufacturer", device.getManufacturerName());
            product.put("name", device.getProductName());

            JSObject dev = new JSObject();
            dev.put("id", device.getDeviceName());
            dev.put("vid", vid);
            dev.put("pid", pid);
            dev.put("serial", device.getSerialNumber());
            dev.put("product", product);

            JSObject response = new JSObject();
            response.put("device", dev);
            notifyListeners("onScaleConnected", response);
        }

        @Override
        public void OnScaleDisconnected(UsbDevice device) {
            int vid = device.getVendorId();
            int pid = device.getProductId();

            JSObject product = new JSObject();
            product.put("manufacturer", device.getManufacturerName());
            product.put("name", device.getProductName());

            JSObject dev = new JSObject();
            dev.put("id", device.getDeviceName());
            dev.put("vid", vid);
            dev.put("pid", pid);
            dev.put("serial", device.getSerialNumber());
            dev.put("product", product);

            JSObject response = new JSObject();
            response.put("device", dev);
            notifyListeners("onScaleDisconnected", response);

            implementation.stop();
        }
    };

    @Override
    public void load() {
        super.load();
        implementation = new USBScale(this.getActivity(), callback);
    }

//    private boolean hasPausedEver = false;
//
//    @Override
//    protected void handleOnPause() {
//        hasPausedEver = true;
//
//    }
//
//    @Override
//    protected void handleOnResume() {
//        if (!hasPausedEver) {
//            return;
//        }
//
//    }

    @PluginMethod
    public void enumerateDevices(PluginCall call) throws JSONException {
        JSObject ret = new JSObject();
        ret.put("devices", implementation.enumerateDevices());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) throws JSONException {
        String device = call.getString("device_id");

        if(device == null) {
            JSONArray enumerateDevices = implementation.enumerateDevices();
            if(enumerateDevices.length() == 0){
                call.reject("No USB Scale found!");
                return;
            }
            JSONObject d = (JSONObject) enumerateDevices.get(0);
            device = d.getString("id");
        }

        String finalDevice = device;
        this.execute(() -> {
            try {
                implementation.requestPermission(finalDevice, status -> {
                    if(!status){
                        call.reject("Permission denied!");
                        return;
                    }

                    call.resolve();
                });
            } catch (IOException e) {
                call.reject(e.getMessage(), e);
            }
        });
    }

    @PluginMethod
    public void open(PluginCall call) throws JSONException {
        String device = call.getString("device_id");

        if(device == null) {
            JSONArray enumerateDevices = implementation.enumerateDevices();
            if(enumerateDevices.length() == 0){
                call.reject("No USB Scale found!");
                return;
            }
            JSONObject d = (JSONObject) enumerateDevices.get(0);
            device = d.getString("id");
        }

        String finalDevice = device;
        lastWeight = new AtomicReference<>((double) 0);
        lastStatus = new AtomicReference<>("");
        this.execute(() -> {
            try {
                implementation.open(finalDevice);
                call.resolve();
            } catch (IOException e) {
                call.reject(e.getMessage(), e);
            }
        });
    }

    @PluginMethod()
    public void stop(PluginCall call) throws JSONException {
        implementation.stop();
        call.resolve();
    }
}
