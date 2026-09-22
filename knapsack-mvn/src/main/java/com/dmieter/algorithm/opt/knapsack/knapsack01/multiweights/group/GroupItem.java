package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.GroupPropertyManager;

public abstract class GroupItem {

    protected int id;

    protected List<Item> subItems = new ArrayList<>();      // subproblem items

    protected GroupPropertyManager groupPropertyManager;    // performs improvements over group usages (value, weight reductions, etc.)

    public GroupItem(int id, List<Item> subItems) {
        this.id = id;
        this.subItems.addAll(subItems);
    }

    public void setGroupPropertyManager(GroupPropertyManager groupPropertyManager) {
        this.groupPropertyManager = groupPropertyManager;
        groupPropertyManager.apply(collectInnerSubItems());
    }

    public List<GroupPropertyManager> collectGroupPropertyManagers() {
        if(groupPropertyManager != null) {
            return Collections.singletonList(groupPropertyManager);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public List<Item> collectInnerSubItems() {
        return subItems != null ? subItems : Collections.EMPTY_LIST;
    }

    // returns map of all possible Items as structure: amount -> (total value, total weight)
    public abstract List<ItemVariant> getVariants(Integer maxAmount, Integer maxWeight);


    // during backward induction we will know how many items and for what cost we should take from this group item (DPGroupEntity in dp table will store this info)
    // the items will be returned depending on the group type
    public abstract List<Item> getBestItems(Integer weight, Integer amount);

    public static class ItemVariant {
        public Integer amount;
        public Integer weight;
        public Double value;

        public Double originValue;      // origin value before any group improvements
        public Integer originWeight;    // origin weight before any group improvements

        public ItemVariant(Integer itemAmount, Integer itemWeight, Double itemValue) {
            amount = itemAmount;
            weight = itemWeight;
            value = itemValue;
            originValue = itemValue;
            originWeight = itemWeight;
        }

        public ItemVariant(Integer itemAmount, Integer itemWeight, Double itemValue, Integer originWeight, Double originValue) {
            amount = itemAmount;
            weight = itemWeight;
            value = itemValue;
            originValue = itemValue;
            originWeight = itemWeight;
        }
    }
}
