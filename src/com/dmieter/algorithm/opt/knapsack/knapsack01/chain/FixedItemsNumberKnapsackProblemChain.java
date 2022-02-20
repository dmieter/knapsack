
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;

/**
 *
 * @author dmieter
 */
public class FixedItemsNumberKnapsackProblemChain extends FixedItemsNumberKnapsackProblem implements ProblemChain{
    
    // id to match chain with outer problems
    private long id;
    
    /* storing dynamic problem table for chain operations */
    private DPEntity[][][] dp;
    
    /* values to use as init for the problem, possibly received from the previous problem */
    private Double[] initValues;
    
    /* values to use as init for the problem, possibly received from the previous problem */
    private Double[] outputValues;
    

    /**
     * @return the initValues
     * 
     */
    public Double[] getInitValues() {
        return initValues;
    }

    /**
     * @return the dp
     */
    public DPEntity[][][] getDp() {
        return dp;
    }

    /**
     * @param dp the dp to set
     */
    public void setDp(DPEntity[][][] dp) {
        this.dp = dp;
    }

    /**
     * @return the outputValues
     */
    public Double[] getOutputValues() {
        return outputValues;
    }

    /**
     * @param outputValues the outputValues to set
     */
    public void setOutputValues(Double[] outputValues) {
        this.outputValues = outputValues;
    }

    /**
     * @param initValues the initValues to set
     */
    public void setInitValues(Double[] initValues) {
        this.initValues = initValues;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
       
}
