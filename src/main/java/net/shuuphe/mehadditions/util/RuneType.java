package net.shuuphe.mehadditions.util;

public enum RuneType {
    FIRE, FROST, LIGHTNING;

    public static RuneType fromOrdinal(int ord) {
        RuneType[] vals = values();
        return vals[Math.abs(ord) % vals.length];
    }
}