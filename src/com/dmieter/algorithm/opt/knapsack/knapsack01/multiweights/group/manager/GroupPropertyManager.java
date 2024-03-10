package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager;


import java.util.ArrayList;
import java.util.List;

import com.dmieter.algorithm.opt.knapsack.Item;

import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem.ItemVariant;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;

public abstract class GroupPropertyManager {

    protected String propertyName = "Group Property";

    protected List<Item> improvedItems = new ArrayList<>();

    public void apply(List<Item> items) {
        improvedItems.addAll(items);
    }

    public abstract ItemVariant createItemVariant(int k, int w, double value);

    // cancels possible weight reductions (sales) and returns original weight
    public abstract Integer getOriginWeight(Integer weight, Integer amount);

    public abstract void updateSolution(IntervalKnapsackWithGroupsProblem problem);

}
