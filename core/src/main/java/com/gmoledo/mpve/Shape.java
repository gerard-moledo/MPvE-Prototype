package com.gmoledo.mpve;

import java.util.EnumMap;

final public class Shape {
    enum Type {
        single, pair, line_3, cluster_3, curve_3, line_4, J_4, U_4, hammer_4, cluster_4, Z_4, hazard_4;

        final private int value;
        final static private Type[] values = Type.values();

        Type() {
            value = this.ordinal();
        }

        public Type toggle_value(int direction) {
            return values[(value + direction + values.length) % values.length];
        }
    }

    static final int[] SINGLE =     new int[]{ 0, 0 };
    static final int[] PAIR =       new int[]{ 0, 0, 0, 1 };
    static final int[] LINE_3 =     new int[]{ 0,-1, 0, 0, 0, 1 };
    static final int[] CLUSTER_3 =  new int[]{ 0, 0, 1,-1, 1, 0 };
    static final int[] CURVE_3 =    new int[]{-1, 0, 0, 0, 1,-1 };
    static final int[] LINE_4 =     new int[]{ 0,-2, 0,-1, 0, 0, 0, 1 };
    static final int[] J_4 =        new int[]{ 0,-2, 0,-1, 0, 0,-1, 1 };
    static final int[] U_4 =        new int[]{-1, 0, 0, 0, 1,-1, 1,-2 };
    static final int[] hammer_4 =   new int[]{ 0,-1, 0, 0, 0, 1,-1, 1 };
    static final int[] cluster_4 =  new int[]{ 0, 0,-1, 1, 0, 1, 1, 0 };
    static final int[] Z_4 =        new int[]{-1, 0, 0, 0, 0, 1, 1, 1 };
    static final int[] hazard_4  =  new int[]{ 0, 0,-1, 1, 0,-1, 1, 0 };
    static EnumMap<Type, int[]> SHAPE_MAP;

    static public void Initialize() {
        // Must instantiate map manually
        SHAPE_MAP = new EnumMap<>(Type.class);
        SHAPE_MAP.put(Type.single, SINGLE);
        SHAPE_MAP.put(Type.pair, PAIR);
        SHAPE_MAP.put(Type.line_3, LINE_3);
        SHAPE_MAP.put(Type.cluster_3, CLUSTER_3);
        SHAPE_MAP.put(Type.curve_3, CURVE_3);
        SHAPE_MAP.put(Type.line_4, LINE_4);
        SHAPE_MAP.put(Type.J_4, J_4);
        SHAPE_MAP.put(Type.U_4, U_4);
        SHAPE_MAP.put(Type.hammer_4, hammer_4);
        SHAPE_MAP.put(Type.cluster_4, cluster_4);
        SHAPE_MAP.put(Type.Z_4, Z_4);
        SHAPE_MAP.put(Type.hazard_4, hazard_4);
    }
}
