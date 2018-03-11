package com.piccollage.util.protocol;

public interface IPhoto {
    int getWidth();
    int getHeight();
    void setWidth(float width);
    void setHeight(float height);

    /**
     * @return width over height.
     */
    double getAspectRatio();

    /**
     * The source URL.
     */
    String sourceUrl();

    String thumbnailUrl();

    boolean isIntrinsicallySlotable();
}
