
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;

/**
 *
 * @author emelyanov
 */
public class FixedItemsMultiplicativeProblem extends FixedItemsNumberKnapsackProblem{
    
    @Override
    public void calculateStats(){
        totalValue = 1;
        totalWeight = 0;
        for(Item item : selectedItems){
            totalValue *= item.getValue();
            totalWeight += item.getWeight();
        }
        knapsackSize = selectedItems.size();
    }
}
