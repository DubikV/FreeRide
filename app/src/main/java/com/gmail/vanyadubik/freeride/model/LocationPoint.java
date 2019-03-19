package com.gmail.vanyadubik.freeride.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class LocationPoint implements Parcelable {
  private String id;
  private Location location;
  private String title;
  private String address;

  public LocationPoint(String id, String title, Location location) {
    this.id = id;
    this.location = location;
    this.title = title;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeParcelable(this.location, flags);
    dest.writeString(this.title);
    dest.writeString(this.address);
  }

  protected LocationPoint(Parcel in) {
    this.id = in.readString();
    this.location = in.readParcelable(Location.class.getClassLoader());
    this.title = in.readString();
    this.address = in.readString();
  }

  public static final Creator<LocationPoint> CREATOR = new Creator<LocationPoint>() {
    @Override
    public LocationPoint createFromParcel(Parcel source) {
      return new LocationPoint(source);
    }

    @Override
    public LocationPoint[] newArray(int size) {
      return new LocationPoint[size];
    }
  };

  @Override
  public String toString() {
    return "LekuPoi{" + "id='" + id + '\'' + ", location=" + location + ", title='" + title + '\'' + ", address='" + address + '\'' + '}';
  }
}
