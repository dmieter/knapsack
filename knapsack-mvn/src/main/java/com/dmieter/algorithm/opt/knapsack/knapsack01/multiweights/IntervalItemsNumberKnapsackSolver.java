package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

/**
 *
 * @author dmiet
 */
public class IntervalItemsNumberKnapsackSolver extends FixedItemsNumberKnapsackSolver {

    @Override
    protected boolean backwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {
        return backwardInduction(dp, problem, null);
    }

    protected boolean backwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, Integer startingWeight) {
        /* II. Backward induction */
        int n = problem.getItems().size();

        /* 0. Convering problem to Interval problem */
        IntervalItemsNumberKnapsackProblem intervalProblem = (IntervalItemsNumberKnapsackProblem) problem;

        /* 1. Finding items number from min-max interval providing maximum value */
        int requiredItemsNumber = getBestItemsNumber(dp, intervalProblem, n, startingWeight != null ? startingWeight : problem.getMaxWeight());
        /* 2. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = startingWeight != null ? startingWeight : calculateTightWeight(dp, problem, n, requiredItemsNumber);

        /* 3. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredItemNum = n;
        int itemsLeft = requiredItemsNumber;
        while (itemsLeft > 0) {

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
        
        problem.calculateStats();

        return true;
    }


    // we want to find what itemsNumber from the interval provides the best output value
    protected int getBestItemsNumber(DPEntity[][][] dp, IntervalItemsNumberKnapsackProblem problem, int n) {
        return getBestItemsNumber(dp, problem, n, problem.getMaxWeight());
    }

    // we want to find what itemsNumber from the interval provides the best output value with tightWeight
    protected Integer getBestItemsNumber(DPEntity[][][] dp, IntervalItemsNumberKnapsackProblem problem, int n, int tightWeight) {

        Double maxValue = Double.NEGATIVE_INFINITY;
        Integer bestItemsNumber = null;
        for (int itemsNumber = problem.getMinItemsNumber(); itemsNumber <= problem.getMaxItemsNumber(); itemsNumber++) {
            if (dp[tightWeight][n][itemsNumber] != null) {
                Double value = dp[tightWeight][n][itemsNumber].value;
                if (value > maxValue) {
                    maxValue = value;
                    bestItemsNumber = itemsNumber;
                }
            }
        }

        return bestItemsNumber;
    }

    @Override
    protected Double getMaxValue(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, int n) {

        IntervalItemsNumberKnapsackProblem intervalProblem = (IntervalItemsNumberKnapsackProblem) problem;

        Double maxValue = null;
        for (int itemsNumber = intervalProblem.getMinItemsNumber(); itemsNumber <= intervalProblem.getMaxItemsNumber(); itemsNumber++) {

            if (dp[problem.getMaxWeight()][n][itemsNumber] != null) {
                Double value = dp[problem.getMaxWeight()][n][itemsNumber].value;
                if (maxValue == null || maxValue < value) {
                    maxValue = value;
                }
            }
        }

        return maxValue;

    }

}
