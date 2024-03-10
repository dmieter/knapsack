package com.dmieter.algorithm.opt.knapsack.data;

public class DPGroupEntity extends DPEntity {

    public int usedAmount;
    public int usedWeight;

    public DPGroupEntity(double value, int relatedGroupItemNum, int usedAmount, int usedWeight) {
        super(value, relatedGroupItemNum);
        this.usedAmount = usedAmount;
        this.usedWeight = usedWeight;  
    }

    public DPGroupEntity(DPGroupEntity dp) {
        super(dp.value, dp.relatedObjectID);
        this.usedAmount = dp.usedAmount;
        this.usedWeight = dp.usedWeight;
    }

    public int getRelatedGroupItemNum() {
        return relatedObjectID;
    }

}
