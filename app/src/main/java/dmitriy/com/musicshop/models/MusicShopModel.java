package dmitriy.com.musicshop.models;

import java.io.Serializable;

public class MusicShopModel implements Serializable {
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public Location getLocation() {
        return location;
    }

    public long getId() {
        return id;
    }

    public String getWebsite() {
        return website;
    }

    private long id;
    private String name;
    private String address;
    private String phone;
    private String website;
    private Location location;

    public class Location implements Serializable {
        public long getLatitude() {
            return latitude;
        }

        public long getLongitude() {
            return longitude;
        }

        long latitude;
        long longitude;
    }
}
