package com.dmieter.algorithm.opt.knapsack.data;

/**
 *
 * @author emelyanov
 */
public class DPEntity {

    public double value;
    public int relatedObjectID = -1;

    public DPEntity(double value) {
        this.value = value;
    }

    public DPEntity(double value, int objectID) {
        this.value = value;
        this.relatedObjectID = objectID;
    }

    public DPEntity(DPEntity dpEntity) {
        this.value = dpEntity.value;
        this.relatedObjectID = dpEntity.relatedObjectID;
    }
}
