
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

/**
 *
 * @author emelyanov
 */
public interface ChainSolver {
    public boolean performForwardPropagation(ProblemChain problem);
    public boolean performBackwardInduction(ProblemChain problem, Integer tightWeight);
}
