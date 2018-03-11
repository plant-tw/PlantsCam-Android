package com.piccollage.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prada on 06/10/2017.
 */

public class SizeF implements Parcelable {
    @SerializedName("width")
    @Expose
    private float width;
    @SerializedName("height")
    @Expose
    private float height;

    public SizeF(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isSquare() {
        return Float.compare(width, height) == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.width);
        dest.writeFloat(this.height);
    }

    protected SizeF(Parcel in) {
        this.width = in.readFloat();
        this.height = in.readFloat();
    }

    public static final Creator<SizeF> CREATOR = new Creator<SizeF>() {
        @Override
        public SizeF createFromParcel(Parcel source) {
            return new SizeF(source);
        }

        @Override
        public SizeF[] newArray(int size) {
            return new SizeF[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SizeF size = (SizeF) o;

        if (width != size.width) return false;
        return height == size.height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        int result = (width != +0.0f ? Float.floatToIntBits(width) : 0);
        result = 31 * result + (height != +0.0f ? Float.floatToIntBits(height) : 0);
        return result;
    }
}
