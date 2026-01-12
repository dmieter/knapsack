package org.or_tools.example;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/** Sample showing how to solve a multiple knapsack problem. */
public class HierarchicalKnapsackSat {
  public static void main(String[] args) {
    Loader.loadNativeLibraries();
    // Instantiate the data problem.
    final int[] weights = {18, 30, 42, 36, 31, 28, 42, 32, 15};
    final int[] values = {10, 30, 25, 50, 35, 30, 15, 40, 23};
    final int numItems = weights.length;
    final int[] allItems = IntStream.range(0, numItems).toArray();

    final int binCapacity = 100;

    CpModel model = new CpModel();

    // Variables.
    Literal[] x = new Literal[numItems];
    for (int i : allItems) {
        x[i] = model.newBoolVar("x_" + i);
    }

    // Constraints.

    // The amount packed in each bin cannot exceed its capacity.
    LinearExprBuilder load = LinearExpr.newBuilder();
    for (int i : allItems) {
      load.addTerm(x[i], weights[i]);
    }
    model.addLessOrEqual(load, binCapacity);
    

    LinearExprBuilder count = LinearExpr.newBuilder();
    for (int i : allItems) {
      count.addTerm(x[i], 1);
    }
    model.addEquality(count, 3);
    
    Literal g = model.newBoolVar("G");
    LinearExprBuilder countGroup1 = LinearExpr.newBuilder();
    countGroup1.addTerm(x[0], 1);
    countGroup1.addTerm(x[1], 1);
    countGroup1.addTerm(x[2], 1);
    
    model.addGreaterOrEqual(countGroup1, 3).onlyEnforceIf(g);
    model.addLessOrEqual(countGroup1, 2).onlyEnforceIf(g.not());
    


    // Objective.
    // Maximize total value of packed items.
    LinearExprBuilder obj = LinearExpr.newBuilder();
    for (int i : allItems) {
        obj.addTerm(x[i], values[i]);
    }
    obj.addTerm(g, 400);
    
    model.maximize(obj);

    CpSolver solver = new CpSolver();
    Long startTime = System.nanoTime();
    final CpSolverStatus status = solver.solve(model);
    Long endTime = System.nanoTime();

    // Check that the problem has an optimal solution.
    if (status == CpSolverStatus.OPTIMAL) {
      System.out.println("Total packed value: " + solver.objectiveValue());
      long totalWeight = 0;

      long binWeight = 0;
      long binValue = 0;
      for (int i : allItems) {
        if (solver.booleanValue(x[i])) {
          System.out.println("Item " + i + " weight: " + weights[i] + " value: " + values[i]);
          binWeight += weights[i];
          binValue += values[i];
        }
      }
      System.out.println("Packed bin weight: " + binWeight);
      System.out.println("Packed bin value: " + binValue);
      totalWeight += binWeight;
      
      System.out.println("Total packed weight: " + totalWeight);
      System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");   
      System.out.println("Problem solved in, ms: " + (endTime - startTime)/1000000d);
    } else {
      System.err.println("The problem does not have an optimal solution.");
    }
  }

  private HierarchicalKnapsackSat() {}
}
