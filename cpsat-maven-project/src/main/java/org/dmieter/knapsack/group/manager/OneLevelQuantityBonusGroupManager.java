
package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.Map;
import java.util.function.Function;


public class OneLevelQuantityBonusGroupManager extends QuantityAdditionWeightGroupManager implements SatConvertableGroupManager {
    
    private int quantityThreshold = 0;
    private int singleBonus = 0;
    
    
    public OneLevelQuantityBonusGroupManager(String name, int quantityThreshold, int singleBonus) {
        super(name, k -> k >= quantityThreshold ? singleBonus : 0d, null);
        this.quantityThreshold = quantityThreshold;
        this.singleBonus = singleBonus;
    }

    @Override
    public void createGroupInSatModel(CpModel model, LinearExprBuilder obj, LinearExprBuilder limit, Map<Item, Literal> itemsMap) {
        
        
        LinearExprBuilder countGroup1 = LinearExpr.newBuilder();
        for(Item item : this.improvedItems) {
            Literal x = null;
            if(!itemsMap.containsKey(item)) {
                x = model.newBoolVar("x_" + item.id);
                itemsMap.put(item, x);
            } else {
                x = itemsMap.get(item);
            }
            //Literal x = itemsMap.putIfAbsent(item, model.newBoolVar("x_" + item.id));
            countGroup1.addTerm(x, 1);
        }
        
        if(quantityThreshold <= 0 || singleBonus ==0) {
            return;   // nothing special, elements are created, return
        }
        
        Literal g = model.newBoolVar(this.propertyName);
        model.addGreaterOrEqual(countGroup1, quantityThreshold).onlyEnforceIf(g);
        model.addLessOrEqual(countGroup1, quantityThreshold - 1).onlyEnforceIf(g.not());
        
        obj.addTerm(g, singleBonus);
    }
    
}
