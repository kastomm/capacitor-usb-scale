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

    private String incomingWeightDataCallbackId;

    @Override
    public void load() {
        super.load();
        implementation = new USBScale(this.getActivity(), (data, status, weight) -> {
            if(weight == lastWeight.get() && Objects.equals(status, lastStatus.get()))
                return;

            lastStatus.set(status);
            lastWeight.set(weight);

            try {
                JSObject ret = new JSObject();
                ret.put("data", data);
                ret.put("weight", weight);
                ret.put("status", status);

                PluginCall call = getIncomingWeightDataCallback();
                if (call != null) {
                    call.resolve(ret);
                } else {
                    bridge.triggerWindowJSEvent("usb_scale_read", ret.toString(3));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, (UsbDevice device) -> {
            try {
                int vid = device.getVendorId();
                int pid = device.getProductId();

                JSONObject product = new JSONObject();
                product.put("manufacturer", device.getManufacturerName());
                product.put("name", device.getProductName());

                JSONObject obj = new JSONObject();
                obj.put("id", device.getDeviceName());
                obj.put("vid", vid);
                obj.put("pid", pid);
                obj.put("serial", device.getSerialNumber());
                obj.put("product", product);

                JSONObject json = new JSONObject();
                json.put("device", obj);
                this.getBridge().triggerWindowJSEvent("usb_scale_disconnected", json.toString(3));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, (UsbDevice device) -> {
            try {
                int vid = device.getVendorId();
                int pid = device.getProductId();

                JSONObject product = new JSONObject();
                product.put("manufacturer", device.getManufacturerName());
                product.put("name", device.getProductName());

                JSONObject obj = new JSONObject();
                obj.put("id", device.getDeviceName());
                obj.put("vid", vid);
                obj.put("pid", pid);
                obj.put("serial", device.getSerialNumber());
                obj.put("product", product);

                JSONObject json = new JSONObject();
                json.put("device", obj);
                this.getBridge().triggerWindowJSEvent("usb_scale_connected", json.toString(3));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @PluginMethod
    public void enumerateDevices(PluginCall call) throws JSONException {
        JSObject ret = new JSObject();
        ret.put("devices", implementation.enumerateDevices());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) throws JSONException {
        String device = call.getString("device");

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

                    JSObject ret = new JSObject();
                    ret.put("status", status);
                    call.resolve(ret);
                });
            } catch (IOException e) {
                call.reject(e.getMessage(), e);
            }
        });
    }

    @PluginMethod
    public void open(PluginCall call) throws JSONException {
        String device = call.getString("device");

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

    @Nullable
    protected PluginCall getIncomingWeightDataCallback() {
        if(incomingWeightDataCallbackId == null) {
            return null;
        }

        return bridge.getSavedCall(incomingWeightDataCallbackId);
    }

    @PluginMethod(returnType = PluginMethod.RETURN_CALLBACK)
    public void setIncomingWeightDataCallback(PluginCall call) {
        if(incomingWeightDataCallbackId != null) {
            bridge.releaseCall(incomingWeightDataCallbackId);
            incomingWeightDataCallbackId = null;
        }

        call.setKeepAlive(true);
        incomingWeightDataCallbackId = call.getCallbackId();
        bridge.saveCall(call);
    }

    @PluginMethod()
    public void clearIncomingWeightDataCallback(PluginCall call) {
        if(incomingWeightDataCallbackId == null) {
            return;
        }

        bridge.releaseCall(incomingWeightDataCallbackId);
        incomingWeightDataCallbackId = null;

        call.resolve();
    }
}
