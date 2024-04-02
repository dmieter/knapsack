package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.data.DPEntity;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.DetailedIntervalItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;

public class GroupItemKnapsack extends GroupItem {

    protected DPEntity[][][] dp = null;        // simple dp table for knapsack problem over subitems
    protected Integer localMaxWeight;
    protected Integer localMaxItemsNum; 
    protected List<ItemVariant> variants = null;

    public GroupItemKnapsack(int id, List<Item> subItems) {
        super(id, subItems);
    }

    @Override
    public List<ItemVariant> getVariants(Integer maxAmount, Integer maxWeight) {
        if (variants == null) {
            solveSubProblem(maxAmount, maxWeight);
        }

        return variants;
    }

    @Override
    public List<Item> getBestItems(Integer weight, Integer amount) {
        // We should perform backward induction starting with values weight, amount, subItems.size()
        // Additionally may need to provide some log on what items from what groupitem and for what cost were selected

        System.out.println("Retrieving " + amount + " items with weight " + weight + " from KnapsackGroupItem " + id);

        Integer startingWeight = weight;
        if(groupPropertyManager != null) {
            startingWeight = groupPropertyManager.getOriginWeight(weight, amount);
            System.out.println("Original starting weight for this group is " + startingWeight);
        }
        

        List<Item> selectedItems = new ArrayList<>();

        /* 1. Backward induction from tight solution */
        int weightLeft = startingWeight;
        int iterations = 0;
        int consideredItemNum = subItems.size(); // start from last item
        int itemsLeft = amount;
        while (itemsLeft > 0) {

            /* adding next best item */
            DPEntity dpEntity = dp[weightLeft][consideredItemNum][itemsLeft];
            Item item = subItems.get(dpEntity.relatedObjectID);
            selectedItems.add(item);

            /* modifying indexes to find the next item */
            weightLeft -= item.getWeight();
            itemsLeft--;
            consideredItemNum = dpEntity.relatedObjectID;
            /* if we already added dpEntity.relatedObjectID: (j-1) in item list and jth in DP
                                                            then we need to consider only earlier items, starting from (j-1) in DP */

            //System.out.println("Item " + item.id + " is selected with w = " + item.getWeight() + " v = " + item.getValue());
            /* we can't have more items in a solution then a total items number */
            iterations++;
            if (iterations > amount) {
                System.out.println("Returning NULL items from KnapsackGroupItem " + id);
                return null;
            }
        }
        System.out.println("Returning following items from KnapsackGroupItem " + id + ": " + selectedItems);
        return selectedItems;
    }

    private void solveSubProblem(Integer maxAmount, Integer maxWeight) {

        variants = new ArrayList<>();

        IntervalItemsNumberKnapsackProblem problem = new IntervalItemsNumberKnapsackProblem();
        problem.setItems(subItems);
        problem.setMinItemsNumber(1);
        
        localMaxItemsNum = subItems.size() < maxAmount ? subItems.size() : maxAmount; // lesser from both
        problem.setMaxItemsNumber(localMaxItemsNum);           
        
        // TODO: need to increase max weight taking into account possible reductions from groupPropertyManager
        Integer subItemsWeight = subItems.stream().mapToInt(i -> i.getWeight()).sum();
        localMaxWeight = subItemsWeight < maxWeight ? subItemsWeight : maxWeight; // lesser from both
        if(groupPropertyManager != null) {
            localMaxWeight = groupPropertyManager.getOriginWeight(localMaxWeight, localMaxItemsNum); // we can use more weight if count on possible sales/weight reductions
        }
        problem.setMaxWeight(localMaxWeight);  

        DetailedIntervalItemsNumberKnapsackSolver solver = new DetailedIntervalItemsNumberKnapsackSolver();
        boolean success = solver.solve(problem);
        //System.out.println(KnapsackAnalysis.getSolutionInfo(problem));
        System.out.println("Inner problem for GroupItem " + id + " is finished: " + success);

        if(!success) {
            return;
        }
        
        // next we will analyze solver.dpFinal for available variants
        dp = solver.getDPTable();
        variants = prepareVariants(dp);
    }

    private List<ItemVariant> prepareVariants(DPEntity[][][] dpTable) {
        List<ItemVariant> variants = new ArrayList<>();
        
        for(int k = 1; k <= localMaxItemsNum; k++) {
            Double curValue = Double.NEGATIVE_INFINITY;
            for(int w = 0; w <= localMaxWeight; w++) {
                DPEntity nextEntity = dpTable[w][subItems.size()][k];
                if(nextEntity != null && nextEntity.value > curValue) {

                    ItemVariant itemVariant = null;
                    if(groupPropertyManager != null) {
                        itemVariant = groupPropertyManager.createItemVariant(k, w, nextEntity.value);   // create variant with group improvements
                    } else {
                        itemVariant = new ItemVariant(k, w, nextEntity.value);                          // create default variant
                    }
                    variants.add(itemVariant);
                    curValue = nextEntity.value;
                }
            }
        }

        return variants;
    }

}
