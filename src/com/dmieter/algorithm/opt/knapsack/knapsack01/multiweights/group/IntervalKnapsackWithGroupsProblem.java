package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.GroupPropertyManager;

public class IntervalKnapsackWithGroupsProblem extends IntervalItemsNumberKnapsackProblem{
    protected List<GroupItem> groupItems = new ArrayList<>();

    protected double improvedTotalValue = 0;
    protected int improvedTotalWeight = 0;

    public List<GroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<GroupItem> groupItems) {
        this.groupItems = groupItems;
        this.items = groupItems.stream()
                    .flatMap(g -> g.subItems.stream())
                    .collect(Collectors.toList());
    }

    public double getImprovedTotalValue() {
        return improvedTotalValue;
    }

    public Integer getImprovedTotalWeight() {
        return improvedTotalWeight;
    }

    public void improveTotalValue(Double addValue) {
        improvedTotalValue += addValue;
    }

    public void improveTotalWeight(Integer addValue) {
        improvedTotalWeight += addValue;
    }

    public void calculateStats() {
        super.calculateStats();

        improvedTotalValue = totalValue;
        improvedTotalWeight = totalWeight;
        groupItems.stream()
                    .filter(g -> g.groupPropertyManager != null)
                    .map(g -> g.groupPropertyManager)
                    .forEach(g -> g.updateSolution(this));  // update solution based on group dependencies between selected items
    }
}
