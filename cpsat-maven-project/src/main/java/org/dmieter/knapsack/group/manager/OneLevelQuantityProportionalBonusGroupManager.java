
package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OneLevelQuantityProportionalBonusGroupManager extends SatQuantityAdditionGroupManager {
    
    private int quantityThreshold = 0;
    private int itemBonus = 0;
    
    
    public OneLevelQuantityProportionalBonusGroupManager(String name, int quantityThreshold, int itemBonus) {
        super(name, k -> k >= quantityThreshold ? k*itemBonus : 0d, null);
        this.quantityThreshold = quantityThreshold;
        this.itemBonus = itemBonus;
    }

    @Override
    public void createGroupInSatModel(CpModel model, LinearExprBuilder obj, LinearExprBuilder limit, Map<Item, IntVar> itemsMap, List<IntVar> allVars) {
        
        List<BoolVar> groupXVars = new ArrayList();
        IntVar count = model.newIntVar(0, this.improvedItems.size(), this.propertyName+"_count");
        
        
        for(Item item : this.improvedItems) {
            IntVar x = null;
            if(!itemsMap.containsKey(item)) {
                x = model.newBoolVar("x_" + item.id);
                itemsMap.put(item, x);
            } else {
                x = itemsMap.get(item);
            }
            groupXVars.add((BoolVar)x);
            
        }
        
        if(quantityThreshold <= 0 || itemBonus ==0) {
            return;   // nothing special, elements are created, return
        }
        
        BoolVar[] xVarsArray = groupXVars.toArray(new BoolVar[0]);
        model.addEquality(count, LinearExpr.sum(xVarsArray));
        
        BoolVar g = model.newBoolVar(this.propertyName + "_used_bonus");
        model.addGreaterOrEqual(count, quantityThreshold).onlyEnforceIf(g);
        model.addLessOrEqual(count, quantityThreshold - 1).onlyEnforceIf(g.not());
        
        IntVar bonusValue = model.newIntVar(0, itemBonus * this.improvedItems.size(), this.propertyName+"_bonus");
        model.addEquality(bonusValue, LinearExpr.term(count, itemBonus)).onlyEnforceIf(g);
        model.addEquality(bonusValue, 0).onlyEnforceIf(g.not());
        
        obj.addTerm(bonusValue, 1);
        
        allVars.add(g);
        allVars.add(count);
        allVars.add(bonusValue);
    }
    
}
