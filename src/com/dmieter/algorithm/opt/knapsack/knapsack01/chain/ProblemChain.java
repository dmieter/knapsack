
package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import com.dmieter.algorithm.opt.knapsack.Item;
import java.util.List;

/**
 *
 * @author emelyanov
 */
public interface ProblemChain {
    
    public void setId(long id);
    public long getId();
    
    public Double[] getInitValues();
    public void setInitValues(Double[] initValues);
    public Double[] getOutputValues();
    public void setOutputValues(Double[] outValues);
    
    public List<Item> getSelectedItems();
    public int getTotalWeight();
    public double getTotalValue();
    
    public void calculateStats();
}
