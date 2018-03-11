package com.piccollage.util;

import android.content.Intent;
import android.os.Parcel;

public final class EnumUtils {
    public static class Serializer<T extends Enum<T>> extends Deserializer<T> {
        private final T mVictim;
        private final String mName;
        @SuppressWarnings("unchecked") 
        public Serializer(T victim) {
            super((Class<T>) victim.getClass());
            this.mVictim = victim;
            this.mName = victim.name();
        }
        public void to(Intent intent) {
            intent.putExtra(mName, mVictim.ordinal());
        }
        public void to(Parcel parcel) {
            parcel.writeInt(mVictim.ordinal());
        }
    }
    public static class Deserializer<T extends Enum<T>> {
        protected Class<T> mVictimClass;
        protected String mName;
        public Deserializer(Class<T> victimClass) {
            this.mVictimClass = victimClass;
            this.mName = victimClass.getName();
        }
        public T from(Intent intent) {
            if (!intent.hasExtra(mName)) throw new IllegalStateException();
            return mVictimClass.getEnumConstants()[intent.getIntExtra(mName, -1)];
        }
        public T from(Parcel parcel) {
            return mVictimClass.getEnumConstants()[parcel.readInt()];
        }
    }
    public static <T extends Enum<T>> Deserializer<T> deserialize(Class<T> victimClass) {
        return new Deserializer<T>(victimClass);
    }
    public static <T extends Enum<T>> Serializer<T> serialize(T victim) {
        return new Serializer<T>(victim);
    }
}