package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

/**
 *
 * @author emelyanov
 */
public class KnapsackSolverHelp {

    private static FixedItemsNumberKnapsackChainSolver finkChainSolver = null;
    private static Knapsack01ChainSolver k01ChainSolver = null;
    private static IntervalItemsNumberKnapsackChainSolver intervalChainSolver = null;

    public static ChainSolver getSolver(ProblemChain problem) {

        if (problem instanceof FixedItemsNumberKnapsackProblemChain) {
            if (finkChainSolver == null) {
                finkChainSolver = new FixedItemsNumberKnapsackChainSolver();
            }
            return finkChainSolver;
        } else if (problem instanceof KnapsackProblemChain) {
            if (k01ChainSolver == null) {
                k01ChainSolver = new Knapsack01ChainSolver();
            }
            return k01ChainSolver;
        } else if (problem instanceof IntervalItemsNumberKnapsackProblemChain) {
            if (intervalChainSolver == null) {
                intervalChainSolver = new IntervalItemsNumberKnapsackChainSolver();
            }
            return intervalChainSolver;
        }else {
            throw new IllegalStateException(problem.getClass() + " not supported");
        }
    }

    public static void describeTable(DPEntity dp[][], int W, int n) {
        for (int i = 0; i <= W; i++) {
            String line = "";
            for (int j = 0; j <= n; j++) {
                if(dp[i][j] != null){
                    line += " " + dp[i][j].value;
                } else {
                    line += " -";
                }
            }
            System.out.println(line);
        }
    }
    
    public static void describeValues(Double values [], int W) {
        System.out.println("VALUES:");
        for (int i = 0; i <= W; i++) {
            System.out.println(values[i]);
        }
    }
}
