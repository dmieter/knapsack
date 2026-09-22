package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolver;
import java.util.ListIterator;

/**
 *
 * @author emelyanov
 */

/* solves chains of FixedItemsNumberKnapsack problems */
public class FixedItemsNumberKnapsackChainSolver extends FixedItemsNumberKnapsackSolver implements ChainSolver {

    protected void initDPTable(DPEntity[][][] dp, FixedItemsNumberKnapsackProblemChain problem) {
        
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

    protected boolean backwardInduction(DPEntity[][][] dp, FixedItemsNumberKnapsackProblem problem, Integer startingWeight) {
        /* II. Backward induction */
        int n = problem.getItems().size();
        
 /* 1. Finding a tight solution: minimum weight which still provides optimal solution value = maxValue */
        int tightWeight = calculateTightWeight(dp, problem, n, problem.getItemsRequiredNumber());
        if (startingWeight != null) {
            tightWeight = startingWeight;
        }

        /* 2. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredItemNum = n;
        int itemsLeft = problem.getItemsRequiredNumber();
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
            if (iterations > n || iterations > problem.getItemsRequiredNumber()) {
                return false;
            }
        }
        
        problem.calculateStats();

        return true;
    }

    @Override
    public boolean performForwardPropagation(ProblemChain problem) {
        FixedItemsNumberKnapsackProblemChain chainProblem = (FixedItemsNumberKnapsackProblemChain) problem;
        
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
            if(dp[w][n][chainProblem.getItemsRequiredNumber()] != null){
                outValues[w] = dp[w][n][chainProblem.getItemsRequiredNumber()].value;
            }else{
                outValues[w] = null;    // no solution for this w, so can't use this w in the next problem
            }
        }
        chainProblem.setOutputValues(outValues);
        
        return result;
    }

    @Override
    public boolean performBackwardInduction(ProblemChain problem, Integer tightWeight) {
        FixedItemsNumberKnapsackProblemChain chainProblem = (FixedItemsNumberKnapsackProblemChain) problem;
        return backwardInduction(chainProblem.getDp(), chainProblem, tightWeight);
    }
}
