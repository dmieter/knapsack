
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;

/**
 *
 * @author dmieter
 */
public class IntervalItemsNumberKnapsackProblemChain extends IntervalItemsNumberKnapsackProblem implements ProblemChain{
    
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
    @Override
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
    @Override
    public Double[] getOutputValues() {
        return outputValues;
    }

    /**
     * @param outputValues the outputValues to set
     */
    @Override
    public void setOutputValues(Double[] outputValues) {
        this.outputValues = outputValues;
    }

    /**
     * @param initValues the initValues to set
     */
    @Override
    public void setInitValues(Double[] initValues) {
        this.initValues = initValues;
    }

    /**
     * @return the id
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }
       
}
