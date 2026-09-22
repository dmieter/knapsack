package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.calculations.CalculationStats;
import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import java.util.Iterator;

/**
 *
 * @author dmieter clone of FixedItemsNumberKnapsackSolver for deeper built-in
 * optimization technics such as dp table reuse
 */
public class FixedItemsNumberKnapsackSolverOpt implements IKnapsack01MultiWeightsSolver {

    public static CalculationStats dpOperations = new CalculationStats();

    protected DPEntity dp[][][] = null;
    protected int maxItemsNum = 30;
    protected int startingN = 1;
    /* we may try to reuse calculations and start from bigger n */
    protected FixedItemsNumberKnapsackProblem prevProblem;

    @Override
    public boolean solve(MultiWeightsKnapsackProblem problem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        /* clear previous solution if any */
        problem.resetSolution();

        /* preliminary steps to compare current problem with previous one to save some calculations */
        compareWithPreviousIterations(problem);

        /* saving current problem items size */
        int n = problem.getItems().size();

        /* Algorithm itself */
 /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
                we compose k-items knapsack out of first n items */
 /* 0. initializing table and boundary values */
        int initStartNum = startingN;
        if (dp == null /* not initialized yet */
                || dp[0].length - 1 < n /* old table is to small for new problem size */ 
                || startingN == 1) {    /* can't reuse older problem, starting all over */
            /* so we need to create new table as old isn't exist or we can't reuse it */

 /* sometimes we may need to allocate more space to use it in future iterations */
            int itemsTableSize = Math.max(n, maxItemsNum) + 1;
            dp = new DPEntity[problem.getMaxWeight() + 1][itemsTableSize][problem.itemsRequiredNumber + 1];
            /* +1 everywhere because 0th element - is for init edge values, items go from 1 to n+1 */

 /* we have new empty table: so make sure to start init from 0 element */
            initStartNum = 0;
            startingN = 1;  // we recreated the table, need to start calculations from 1 anyway
        }

        for (int w = 0; w <= problem.getMaxWeight(); w++) {
            for (int j = initStartNum; j <= n; j++) {
                dp[w][j][0] = new DPEntity(0);
                dpOperations.addInitOperation();
            }
        }


        /* 1. going through all items as in common 01 problem */
 /* assuming that dp is filled for all n <= startingN */
        for (int j = startingN; j <= n; j++) {
            /* going through all possible weights as in common 01 problem */
            for (int w = 1; w <= problem.getMaxWeight(); w++) {
                Item item = problem.getItem(j - 1);
                /* here j-1 from items list, it corresponds to j in DP */

                if (item.getWeight() <= w) {
                    /* if we can add this item to the knapsack */
                    for (int k = 1; k <= problem.itemsRequiredNumber; k++) {
                        dpOperations.addOperation();

                        /*DP[w][n][k] = max {DP[w][n-1][k] ; DP[w-wi][n-1][k-1]}*/
                        DPEntity prevEntity1 = dp[w][j - 1][k];
                        DPEntity prevEntity2 = dp[w - item.getWeight()][j - 1][k - 1];

                        /* if both are null - then there's no solution for current w, n, k, for example, k > 2 for first item 
                         the same holds for all bigger ks, so may be break here */
                        if (prevEntity1 == null && prevEntity2 == null) {
                            dp[w][j][k] = null;     // erasing (possiblly not null) values from previous calculations
                            continue;
                        }

                        double prevItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity1 != null) {
                            prevItemValue = prevEntity1.value;
                        }

                        double addedItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity2 != null) {
                            addedItemValue = prevEntity2.value + item.getValue();
                        }

                        if (prevItemValue >= addedItemValue) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        } else {
                            dp[w][j][k] = new DPEntity(addedItemValue, j - 1);
                            /* here j-1 - reference to item number inside items list
                                                                                    to know what item to use during the backward run at the current stage*/
                        }
                    }
                } else {
                    for (int k = 1; k <= problem.itemsRequiredNumber; k++) {
                        dpOperations.addOperation();
                        if (dp[w][j - 1][k] != null) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        } else {
                            dp[w][j][k] = null;
                        }
                    }
                }
            }
        }

        /* if we have solution with maxWeight and itemsRequiredNumber after checking all n items */
        if (dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber] == null) {
            return false;
        }

        /* solution value */
        double maxValue = dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber].value;
        //System.out.println("Max value is: " + maxValue);
        //System.out.println(maxValue + " - " + problem.getItems().size() + " - " + startingN);
