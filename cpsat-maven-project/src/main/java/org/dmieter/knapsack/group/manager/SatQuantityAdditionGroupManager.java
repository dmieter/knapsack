package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import java.util.function.Function;

/**
 *
 * @author dmieter
 */
public abstract class SatQuantityAdditionGroupManager extends QuantityAdditionWeightGroupManager implements SatConvertableGroupManager {
    
    public SatQuantityAdditionGroupManager(String name, Function<Integer, Double> valueBoostFunction, Function<Integer, Double> weightReductionFunction) {
        super(name, valueBoostFunction, weightReductionFunction);
    }
}
