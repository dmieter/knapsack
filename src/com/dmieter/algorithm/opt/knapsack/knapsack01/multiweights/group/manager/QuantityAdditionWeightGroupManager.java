package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager;

import java.util.function.Function;

public class QuantityAdditionWeightGroupManager extends QuantityMultiplierWeightGroupManager {

    public QuantityAdditionWeightGroupManager(String name, Function<Integer, Double> valueBoostFunction, Function<Integer, Double> weightReductionFunction) {
        super(name, valueBoostFunction, weightReductionFunction);
    }

    protected Double getImprovedValue(Integer amount, Double value) {
        return value + getValueBoostFactor(amount);
    }
}
