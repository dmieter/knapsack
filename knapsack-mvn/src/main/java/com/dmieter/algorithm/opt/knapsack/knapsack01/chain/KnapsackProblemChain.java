
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

/**
 *
 * @author dmiet
 */
public class KnapsackProblemChain extends KnapsackProblem implements ProblemChain {

    // id to match chain with outer problems
    private long id;
    
    /* storing dynamic problem table for chain operations */
    private DPEntity dp[][];
    
    /* values to use as init for the problem, possibly received from the previous problem */
    private Double[] initValues;
    
    /* values to use as init for the problem, possibly received from the previous problem */
    private Double[] outputValues;
    

    @Override
    public Double[] getInitValues() {
        return initValues;
    }
    

    @Override
    public Double[] getOutputValues() {
        return outputValues;
    }


    @Override
    public void setOutputValues(Double[] outputValues) {
        this.outputValues = outputValues;
    }


    @Override
    public void setInitValues(Double[] initValues) {
        this.initValues = initValues;
    }


    public DPEntity[][] getDp() {
        return dp;
    }


    public void setDp(DPEntity[][] dp) {
        this.dp = dp;
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
