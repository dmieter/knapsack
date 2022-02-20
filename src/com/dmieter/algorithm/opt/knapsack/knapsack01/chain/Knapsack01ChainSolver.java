package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.SeparatedKnapsack01Solver;

/**
 *
 * @author emelyanov
 */

/* solves chains of FixedItemsNumberKnapsack problems */
public class Knapsack01ChainSolver extends SeparatedKnapsack01Solver implements ChainSolver {

    protected void initDPTable(DPEntity[][] dp, KnapsackProblemChain problem) {
        
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
            /* null means we can't use this w, so related DPEntitty will be null */
            if (initValues[w] != null) {
                dp[w][0] = new DPEntity(initValues[w]);
            }
        }
        
        // 0 items is a possible solution, we want to propagate it to output values
        if(dp[0][0] != null){
            for(int i = 1; i <= problem.getItems().size(); i++){
                dp[0][i] = new DPEntity(0);
            }
        }
        
        // starting table
        //KnapsackSolverHelp.describeTable(dp, problem.getMaxWeight(), problem.getItems().size());

    }

    @Override
    public boolean performForwardPropagation(ProblemChain problem) {
        KnapsackProblemChain chainProblem = (KnapsackProblemChain) problem;

        /* init dp table */
        int n = chainProblem.getItems().size();
        DPEntity dp[][] = new DPEntity[chainProblem.getMaxWeight() + 1][n + 1];
        initDPTable(dp, chainProblem);

        /* forward propagation */
        boolean result = performForwardInduction(dp, chainProblem);

        /* saving intermediate calculations */
        chainProblem.setDp(dp);

        /* prepare output interchain values */
        Double[] outValues = new Double[chainProblem.getMaxWeight() + 1];
        for (int w = 0; w <= chainProblem.getMaxWeight(); w++) {
            if (dp[w][n] != null) {
                outValues[w] = dp[w][n].value;
            } else {
                outValues[w] = null;    // no solution for this w, so can't use this w in the next problem
            }
        }
        
        chainProblem.setOutputValues(outValues);
        
        // finish table
        KnapsackSolverHelp.describeTable(dp, chainProblem.getMaxWeight(), chainProblem.getItems().size());

        return result;
    }

    @Override
    public boolean performBackwardInduction(ProblemChain problem, Integer tightWeight) {
        KnapsackProblemChain chainProblem = (KnapsackProblemChain) problem;
        return performBackwardInduction(chainProblem.getDp(), chainProblem, tightWeight);
    }
}
