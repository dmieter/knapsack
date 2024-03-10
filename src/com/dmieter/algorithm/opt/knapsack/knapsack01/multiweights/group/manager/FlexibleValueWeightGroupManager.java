package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager;



import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem.ItemVariant;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;

public class FlexibleValueWeightGroupManager extends GroupPropertyManager {

    private Integer maxOriginWeight = Integer.MIN_VALUE;
    private Function<Integer, Double> valueBoostFunction;
    private Function<Integer, Double> weightReductionFunction;

    public FlexibleValueWeightGroupManager(String name) {
        this.propertyName = name;
    }

    public FlexibleValueWeightGroupManager(String name, Function<Integer, Double> valueBoostFunction, Function<Integer, Double> weightReductionFunction) {
        this.propertyName = name;
        this.valueBoostFunction = valueBoostFunction;
        this.weightReductionFunction = weightReductionFunction;
    }

    private Double getValueBoostFactor(Integer amount) {
        if(valueBoostFunction == null) { 
            return 1d; 
        }
        else {
            return valueBoostFunction.apply(amount);
        }
    }

    private Double getWeightReductionFactor(Integer amount) {
        if(weightReductionFunction == null) { 
            return 1d; 
        }
        else {
            return weightReductionFunction.apply(amount);
        }
    }

    private Integer getReducedWeight(Integer amount, Integer weight) {
        return ((Double) Math.ceil(weight * getWeightReductionFactor(amount))).intValue();
    }

    private Double getImprovedValue(Integer amount, Double value) {
        return value * getValueBoostFactor(amount);
    }

    @Override
    public ItemVariant createItemVariant(int k, int w, double value) {
        if(w > maxOriginWeight) {
            maxOriginWeight = w;    // storing max processed weight as upper bound for decoding in getOriginWeight function
        }
        Integer reducedWeight = getReducedWeight(k, w);
        Double boostedValue = getImprovedValue(k, value);
        return new ItemVariant(k, reducedWeight, boostedValue);
    }

    // decoding original weight from sale/decreased weight to that of the original problem without group improvements
    @Override
    public Integer getOriginWeight(Integer weight, Integer amount) {
        Integer originWeight = ((Double) Math.ceil(weight/getWeightReductionFactor(amount))).intValue();

        // better have exception then possibly incorrect behavior with strict upper bound
        //return originWeight <= maxOriginWeight ? originWeight : maxOriginWeight;  
        return originWeight;
    }

    @Override
    public void updateSolution(IntervalKnapsackWithGroupsProblem problem) {
        List<Item> selectedGroupItems = problem.getSelectedItems().stream()
                                        .filter(item -> improvedItems.contains(item))
                                        .collect(Collectors.toList());

        Integer totalWeight = selectedGroupItems.stream()
                                .map(item -> item.getWeight())
                                .reduce(0, (a,b) -> a + b);

        Double totalValue = selectedGroupItems.stream()
                                .map(item -> item.getValue())
                                .reduce(0d, (a,b) -> a + b);   
                                
        Integer weightReduction = totalWeight - getReducedWeight(selectedGroupItems.size(), totalWeight);
        Double valueBoost = getImprovedValue(selectedGroupItems.size(), totalValue) - totalValue;

        if(weightReduction > 0 || valueBoost > 0) {
            System.out.println("Weight reudced by " + weightReduction + " Value improved by " + valueBoost + " as " 
            + selectedGroupItems.size() + " items " + selectedGroupItems + " selected with group property " + propertyName);
        }

        problem.improveTotalValue(valueBoost);
        problem.improveTotalWeight(-weightReduction);
        
    }

}
