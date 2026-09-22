package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.data.DPGroupEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.DetailedIntervalItemsNumberKnapsackSolver;

public class GroupItemIntervalKnapsackSolver extends DetailedIntervalItemsNumberKnapsackSolver {

    protected DPGroupEntity dpGroupFinal[][][];  // saving dp table for additional analysis of the solution

    public boolean solve(IntervalKnapsackWithGroupsProblem problem) {
        int n = problem.getGroupItems().size();

        /* I. Recurrent table calculations */
 /* DP[w][n][k] represents best value achievable for weight w, when 
                we compose k-items knapsack out of first n items */
 /* initializing boundary values */
        DPGroupEntity dp[][][] = new DPGroupEntity[problem.getMaxWeight() + 1][n + 1][problem.getItemsRequiredNumber() + 1];
        dpGroupFinal = null;

        /* setting init/edge values for DP problem */
        initDPTable(dp, problem, n);

        /* running forward induction */
        if (!forwardInduction(dp, problem, n)) {
            return false;
        }

        dpGroupFinal = dp;

        /* running backward induction to retrieve actual solution */
        if (!backwardInductionGroup(dp, problem, n)) {
            return false;
        }

        /* double check solution */
        return KnapsackAnalysis.validateSolution(problem);
    }

    protected void initDPTable(DPGroupEntity[][][] dp, IntervalKnapsackWithGroupsProblem problem, int n) {

        /* standard init with zeroes */
        for (int w = 0; w <= problem.getMaxWeight(); w++) {
            for (int j = 0; j <= n; j++) {
                dp[w][j][0] = new DPGroupEntity(0, -1, 0, 0);  // zero out init conditions
            }
        }
    }

    protected boolean forwardInduction(DPGroupEntity[][][] dp, IntervalKnapsackWithGroupsProblem problem, int n) {

        /* going through all group items as in common 01 problem starting from 1 (as 0 contains edge value) */
        for (int j = 1; j <= n; j++) {
            // 0. Start processing next group item
            GroupItem item = problem.getGroupItems().get(j - 1);  /* here j-1 from items list, it corresponds to j in DP */
            
            // 1. copy all previous tier (item) results to current tier (after that we are left to just update/improve some of them if needed)
            for (int w = 1; w <= problem.getMaxWeight(); w++) {
                for (int k = 1; k <= problem.getItemsRequiredNumber(); k++) {
                    if(dp[w][j - 1][k] != null) {
                        dp[w][j][k] = new DPGroupEntity(dp[w][j - 1][k]);
                    }
                }
            }

            // 2. getting and iterate over (k -> (v,w)) variants from groupitem subproblem
            List<GroupItem.ItemVariant> itemVariants = item.getVariants(problem.getItemsRequiredNumber(), problem.getMaxWeight());
            if (itemVariants == null || itemVariants.isEmpty()) {
                continue;   //to next group item, as this group item doesnt provide any variants
            }

            for(GroupItem.ItemVariant itemVariant : itemVariants) {
                
                /* going through all possible weights as in common 01 problem */
                for (int w = itemVariant.weight; w <= problem.getMaxWeight(); w++) {

                    for (int k = itemVariant.amount; k <= problem.getItemsRequiredNumber(); k++) {
                        
                        DPEntity curEntity1 = dp[w][j][k];  // we already copied it from item j-1 and possibly updated for previous k values
                        DPEntity prevEntity2 = dp[w - itemVariant.weight][j - 1][k - itemVariant.amount]; // check

                        /* if both are null - then there's no solution for current w, n, k, for example, k > 2 for first item 
                        the same holds for all bigger ks, so may be break here */
                        if (curEntity1 == null && prevEntity2 == null) {
                            continue; // break, not possible to update for current item
                        }

                        double curItemValue = Double.NEGATIVE_INFINITY;
                        if (curEntity1 != null) {
                            curItemValue = curEntity1.value;
                        }

                        double addedItemValue = Double.NEGATIVE_INFINITY;
                        if (prevEntity2 != null) {
                            addedItemValue = prevEntity2.value + itemVariant.value;
                        }

                        if (curItemValue >= addedItemValue) {
                            // do nothing
                        } else {
                            dp[w][j][k] = new DPGroupEntity(addedItemValue, j - 1, itemVariant.amount, itemVariant.weight);
                            /* here j-1 - reference to item number inside items list
                                                                                    to know what group item to use during the backward run at the current stage*/
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

    protected boolean backwardInductionGroup(DPGroupEntity[][][] dp, IntervalKnapsackWithGroupsProblem problem, Integer n) {

        /* II. Backward induction */

        /* 1. Finding particular items' number from min-max interval providing maximum value */
        int requiredItemsNumber = getBestItemsNumber(dp, problem, n, problem.getMaxWeight());
        /* 2. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = calculateTightWeight(dp, problem, n, requiredItemsNumber);

        /* 3. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredGroupItemNum = n;
        int itemsLeft = requiredItemsNumber;
        while (itemsLeft > 0) {

                /* adding next best item */
                DPGroupEntity dpEntity = dp[weightLeft][consideredGroupItemNum][itemsLeft];
                GroupItem groupItem = problem.getGroupItems().get(dpEntity.getRelatedGroupItemNum());

                //adding a set of items from the selected group item
                problem.getSelectedItems().addAll(groupItem.getBestItems(dpEntity.usedWeight, dpEntity.usedAmount));

                /* modifying indexes to find the next item */
                weightLeft -= dpEntity.usedWeight;
                itemsLeft -= dpEntity.usedAmount;
                consideredGroupItemNum = dpEntity.getRelatedGroupItemNum();
            /* if we already added dpEntity.relatedObjectID: (j-1) in item list and jth in DP
                                                            then we need to consider only earlier items, starting from (j-1) in DP */

            //System.out.println("Item " + item.id + " is selected with w = " + item.getWeight() + " v = " + item.getValue());
            /* we can't have more items in a solution then a total items number */
            iterations++;
            if (iterations > n || iterations > requiredItemsNumber) {
                return false;
            }
        }
        
        problem.calculateStats();

        return true;
    }

    @Override
    public DPGroupEntity[][][] getDPTable() {
        return dpGroupFinal;
    }
}
