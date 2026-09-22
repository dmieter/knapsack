
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

/**
 *
 * @author dmiet
 */
public class IntervalItemsNumberKnapsackProblem extends FixedItemsNumberKnapsackProblem {
    
    private int minItemsNumber;

    /**
     * @return the minItemsNumber
     */
    public int getMinItemsNumber() {
        return minItemsNumber;
    }

    /**
     * @param minItemsNumber the minItemsNumber to set
     */
    public void setMinItemsNumber(int minItemsNumber) {
        this.minItemsNumber = minItemsNumber;
    }
    
    /**
     * @return the minItemsNumber
     */
    public int getMaxItemsNumber() {
        return itemsRequiredNumber;
    }

    /**
     * @param minItemsNumber the minItemsNumber to set
     */
    public void setMaxItemsNumber(int minItemsNumber) {
        this.itemsRequiredNumber = minItemsNumber;
    }
}
