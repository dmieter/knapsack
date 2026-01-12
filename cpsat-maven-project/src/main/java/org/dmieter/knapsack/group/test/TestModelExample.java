
package org.dmieter.knapsack.group.test;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.BruteForceIntervalKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemGroupKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemIntervalKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityMultiplierWeightGroupManager;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dmieter.knapsack.group.manager.OneLevelQuantityBonusGroupManager;


public class TestModelExample {
    
    public static void main(String[] args) {
        Loader.loadNativeLibraries();
        runTest();
    }
    
    
    public static void runTest() {
        
        //solveBruteForce(generateTestProblem());
        solveGKA(generateTestProblem());
        solveSat(generateTestProblem());
    }
    
    public static void solveGKA(IntervalKnapsackWithGroupsProblem groupProblem) {
        System.out.println("\n=========== GROUP KNAPSACK RESULT ============\n");
        GroupItemIntervalKnapsackSolver groupSolver = new GroupItemIntervalKnapsackSolver();
        Long startTime = System.nanoTime();
        boolean groupSuccess = groupSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);

        QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
        groupProblem.calculateStats();
        QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);
    }
    
    public static void solveSat(IntervalKnapsackWithGroupsProblem groupProblem) {
        System.out.println("\n=========== CPSAT RESULT ============\n");
        
        CpModel model = new KnapsackSatConverter().convertHierarchicalKnapsackProblem(groupProblem);
        
        CpSolver solver = new CpSolver();
        Long startTime = System.nanoTime();
        final CpSolverStatus status = solver.solve(model);
        Long endTime = System.nanoTime();

        // Check that the problem has an optimal solution.
        if (status == CpSolverStatus.OPTIMAL) {
          System.out.println("Total packed value: " + solver.objectiveValue());
          /*long totalWeight = 0;

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

          System.out.println("Total packed weight: " + totalWeight);*/
          System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");   
          System.out.println("Problem solved in, ms: " + (endTime - startTime)/1000000d);
        } else {
          System.err.println("The problem does not have an optimal solution.");
        }
    }
    
    public static void solveBruteForce(IntervalKnapsackWithGroupsProblem groupProblem) {
        System.out.println("\n=========== BRUTE FORCE RESULT ============\n");
            BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
            Long startTime = System.nanoTime();
            boolean bruteSuccess = bruteSolver.solve(groupProblem);
            Long endTime = System.nanoTime();
            System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
            System.out.println("\nTime, ms: " + (endTime - startTime) * 1d / 1000000);
            System.out.println("\nFinished: " + bruteSuccess);

            QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
            groupProblem.calculateStats();
            QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
    }
    
    public static IntervalKnapsackWithGroupsProblem generateTestProblem() {
        int vmCount = 15;
        int totalCost = 200;

        int genNum = vmCount;
        int baseValue = 5;

        // DC1
        int startingId = 101;
        int dcCost = 9;
        List<Item> itemsDC1 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC1.add(item);
        }

        // DC2
        startingId = 201;
        baseValue = 3;
        dcCost = 5;
        List<Item> itemsDC2 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC2.add(item);
        }

        // DC3
        startingId = 301;
        baseValue = 4;
        dcCost = 7;
        List<Item> itemsDC3 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC3.add(item);
        }

        // DC4
        startingId = 401;
        baseValue = 6;
        dcCost = 12;
        List<Item> itemsDC4 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC4.add(item);
        }

        // DC5
        startingId = 501;
        baseValue = 5;
        dcCost = 10;
        List<Item> itemsDC5 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC5.add(item);
        }

        // DC6
        startingId = 601;
        baseValue = 10;
        dcCost = 15;
        List<Item> itemsDC6 = new ArrayList<>();
        for(int i = startingId; i < startingId + genNum; i++) {
            Item item = new Item(i,dcCost,baseValue);
            itemsDC6.add(item);
        }



        GroupItemKnapsack groupItemDC1 = new GroupItemKnapsack(1, itemsDC1);
        groupItemDC1.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC1", 2, 4));

        GroupItemKnapsack groupItemDC2 = new GroupItemKnapsack(2, itemsDC2);
        groupItemDC2.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC2", 1, 2));

        GroupItemKnapsack groupItemDC3 = new GroupItemKnapsack(3, itemsDC3);
        groupItemDC3.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC3", 3, 7));

        GroupItemKnapsack groupItemDC4 = new GroupItemKnapsack(4, itemsDC4);
        groupItemDC4.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC4", 2, 4));

        GroupItemKnapsack groupItemDC5 = new GroupItemKnapsack(5, itemsDC5);
        groupItemDC5.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC5", 2, 10));

        GroupItemKnapsack groupItemDC6 = new GroupItemKnapsack(6, itemsDC6);
        groupItemDC6.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("DC6", 1, 3));


        GroupItemGroupKnapsack groupItemZoneA = new GroupItemGroupKnapsack(1000, Arrays.asList(groupItemDC1));
        groupItemZoneA.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("Availability Zone A", 2, 4));

        GroupItemGroupKnapsack groupItemZoneB = new GroupItemGroupKnapsack(2000, Arrays.asList(groupItemDC2, groupItemDC3));
        groupItemZoneB.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("Availability Zone B", 2, 8));

        GroupItemGroupKnapsack groupItemZoneC = new GroupItemGroupKnapsack(3000, Arrays.asList(groupItemDC4, groupItemDC5, groupItemDC6));
        groupItemZoneC.setGroupPropertyManager(new OneLevelQuantityBonusGroupManager("Availability Zone C", 2, 10));


        List<GroupItem> regionGroupItems = Arrays.asList(groupItemZoneA, groupItemZoneB, groupItemZoneC);


        IntervalKnapsackWithGroupsProblem groupProblem = new IntervalKnapsackWithGroupsProblem();
        groupProblem.setGroupItems(regionGroupItems);
        groupProblem.setMaxWeight(totalCost);
        groupProblem.setMinItemsNumber(vmCount);
        groupProblem.setMaxItemsNumber(vmCount);

        return groupProblem;
        
        /*
        // 1. Solve group problem
        System.out.println("\n=========== GROUP KNAPSACK RESULT ============\n");
        GroupItemIntervalKnapsackSolver groupSolver = new GroupItemIntervalKnapsackSolver();
        Long startTime = System.nanoTime();
        boolean groupSuccess = groupSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);

        QuantityMultiplierWeightGroupManager.PRINT_LOGS = true;
        groupProblem.calculateStats();
        QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;

        // 3. Solve with brute
        if(true) {
            System.out.println("\n=========== BRUTE FORCE RESULT ============\n");
            BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
            startTime = System.nanoTime();
            boolean bruteSuccess = bruteSolver.solve(groupProblem);
            endTime = System.nanoTime();
            System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
            System.out.println("\nTime, ms: " + (endTime - startTime) * 1d / 1000000);
            System.out.println("\nFinished: " + groupSuccess + " " + bruteSuccess);

            QuantityMultiplierWeightGroupManager.PRINT_LOGS = true;
            groupProblem.calculateStats();
            QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
        }
*/
    }
}
