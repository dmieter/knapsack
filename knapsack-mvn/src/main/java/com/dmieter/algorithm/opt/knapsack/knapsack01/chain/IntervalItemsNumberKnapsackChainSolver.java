package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackSolver;

/**
 *
 * @author emelyanov
 */

/* solves chains of FixedItemsNumberKnapsack problems */
public class IntervalItemsNumberKnapsackChainSolver extends IntervalItemsNumberKnapsackSolver implements ChainSolver {

    protected void initDPTable(DPEntity[][][] dp, IntervalItemsNumberKnapsackProblemChain problem) {
        
        /* prepare init values */
        Double[] initValues = null;
        
        if (problem.getInitValues() != null && problem.getInitValues().length == (problem.getMaxWeight() + 1)) {
            initValues = problem.getInitValues();
        }

        if (initValues == null) {
            initValues = new Double[problem.getMaxWeight() + 1];
            for (int w = 0; w <= problem.getMaxWeight(); w++) {
                initValues[w] = 0d;
            }
        }

        /* init DP with init values */
        for (int w = 0; w <= problem.getMaxWeight(); w++) {
            for (int j = 0; j <= problem.getItems().size(); j++) {
                /* null means we can't use this w, so related DPEntitty will be null */
                if(initValues[w] != null){
                    dp[w][j][0] = new DPEntity(initValues[w]);
                }
            }
        }
    }


    @Override
    public boolean performBackwardInduction(ProblemChain problem, Integer tightWeight) {
        IntervalItemsNumberKnapsackProblemChain chainProblem = (IntervalItemsNumberKnapsackProblemChain) problem;
        return backwardInduction(chainProblem.getDp(), chainProblem, tightWeight);
    }

    @Override
    public boolean performForwardPropagation(ProblemChain problem) {
        IntervalItemsNumberKnapsackProblemChain chainProblem = (IntervalItemsNumberKnapsackProblemChain) problem;
        
        /* init dp table */
        int n = chainProblem.getItems().size();
        DPEntity dp[][][] = new DPEntity[chainProblem.getMaxWeight() + 1][n + 1][chainProblem.getItemsRequiredNumber() + 1];
        initDPTable(dp, chainProblem);
        
        /* forward propagation */
        boolean result = forwardInduction(dp, chainProblem, n);
        
        /* saving intermediate calculations */
        chainProblem.setDp(dp);
        
        /* prepare output interchain values */
        Double[] outValues = new Double[chainProblem.getMaxWeight() + 1];
        for (int w = 0; w <= chainProblem.getMaxWeight(); w++) {
            
            Integer requiredItemsNumber = getBestItemsNumber(dp, chainProblem, n, w);  // for each weight limit we may have best value provided by different number of elements
            
            if(requiredItemsNumber != null){
                outValues[w] = dp[w][n][requiredItemsNumber].value;
            }else{
                outValues[w] = null;    // no solution for this w, so can't use this w in the next problem
            }
        }
        chainProblem.setOutputValues(outValues);
        
        return result;
    }
}
