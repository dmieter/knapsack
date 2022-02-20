package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolver;

/**
 *
 * @author emelyanov
 */
public class FixedItemsMultiplicativeSolver extends FixedItemsNumberKnapsackSolver {

    @Override
    protected void initDPTable(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

        if (problem.getItemsRequiredNumber() < 1) {
            return;
        }

        /* standard init with zeroes */
        for (int w = 0; w <= problem.getMaxWeight(); w++) {
            for (int j = 0; j <= n; j++) {
                dp[w][j][0] = new DPEntity(1);
            }

        }
    }

    @Override
    protected boolean forwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

//        if (problem.itemsRequiredNumber == 1) {
//            return true;    // for n = 1 all values are already prepared during the init step
//        }

        /* going through all items as in common 01 problem */
        for (int j = 1; j <= n; j++) {
            /* going through all possible weights as in common 01 problem */
            for (int w = 1; w <= problem.getMaxWeight(); w++) {
                Item item = problem.getItem(j - 1);
                /* here j-1 from items list, it corresponds to j in DP */

                if (item.getWeight() <= w) {
                    /* if we can add this item to the knapsack */
                    for (int k = 1; k <= problem.getItemsRequiredNumber(); k++) {

                        /*DP[w][n][k] = max {DP[w][n-1][k] ; DP[w-wi][n-1][k-1] * cur.val}*/
                        DPEntity prevEntity1 = dp[w][j - 1][k];
                        DPEntity prevEntity2 = dp[w - item.getWeight()][j - 1][k - 1];

                        /* if both are null - then there's no solution for current w, n, k, for example, k > 2 for first item 
                         the same holds for all bigger ks, so may be break here */
                        if (prevEntity1 == null && prevEntity2 == null) {
                            continue; // break;
                        }

                        double prevItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity1 != null) {
                            prevItemValue = prevEntity1.value;
                        }

                        double multipliedItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity2 != null) {
                            multipliedItemValue = prevEntity2.value * item.getValue();
                        }

                        if (prevItemValue >= multipliedItemValue) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        } else {
                            dp[w][j][k] = new DPEntity(multipliedItemValue, j - 1);
                            /* here j-1 - reference to item number inside items list
                                                                                    to know what item to use during the backward run at the current stage*/
                        }
                    }
                } else {
                    for (int k = 1; k <= problem.getItemsRequiredNumber(); k++) {
                        if (dp[w][j - 1][k] != null) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        }
                    }
                }
            }
        }

        /* if we have solution with maxWeight and itemsRequiredNumber after checking all n items */
        if (dp[problem.getMaxWeight()][n][problem.getItemsRequiredNumber()] == null) {
            return false;
        }
        /* solution value */
        double maxValue = dp[problem.getMaxWeight()][n][problem.getItemsRequiredNumber()].value;
        //System.out.println("Max value is: " + maxValue);
        return true;
    }

}
