package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager;

import java.util.function.Function;

public class QuantityAdditionWeightGroupManager extends QuantityMultiplierWeightGroupManager {

    public QuantityAdditionWeightGroupManager(String name, Function<Integer, Double> valueBoostFunction, Function<Integer, Double> weightReductionFunction) {
        super(name, valueBoostFunction, weightReductionFunction);
    }

    @Override
    protected Double getImprovedValue(Integer amount, Double value) {
        return value + getValueBoostFactor(amount);
    }
    
    @Override
    protected Double getValueBoostFactor(Integer amount) {
        if(valueBoostFunction == null) { 
            return 0d;      // as this is addition, not multiplication
        }
        else {
            return valueBoostFunction.apply(amount);
        }
    }
    
    
}
