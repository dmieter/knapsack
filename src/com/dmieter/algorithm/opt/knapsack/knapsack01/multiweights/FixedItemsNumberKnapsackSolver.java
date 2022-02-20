package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

/**
 *
 * @author dmieter
 */
public class FixedItemsNumberKnapsackSolver implements IKnapsack01MultiWeightsSolver {

    @Override
    public boolean solve(MultiWeightsKnapsackProblem problem) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void initDPTable(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

        /* standard init with zeroes */
        for (int w = 0; w <= problem.getMaxWeight(); w++) {
            for (int j = 0; j <= n; j++) {
                dp[w][j][0] = new DPEntity(0);
            }
        }
    }

    protected boolean forwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

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
                        if (dp[w][j - 1][k] != null) {
                            dp[w][j][k] = new DPEntity(dp[w][j - 1][k]);
                        }
                    }
                }
            }
        }

        /* solution value */
        Double maxValue = getMaxValue(dp, problem, n);
        //System.out.println("Max value is: " + maxValue);
        //System.out.println(maxValue + " - " + problem.getItems().size());
        
        /* if we don't have have solution a satisfying solution return false*/
        return maxValue != null;
    }
    
    protected Double getMaxValue(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n){
        if (dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber] != null) {
            return dp[problem.getMaxWeight()][n][problem.itemsRequiredNumber].value;
        } else {
            return null;
        } 
    }

    protected int calculateTightWeight(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n, int itemsRequiredNumber){
        int tightWeight = problem.getMaxWeight();

        for (int w = problem.getMaxWeight(); w > 0; w--) {

            /* if there's no solution with less weight then current weight is tight */
            if (dp[w - 1][n][itemsRequiredNumber] == null) {
                tightWeight = w;
                break;
            }

            double curWeightValue = dp[w][n][itemsRequiredNumber].value;
            double nextWeightValue = dp[w - 1][n][itemsRequiredNumber].value;


            /* if solution with less weight is smaller then current weight is tight */
            if (nextWeightValue < curWeightValue) {
                tightWeight = w;
                break;
            }
        }
        
        return tightWeight;
    }
    
    protected boolean backwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {
        /* II. Backward induction */
 /* 1. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = calculateTightWeight(dp, problem, n, problem.itemsRequiredNumber);

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

        return true;
    }

    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        int n = problem.getItems().size();

        /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
                we compose k-items knapsack out of first n items */
 /* initializing boundary values */
        DPEntity dp[][][] = new DPEntity[problem.getMaxWeight() + 1][n + 1][problem.itemsRequiredNumber + 1];

        /* setting init/edge values for DP problem */
        initDPTable(dp, problem, n);

        /* running forward induction */
        if (!forwardInduction(dp, problem, n)) {
            return false;
        }

        /* running backward induction */
        if (!backwardInduction(dp, problem, n)) {
            return false;
        }

        /* double check solution */
        return KnapsackAnalysis.validateSolution(problem);
    }

    @Deprecated
    public boolean newbackupsolve(FixedItemsNumberKnapsackProblem problem) {
        int n = problem.getItems().size();

        /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
                we compose k-items knapsack out of first n items */
 /* initializing boundary values */
        DPEntity dp[][][] = new DPEntity[problem.getMaxWeight() + 1][n + 1][problem.itemsRequiredNumber + 1];

        /* setting init/edge values for DP problem */
        initDPTable(dp, problem, n);

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

    @Deprecated
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

    @Override
    public void flush() {
        // no variables to flush
    }

}
