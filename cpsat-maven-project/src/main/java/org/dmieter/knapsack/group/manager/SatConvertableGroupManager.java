
package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.Map;

public interface SatConvertableGroupManager {

    void createGroupInSatModel(CpModel model, LinearExprBuilder obj, LinearExprBuilder limit, Map<Item, Literal> itemsMap);
    
}
