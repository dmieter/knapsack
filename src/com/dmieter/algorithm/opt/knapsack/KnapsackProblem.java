
package com.dmieter.algorithm.opt.knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dmieter
 */
public class KnapsackProblem {
    protected List<Item> items = new ArrayList<>();
    protected int maxWeight = 0;
    
    protected List<Item> selectedItems = new ArrayList<>();
    protected double totalValue = 0;
    protected int totalWeight = 0;
    protected int knapsackSize = 0;
    
    public void calculateStats(){
        totalValue = 0;
        totalWeight = 0;
        for(Item item : selectedItems){
            totalValue += item.getValue();
            totalWeight += item.getWeight();
        }
        knapsackSize = selectedItems.size();
    }
    
    public void resetSolution(){
        selectedItems = new ArrayList<>(); 
    }
    
    public Item getItem(int itemNum){
        return items.get(itemNum);
    }
    
    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * @return the maxWeight
     */
    public int getMaxWeight() {
        return maxWeight;
    }

    /**
     * @param maxWeight the maxWeight to set
     */
    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * @return the selectedItems
     */
    public List<Item> getSelectedItems() {
        return selectedItems;
    }

    /**
     * @param selectedItems the selectedItems to set
     */
    public void setSelectedItems(List<Item> selectedItems) {
        this.selectedItems = selectedItems;
    }

    /**
     * @return the totalValue
     */
    public double getTotalValue() {
        return totalValue;
    }

    /**
     * @return the totalWeight
     */
    public int getTotalWeight() {
        return totalWeight;
    }

    /**
     * @return the knapsackSize
     */
    public int getKnapsackSize() {
        return knapsackSize;
    }
}
