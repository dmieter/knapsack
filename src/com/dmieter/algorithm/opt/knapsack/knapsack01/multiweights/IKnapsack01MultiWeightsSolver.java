
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

/**
 *
 * @author dmieter
 */
public interface IKnapsack01MultiWeightsSolver {
    public boolean solve(MultiWeightsKnapsackProblem problem);
    public boolean solve(FixedItemsNumberKnapsackProblem problem);
    public void flush();
}
