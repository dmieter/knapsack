
package org.dmieter.knapsack.group.manager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.List;
import java.util.Map;

public interface SatConvertableGroupManager {

    public static int MAX_ITEM_WEIGHT = 100;
    public static int BASE_WEIGHT_MULTIPLIER = 10;  // we suppose all discounts will be like 0.1, 0.2, 0.3. etc.
    
    void createGroupInSatModel(CpModel model, LinearExprBuilder obj, LinearExprBuilder limit, Map<Item, IntVar> itemsMap, List<IntVar> allVars);
    
}
