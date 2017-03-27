package com.wgc.cmwgc.model;

import java.util.List;

/**
 * 功能： descriable
 * 作者： Administrator
 * 日期： 2017/3/20 14:27
 * 邮箱： descriable
 */
public class Geofences {


    /**
     * count : 1
     * geofences : [{"prop":"1","points":[{"lon":"113.123456","lat":"22.123456"}],"startTime":"2016-12-01","endTime":"2016-12-31","maxSpeed":"80","overSpeedTime":"10"}]
     */

    private String count;
    /**
     * prop : 1
     * points : [{"lon":"113.123456","lat":"22.123456"}]
     * startTime : 2016-12-01
     * endTime : 2016-12-31
     * maxSpeed : 80
     * overSpeedTime : 10
     */

    private List<GeofencesBean> geofences;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<GeofencesBean> getGeofences() {
        return geofences;
    }

    public void setGeofences(List<GeofencesBean> geofences) {
        this.geofences = geofences;
    }

    public static class GeofencesBean {
        private String prop;
        private String startTime;
        private String endTime;
        private String maxSpeed;
        private String overSpeedTime;
        /**
         * lon : 113.123456
         * lat : 22.123456
         */

        private List<PointsBean> points;

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getMaxSpeed() {
            return maxSpeed;
        }

        public void setMaxSpeed(String maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        public String getOverSpeedTime() {
            return overSpeedTime;
        }

        public void setOverSpeedTime(String overSpeedTime) {
            this.overSpeedTime = overSpeedTime;
        }

        public List<PointsBean> getPoints() {
            return points;
        }

        public void setPoints(List<PointsBean> points) {
            this.points = points;
        }

        public static class PointsBean {
            private double lon;
            private double lat;

            public double getLon() {
                return lon;
            }

            public void setLon(double lon) {
                this.lon = lon;
            }

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }
        }
    }
}
