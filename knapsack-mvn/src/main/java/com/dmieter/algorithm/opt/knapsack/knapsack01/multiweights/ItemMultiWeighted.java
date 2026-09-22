
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.Item;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author dmieter
 */
public class ItemMultiWeighted extends Item{
    
    protected List<Integer> weights;
    
    public ItemMultiWeighted(int id, int weight, double value) {
        super(id, weight, value);
        weights = Collections.singletonList(weight);
    }
    
    public ItemMultiWeighted(int id, List<Integer> weights, double value) {
        super(id, weights.get(0), value);
        this.weights = weights;
    }

    public int getWeight(int num){
        return weights.get(num);
    }
    
    /**
     * @return the weights
     */
    public List<Integer> getWeights() {
        return weights;
    }

    /**
     * @param weights the weights to set
     */
    public void setWeights(List<Integer> weights) {
        this.weights = weights;
    }
    
}
