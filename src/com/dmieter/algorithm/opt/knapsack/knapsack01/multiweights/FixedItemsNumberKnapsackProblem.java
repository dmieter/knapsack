
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;

/**
 *
 * @author dmieter
 */
public class FixedItemsNumberKnapsackProblem extends KnapsackProblem {
    protected int itemsRequiredNumber;

    /**
     * @return the itemsRequiredNumber
     */
    public int getItemsRequiredNumber() {
        return itemsRequiredNumber;
    }

    /**
     * @param itemsRequiredNumber the itemsRequiredNumber to set
     */
    public void setItemsRequiredNumber(int itemsRequiredNumber) {
        this.itemsRequiredNumber = itemsRequiredNumber;
    }
    
}
