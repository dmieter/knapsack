package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

// saves some calculation details and logs to enable solution post processing, otherwise the same as parent
public class DetailedIntervalItemsNumberKnapsackSolver extends IntervalItemsNumberKnapsackSolver {
    
    protected DPEntity dpFinal[][][];  // saving dp table for additional analysis of the solution
    
    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        int n = problem.getItems().size();

        /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
                we compose k-items knapsack out of first n items */
 /* initializing boundary values */
        DPEntity dp[][][] = new DPEntity[problem.getMaxWeight() + 1][n + 1][problem.getItemsRequiredNumber() + 1];
        dpFinal = null;

        /* setting init/edge values for DP problem */
        initDPTable(dp, problem, n);

        /* running forward induction */
        if (!forwardInduction(dp, problem, n)) {
            return false;
        }

        dpFinal = dp;

        /* running backward induction */
        if (!backwardInduction(dp, problem, n)) {
            return false;
        }

        /* double check solution */
        return KnapsackAnalysis.validateSolution(problem);
    }

    public DPEntity[][][] getDPTable() {
        return dpFinal;
    }
}
