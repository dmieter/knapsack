
package com.dmieter.algorithm.opt.knapsack;

/**
 *
 * @author dmieter
 */
public class Item {
    public int id = -1;
    
    protected int weight = 0;
    protected double value = 0;
    
    protected Object refObject;

    public Item(int id, int weight, double value){
        this.id = id;
        this.weight = weight;
        this.value = value;
    }
    
    /**
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the refObject
     */
    public Object getRefObject() {
        return refObject;
    }

    /**
     * @param refObject the refObject to set
     */
    public void setRefObject(Object refObject) {
        this.refObject = refObject;
    }
    
    public boolean equals(Item item){
        return (weight == item.weight && value == item.value);
    }

    @Override
    public String toString() {
        return "Item " + id + " w: " + weight + " v: " + value;
    }
}
