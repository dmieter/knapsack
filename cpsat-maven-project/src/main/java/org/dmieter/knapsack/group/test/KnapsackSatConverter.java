
package org.dmieter.knapsack.group.test;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemGroupKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.GroupPropertyManager;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.dmieter.knapsack.group.manager.SatConvertableGroupManager;


public class KnapsackSatConverter {
    
    public List<IntVar> allVars = new ArrayList();
    private LinearArgument limitVar;

    
    public CpModel convertHierarchicalKnapsackProblem(IntervalKnapsackWithGroupsProblem groupProblem) {
        Map<Item, IntVar> itemsMap = new HashMap<>();
        
        CpModel model = new CpModel();
        LinearExprBuilder obj = LinearExpr.newBuilder();
        LinearExprBuilder limit = LinearExpr.newBuilder();
        LinearExprBuilder totalCount = LinearExpr.newBuilder();

        
        for(GroupItem groupItem : groupProblem.getGroupItems()) {
            for(GroupPropertyManager groupManager : groupItem.collectGroupPropertyManagers()) {
                if(! (groupManager instanceof SatConvertableGroupManager)) {
                    throw new IllegalStateException("Can not convert group " + groupManager.propertyName);
                } else {
                    ((SatConvertableGroupManager)groupManager).createGroupInSatModel(model, obj, limit, itemsMap, allVars);
                }
            }
        }
        
  
        for (Map.Entry<Item, IntVar> entry: itemsMap.entrySet()) {
            obj.addTerm(entry.getValue(), ((Double)entry.getKey().getValue()).longValue());
            limit.addTerm(entry.getValue(), entry.getKey().getWeight() * SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER);
            totalCount.addTerm(entry.getValue(), 1);
        }
        
        model.addLessOrEqual(limit, groupProblem.getMaxWeight() * SatConvertableGroupManager.BASE_WEIGHT_MULTIPLIER);
        model.addLessOrEqual(totalCount, groupProblem.getMaxItemsNumber());
        model.addGreaterOrEqual(totalCount, groupProblem.getMinItemsNumber());
        
        model.maximize(obj);
        allVars.addAll(itemsMap.values());
        limitVar = limit;

        return model;
    }
    
    public void printResultDetails(CpSolver solver) {
        
        System.out.println("limit = " + solver.value(limitVar));
        
        allVars = allVars.stream().sorted(Comparator.comparing(IntVar::getName)).collect(Collectors.toList());
        for(IntVar variable : allVars){
            if(solver.value(variable) != 0 || variable.getName().contains("used")) {
                System.out.println(variable.getName() + " = " + solver.value(variable));
            }
        }
        
    }
    
}
