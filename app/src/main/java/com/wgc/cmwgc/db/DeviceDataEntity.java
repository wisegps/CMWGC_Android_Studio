package com.wgc.cmwgc.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by Administrator on 2016/12/5.
 */
@Entity
public class DeviceDataEntity {

    @Id
    private Long id;
    @Property(nameInDb = "LAT")
    private double lat;
    @Property(nameInDb = "LON")
    private double lon;
    @Property(nameInDb = "GPSFLAG")
    private int  gpsFlag;
    @Property(nameInDb = "SPEED")
    private int  speed;
    @Property(nameInDb = "DIRECT")
    private float direct;
    @Property(nameInDb = "SIGNAL")
    private int signal;
    @Property(nameInDb = "CREATEAT")
    private String  createdAt;
    @Property(nameInDb = "GPSTIME")
    private String  gpsTime;
    @Property(nameInDb = "RCVTIME")
    private String  rcvTime;
    @Property(nameInDb = "MILEAGE")
    private double mileage;
    @Property(nameInDb = "FUEL")
    private double fuel;
    @Property(nameInDb = "STATUS")
    private String  status;

    @Generated(hash = 1928812570)
    public DeviceDataEntity(Long id, double lat, double lon, int gpsFlag,
            int speed, float direct, int signal, String createdAt, String gpsTime,
            String rcvTime, double mileage, double fuel, String status) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.gpsFlag = gpsFlag;
        this.speed = speed;
        this.direct = direct;
        this.signal = signal;
        this.createdAt = createdAt;
        this.gpsTime = gpsTime;
        this.rcvTime = rcvTime;
        this.mileage = mileage;
        this.fuel = fuel;
        this.status = status;
    }

    @Generated(hash = 1513375530)
    public DeviceDataEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getGpsFlag() {
        return gpsFlag;
    }

    public void setGpsFlag(int gpsFlag) {
        this.gpsFlag = gpsFlag;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public float getDirect() {
        return direct;
    }

    public void setDirect(float direct) {
        this.direct = direct;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getRcvTime() {
        return rcvTime;
    }

    public void setRcvTime(String rcvTime) {
        this.rcvTime = rcvTime;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
