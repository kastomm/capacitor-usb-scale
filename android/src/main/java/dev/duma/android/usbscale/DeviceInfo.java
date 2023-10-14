package dev.duma.android.usbscale;

public class DeviceInfo {
    private String id;
    private int vid;
    private int pid;
    private String serial;
    private String manufacturer;
    private String name;

    public DeviceInfo(String id, int vid, int pid, String serial, String manufacturer, String name) {
        this.id = id;
        this.vid = vid;
        this.pid = pid;
        this.serial = serial;
        this.manufacturer = manufacturer;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
