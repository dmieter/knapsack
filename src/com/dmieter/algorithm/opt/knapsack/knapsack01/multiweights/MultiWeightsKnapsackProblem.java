
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;
import java.util.List;

/**
 *
 * @author dmieter
 */
public class MultiWeightsKnapsackProblem extends KnapsackProblem {
    protected List<Integer> maxWeights; 

    public int getMaxWeight(int num){
        return maxWeights.get(num);
    }
    
    /**
     * @return the maxWeights
     */
    public List<Integer> getMaxWeights() {
        return maxWeights;
    }

    /**
     * @param maxWeights the maxWeights to set
     */
    public void setMaxWeights(List<Integer> maxWeights) {
        this.maxWeights = maxWeights;
    }
}
