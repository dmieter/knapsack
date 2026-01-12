
package org.dmieter.knapsack.group.test;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemGroupKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.GroupPropertyManager;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.HashMap;
import java.util.Map;
import org.dmieter.knapsack.group.manager.SatConvertableGroupManager;


public class KnapsackSatConverter {

    public CpModel convertHierarchicalKnapsackProblem(IntervalKnapsackWithGroupsProblem groupProblem) {
        Map<Item, Literal> itemsMap = new HashMap<>();
        
        CpModel model = new CpModel();
        LinearExprBuilder obj = LinearExpr.newBuilder();
        LinearExprBuilder limit = LinearExpr.newBuilder();
        LinearExprBuilder totalCount = LinearExpr.newBuilder();

        
        for(GroupItem groupItem : groupProblem.getGroupItems()) {
            for(GroupPropertyManager groupManager : groupItem.collectGroupPropertyManagers()) {
                if(! (groupManager instanceof SatConvertableGroupManager)) {
                    throw new IllegalStateException("Can not convert group " + groupManager.propertyName);
                } else {
                    ((SatConvertableGroupManager)groupManager).createGroupInSatModel(model, obj, limit, itemsMap);
                }
            }
        }
        
  
        for (Map.Entry<Item, Literal> entry: itemsMap.entrySet()) {
            obj.addTerm(entry.getValue(), ((Double)entry.getKey().getValue()).longValue());
            limit.addTerm(entry.getValue(), entry.getKey().getWeight());
            totalCount.addTerm(entry.getValue(), 1);
        }
        
        model.addLessOrEqual(limit, groupProblem.getMaxWeight());
        model.addLessOrEqual(totalCount, groupProblem.getMaxItemsNumber());
        model.addGreaterOrEqual(totalCount, groupProblem.getMinItemsNumber());
        
        model.maximize(obj);

        return model;
    }
    
}
