
package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Deprecated
public class OldOneLevelQuantityProportionalDiscountGroupManager extends SatQuantityAdditionGroupManager {
    
    private int quantityThreshold = 0;
    private int discount = 0;
    
    
    public OldOneLevelQuantityProportionalDiscountGroupManager(String name, int quantityThreshold, int discount) {
        super(name, null,  k -> k >= quantityThreshold ? (1 - 1.0 * discount/SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER) : 1d);
        this.quantityThreshold = quantityThreshold;
        this.discount = discount;
    }
    
    public OldOneLevelQuantityProportionalDiscountGroupManager(String name, int quantityThreshold, int discount, boolean isLimitSupportable) {
        
        super(name, null,  k -> k >= quantityThreshold ? (1 - 1.0 * discount/SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER) : 1d);
        this.quantityThreshold = quantityThreshold;
        this.discount = discount;
        
        if(!isLimitSupportable){
            System.err.println("This manager " + propertyName + " affects limit anyway!");
        }
    }

    @Override
    public void createGroupInSatModel(CpModel model, LinearExprBuilder obj, LinearExprBuilder limit, Map<Item, IntVar> itemsMap, List<IntVar> allVars) {
        
        List<BoolVar> groupXVars = new ArrayList();
        IntVar count = model.newIntVar(0, this.improvedItems.size(), this.propertyName+"_count");
        
        LinearExprBuilder fullWeight = LinearExpr.newBuilder();
        LinearExprBuilder discountedWeight = LinearExpr.newBuilder();
        for(Item item : this.improvedItems) {
            IntVar x = null;
            if(!itemsMap.containsKey(item)) {
                x = model.newBoolVar("x_" + item.id);
                itemsMap.put(item, x);
            } else {
                x = itemsMap.get(item);
            }
            groupXVars.add((BoolVar)x);
            discountedWeight.addTerm(x, discount * item.getWeight());
            fullWeight.addTerm(x, item.getWeight() * SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER);
        }
        
        if(quantityThreshold <= 0 || discount ==0) {
            return;   // nothing special, elements are created, return
        }
        
        BoolVar[] xVarsArray = groupXVars.toArray(new BoolVar[0]);
        model.addEquality(count, LinearExpr.sum(xVarsArray));
        
        BoolVar g = model.newBoolVar(this.propertyName + "_used_discount");
        model.addGreaterOrEqual(count, quantityThreshold).onlyEnforceIf(g);
        model.addLessOrEqual(count, quantityThreshold - 1).onlyEnforceIf(g.not());
        

        
        IntVar actualWeight = model.newIntVar(0, SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER * this.improvedItems.size() * SatConvertableGroupManager.MAX_ITEM_WEIGHT
                , this.propertyName + "_actualWeight");
        model.addEquality(actualWeight, discountedWeight).onlyEnforceIf(g);
        model.addEquality(actualWeight, fullWeight).onlyEnforceIf(g.not());
        
        limit.addTerm(actualWeight, 1);
        
        allVars.add(g);
        allVars.add(count);
        allVars.add(actualWeight);
    }
    
}
