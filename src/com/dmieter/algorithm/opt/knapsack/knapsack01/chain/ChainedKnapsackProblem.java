
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author emelyanov
 */
public class ChainedKnapsackProblem {
    private Integer maxWeight;
    private List<ProblemChain> problemChains = new ArrayList<>();
    private Double[] initValues;
    
    protected int totalWeight;
    protected double totalValue;


    /**
     * @return the maxWeight
     */
    public Integer getMaxWeight() {
        return maxWeight;
    }

    /**
     * @param maxWeight the maxWeight to set
     */
    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * @return the problemChains
     */
    public List<ProblemChain> getProblemChains() {
        return problemChains;
    }

    /**
     * @param problemChains the problemChains to set
     */
    public void setProblemChains(List<ProblemChain> problemChains) {
        this.problemChains = problemChains;
    }

    /**
     * @return the initValues
     */
    public Double[] getInitValues() {
        return initValues;
    }

    /**
     * @param initValues the initValues to set
     */
    public void setInitValues(Double[] initValues) {
        this.initValues = initValues;
    }
    
    public void addChain(ProblemChain problem){
        problemChains.add(problem);
    }

    /**
     * @return the totalWeight
     */
    public int getTotalWeight() {
        return totalWeight;
    }

    /**
     * @return the totalValue
     */
    public double getTotalValue() {
        return totalValue;
    }
}
