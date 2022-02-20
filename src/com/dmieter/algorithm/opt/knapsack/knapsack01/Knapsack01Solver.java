package com.dmieter.algorithm.opt.knapsack.knapsack01;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;

/**
 *
 * @author dmieter
 */
public class Knapsack01Solver implements IKnapsack01Solver {

    @Override
    public boolean solve(KnapsackProblem problem) {
        int n = problem.getItems().size();

        /* I. Recurrent table calculations */
        
        DPEntity dp[][] = new DPEntity[problem.getMaxWeight() + 1][n + 1];
        for(int j = 0; j <= n; j++){
            dp[0][j] = new DPEntity(0);
        }
        for(int w = 0; w <= problem.getMaxWeight(); w++){
            dp[w][0] = new DPEntity(0);
        }
        
        for (int j = 1; j <= n; j++) {
            for (int w = 1; w <= problem.getMaxWeight(); w++) {
                Item item = problem.getItem(j - 1);
                if (item.getWeight() <= w) {
                    double prevWeightValue = dp[w][j - 1].value;
                    double addedWeightValue = dp[w - item.getWeight()][j - 1].value + item.getValue();
                    
                    if(prevWeightValue >= addedWeightValue){
                        dp[w][j] = new DPEntity(dp[w][j - 1]);
                    }else{
                        dp[w][j] = new DPEntity(addedWeightValue, j - 1);
                    }
                } else {
                    dp[w][j] = new DPEntity(dp[w][j - 1]);
                }
            }
        }
        
        
        double maxValue = dp[problem.getMaxWeight()][n].value;
        //System.out.println("Max value is: " + maxValue);
        if(maxValue <= 0){
            return false;
        }
        
        /* II. Backward induction */
        
        /* 1. Finding a tight solution */
        int tightWeight = problem.getMaxWeight();
        
        for(int w = problem.getMaxWeight(); w > 0; w--){
            double curWeightValue = dp[w][n].value;
            double nextWeightValue = dp[w-1][n].value;
            
            if(nextWeightValue < curWeightValue){
                tightWeight = w;
                break;
            }
        }

        //System.out.println("Tight solution weight is: " + tightWeight);
        
        /* 2. Backward induction from tight solution */
        int weightLeft = tightWeight;
        int iterations = 0;
        int consideredItemNum = n;
        while(weightLeft > 0){
            
            DPEntity dpEntity = dp[weightLeft][consideredItemNum];
            Item item = problem.getItem(dpEntity.relatedObjectID);
            problem.getSelectedItems().add(item);
            
            weightLeft -= item.getWeight();
            consideredItemNum = dpEntity.relatedObjectID;
            
            //System.out.println("Item " + item.id + " is selected with w = " + item.getWeight() + " v = " + item.getValue());
            
            /* we can't have more items in a solution then a total items number */
            iterations++;
            if(iterations > n){
                return false;
            }
        }
        
        return KnapsackAnalysis.validateSolution(problem);
    }

}