//        if (maxValue <= 0) {        //?????
//            return false;
//        }

        /* II. Backward induction */
 /* 1. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = problem.getMaxWeight();

        for (int w = problem.getMaxWeight(); w > 0; w--) {

            /* if there's no solution with less weight then current weight is tight */
            if (dp[w - 1][n][problem.itemsRequiredNumber] == null) {
                tightWeight = w;
                break;
            }

            double curWeightValue = dp[w][n][problem.itemsRequiredNumber].value;
            double nextWeightValue = dp[w - 1][n][problem.itemsRequiredNumber].value;


            /* if solution with less weight is smaller then current weight is tight */
            if (nextWeightValue < curWeightValue) {
                tightWeight = w;
                break;
            }
        }

        /* 2. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredItemNum = n;
        int itemsLeft = problem.itemsRequiredNumber;
        while (weightLeft > 0) {

            /* adding next best item */
            DPEntity dpEntity = dp[weightLeft][consideredItemNum][itemsLeft];
            Item item = problem.getItem(dpEntity.relatedObjectID);
            problem.getSelectedItems().add(item);

            /* modifying indexes to find the next item */
            weightLeft -= item.getWeight();
            itemsLeft--;
            consideredItemNum = dpEntity.relatedObjectID;
            /* if we already added dpEntity.relatedObjectID: (j-1) in item list and jth in DP
                                                            then we need to consider only earlier items, starting from (j-1) in DP */

            //System.out.println("Item " + item.id + " is selected with w = " + item.getWeight() + " v = " + item.getValue());
            /* we can't have more items in a solution then a total items number */
            iterations++;
            if (iterations > n || iterations > problem.itemsRequiredNumber) {
                return false;
            }
        }

        return KnapsackAnalysis.validateSolution(problem);
    }

    public boolean backupsolve(FixedItemsNumberKnapsackProblem problem) {
        int n = problem.getItems().size();

        /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
        we compose k-items knapsack out of first n items */
 /*DP[w][n][k] = max {DP[w][n-1][k] ; DP[w-wi][n-1][k-1]}*/
 /* initializing boundary values */
        DPEntity dp[][][] = new DPEntity[problem.getMaxWeight() + 1][n + 1][problem.itemsRequiredNumber + 1];
        for (int j = 0; j <= n; j++) {
            for (int w = 0; w <= problem.getMaxWeight(); w++) {
                dp[w][j][0] = new DPEntity(0);
            }
        }

        /* going through all items as in common 01 problem */
        for (int j = 1; j <= n; j++) {
            /* going through all possible weights as in common 01 problem */
            for (int w = 1; w <= problem.getMaxWeight(); w++) {
                Item item = problem.getItem(j - 1);
                /* here j-1 from items list, it corresponds to j in DP */

                if (item.getWeight() <= w) {
                    /* if we can add this item to the knapsack */
                    for (int k = 1; k <= problem.itemsRequiredNumber; k++) {

                        /*DP[w][n][k] = max {DP[w][n-1][k] ; DP[w-wi][n-1][k-1]}*/
                        double prevItemValue = 0;
                        if (dp[w][j - 1][k] != null) {
                            prevItemValue = dp[w][j - 1][k].value;
                        }
                        double addedItemValue = item.getValue();
                        if (dp[w - item.getWeight()][j - 1][k - 1] != null) {
                            addedItemValue += dp[w - item.getWeight()][j - 1][k - 1].value;
                        }

                        if (prevItemValue >= addedItemValue) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        } else {
                            dp[w][j][k] = new DPEntity(addedItemValue, j - 1);
                            /* here j-1 - reference to item number inside items list
                                                                                    to know what item to use during the backward run at the current stage*/
                        }
                    }
                } else {
                    for (int k = 1; k < problem.itemsRequiredNumber; k++) {
                        if (dp[w][j - 1][k] != null) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        }
                    }
                }
            }
        }

        /* if we have solution with maxWeight and itemsRequiredNumber after checking all n items */
        if (dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber] == null) {
            return false;
        }

        /* solution value */
        double maxValue = dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber].value;
        //System.out.println("Max value is: " + maxValue);
        if (maxValue <= 0) {
            return false;
        }

        /* II. Backward induction */
 /* 1. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = problem.getMaxWeight();

        for (int w = problem.getMaxWeight(); w > 0; w--) {
            double curWeightValue = dp[w][n][problem.itemsRequiredNumber].value;
            double nextWeightValue = dp[w - 1][n][problem.itemsRequiredNumber].value;

            if (nextWeightValue < curWeightValue) {
                tightWeight = w;
                break;
            }
        }

        /* 2. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredItemNum = n;
        int itemsLeft = problem.itemsRequiredNumber;
        while (weightLeft > 0) {

            /* adding next best item */
            DPEntity dpEntity = dp[weightLeft][consideredItemNum][itemsLeft];
            Item item = problem.getItem(dpEntity.relatedObjectID);
            problem.getSelectedItems().add(item);

            /* modifying indexes to find the next item */
            weightLeft -= item.getWeight();
            itemsLeft--;
            consideredItemNum = dpEntity.relatedObjectID;
            /* if we already added dpEntity.relatedObjectID: (j-1) in item list and jth in DP
                                                            then we need to consider only earlier items, starting from (j-1) in DP */

            //System.out.println("Item " + item.id + " is selected with w = " + item.getWeight() + " v = " + item.getValue());
            /* we can't have more items in a solution then a total items number */
            iterations++;
            if (iterations > n || iterations > problem.itemsRequiredNumber) {
                return false;
            }
        }

        return KnapsackAnalysis.validateSolution(problem);
    }

    private int calcStartingItemNumber(FixedItemsNumberKnapsackProblem problem) {
        if (prevProblem == null
                || problem.getMaxWeight() != prevProblem.getMaxWeight()
                || problem.getItemsRequiredNumber() != prevProblem.getItemsRequiredNumber()
                || prevProblem.getItems().isEmpty()) {

            return 1;
            /* problems are different, we need to calculate it from scratch, from first item */
        }

        int minSize = Math.min(problem.getItems().size(), prevProblem.getItems().size());

        Iterator<Item> newIt = problem.getItems().iterator();
        Iterator<Item> prevIt = prevProblem.getItems().iterator();
        for (int i = 1; i <= minSize; i++) {
            Item newItem = newIt.next();
            Item prevItem = prevIt.next();
            dpOperations.addOptOperation();

            if (!prevItem.equals(newItem)) {
                return i;
                /* order number of first element different from previous item with same order number */
 /* because we start from 1, 0 - base */
            }
        }

        return minSize + 1;
        /* returning minSize, because there's no difference in minSize-1 element */
 /* because we start from 1, 0 - base */

    }

    /* preliminary steps to compare current problem with previous one to save some calculations */
    protected void compareWithPreviousIterations(FixedItemsNumberKnapsackProblem problem) {

        /* depends on prevProblem, should be called before prevProblem is updated */
        startingN = calcStartingItemNumber(problem);
        //startingN = 1;

        /* saving current problem for next iteration */
        if (prevProblem == null) {
            prevProblem = new FixedItemsNumberKnapsackProblem();
        }
        prevProblem.setMaxWeight(problem.getMaxWeight());
        prevProblem.setItemsRequiredNumber(problem.itemsRequiredNumber);
        prevProblem.getItems().clear();
        prevProblem.getItems().addAll(problem.getItems());

        dpOperations.addOptOperation(4);
    }

    /**
     * @param maxItemsNum the maxItemsNum to set
     */
    public void setMaxItemsNum(int maxItemsNum) {
        this.maxItemsNum = maxItemsNum;
    }

    @Override
    public void flush() {
        dp = null;
        maxItemsNum = 30;
        startingN = 1;
        prevProblem = null;
    }

}
