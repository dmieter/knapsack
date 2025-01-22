
package com.dmieter.algorithm.opt.knapsack;

import com.dmieter.algorithm.opt.knapsack.knapsack01.IKnapsack01Solver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.Knapsack01Solver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ChainKnapsackProblemSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ChainedKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.FixedItemsNumberKnapsackChainSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.BruteForceIntervalKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.FixedItemsNumberKnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.IntervalItemsNumberKnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.KnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolverOpt;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.*;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative.FixedItemsMultiplicativeProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative.FixedItemsMultiplicativeSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IKnapsack01MultiWeightsSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.TwoWeightsKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.GroupPropertyManager;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityMultiplierWeightGroupManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author dmieter
 */
public class Example {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //runSimpleIntervalExample();
        //runSimpleChainExample3();
        //runSimpleGroupExample();
        //runSimplestGroupExample();
        runSimpleHierarchicalExample();
    }

    private static void runSimpleExample() {
        Item item1 = new Item(1,7,7);
        Item item2 = new Item(2,5,5);
        Item item3 = new Item(3,4,4);
        Item item4 = new Item(4,1,1);
        Item item5 = new Item(5,1,1);
        Item item6 = new Item(6,1,2);
        Item item7 = new Item(7,1,1);
        Item item8 = new Item(8,2,3);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);
        items.add(item7);
        items.add(item8);

        /* I general 01 knapsack */
        KnapsackProblem problem = new KnapsackProblem();
        problem.setItems(items);
        problem.setMaxWeight(10);
        
        //IKnapsack01Solver solver = new Knapsack01Solver();
        //solver.solve(problem);

        /* II Fixed Item Number Knapsack Problem */
        FixedItemsNumberKnapsackProblem windowProblem = new FixedItemsNumberKnapsackProblem();
        windowProblem.setItems(items);
        windowProblem.setMaxWeight(12);
        windowProblem.setItemsRequiredNumber(6);
        
        //IKnapsack01MultiWeightsSolver mwSolver = new TwoWeightsKnapsackSolver();
        //boolean success = mwSolver.solve(windowProblem);
        
        /* 1st iteration  */
        IKnapsack01MultiWeightsSolver fixedSolver = new FixedItemsNumberKnapsackSolverOpt();
        boolean success = fixedSolver.solve(windowProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(windowProblem));
        System.out.println("Finished: " + success);
        
        /* 2nd iteration  */
        windowProblem.getItems().add(new Item(9,1,3));
        success = fixedSolver.solve(windowProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(windowProblem));
        System.out.println("Finished: " + success);
        
        
    }
    
    
    private static void runSimpleExample2() {
        Item item1 = new Item(1,3,3);
        Item item2 = new Item(2,5,4);
        Item item3 = new Item(3,6,14);
        Item item4 = new Item(4,2,2);
        Item item5 = new Item(5,2,5);
        Item item6 = new Item(6,1,1);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);


        FixedItemsNumberKnapsackProblem problem = new FixedItemsNumberKnapsackProblem();
        problem.setItems(items);
        problem.setMaxWeight(6);
        problem.setItemsRequiredNumber(1);
        
        IKnapsack01MultiWeightsSolver fixedSolver = new FixedItemsNumberKnapsackSolver();
        boolean success = fixedSolver.solve(problem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(problem));
        System.out.println("Finished: " + success);

        
        
    }
    
    private static void runSimpleIntervalExample() {
        Item item1 = new Item(1,3,3);
        Item item2 = new Item(2,5,4);
        Item item3 = new Item(3,6,14);
        Item item4 = new Item(4,2,2);
        Item item5 = new Item(5,2,5);
        Item item6 = new Item(6,1,1);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);

        IntervalItemsNumberKnapsackProblem problem = new IntervalItemsNumberKnapsackProblem();
        problem.setItems(items);
        problem.setMaxWeight(6);
        problem.setMinItemsNumber(2);
        problem.setMaxItemsNumber(4);
        
        IKnapsack01MultiWeightsSolver fixedSolver = new IntervalItemsNumberKnapsackSolver();
        boolean success = fixedSolver.solve(problem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(problem));
        System.out.println("Finished: " + success);

        
        
    }
    
    private static void runSimpleChainExample() {
        Item item1 = new Item(1,4,4);
        Item item2 = new Item(2,5,4);
        Item item3 = new Item(3,3,3);
        Item item4 = new Item(4,2,1);
        Item item5 = new Item(5,1,1);
        Item item6 = new Item(6,8,100);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);

        FixedItemsNumberKnapsackProblemChain chain1 = new FixedItemsNumberKnapsackProblemChain();
        chain1.setItems(items);
        chain1.setMaxWeight(8);
        chain1.setItemsRequiredNumber(2);
        
        FixedItemsNumberKnapsackProblemChain chain2 = new FixedItemsNumberKnapsackProblemChain();
        chain2.setItems(items);
        chain2.setMaxWeight(8);
        chain2.setItemsRequiredNumber(1);
        
        ChainedKnapsackProblem chainedProblem = new ChainedKnapsackProblem();
        chainedProblem.addChain(chain1);
        chainedProblem.addChain(chain2);
        chainedProblem.setMaxWeight(8);
        chainedProblem.setInitValues(new Double[]{0d,0d,0d,null,8d,8d,8d,8d,8d});
        
        ChainKnapsackProblemSolver solver = new ChainKnapsackProblemSolver();
        boolean success = solver.solve(chainedProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(chainedProblem));
        System.out.println("Finished: " + success);

    }
    
    private static void runSimpleChainExample2() {
        Item item1 = new Item(1,4,4);
        Item item2 = new Item(2,5,4);
        Item item3 = new Item(3,3,3);
        Item item4 = new Item(4,2,1);
        Item item5 = new Item(5,1,1);
        Item item6 = new Item(6,8,100);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);

        FixedItemsNumberKnapsackProblemChain chain1 = new FixedItemsNumberKnapsackProblemChain();
        chain1.setItems(items);
        chain1.setMaxWeight(10);
        chain1.setItemsRequiredNumber(3);
        
        KnapsackProblemChain chain2 = new KnapsackProblemChain();
        chain2.setItems(items);
        chain2.setMaxWeight(10);
        
        ChainedKnapsackProblem chainedProblem = new ChainedKnapsackProblem();
        chainedProblem.addChain(chain2);
        chainedProblem.addChain(chain1);
        chainedProblem.setMaxWeight(10);
        //chainedProblem.setInitValues(new Double[]{0d,0d,0d,null,8d,8d,8d,8d,8d});
        
        ChainKnapsackProblemSolver solver = new ChainKnapsackProblemSolver();
        boolean success = solver.solve(chainedProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(chainedProblem));
        System.out.println("Finished: " + success);

    }
    
    private static void runSimpleChainExample3() {
        Item item1 = new Item(1,4,4);
        Item item2 = new Item(2,5,4);
        Item item3 = new Item(3,3,3);
        Item item4 = new Item(4,2,1);
        Item item5 = new Item(5,1,1);
        Item item6 = new Item(6,8,100);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        items.add(item6);

        FixedItemsNumberKnapsackProblemChain chain1 = new FixedItemsNumberKnapsackProblemChain();
        chain1.setItems(items);
        chain1.setMaxWeight(15);
        chain1.setItemsRequiredNumber(3);
        
        KnapsackProblemChain chain2 = new KnapsackProblemChain();
        chain2.setItems(items);
        chain2.setMaxWeight(15);
        
        IntervalItemsNumberKnapsackProblemChain chain3 = new IntervalItemsNumberKnapsackProblemChain();
        chain3.setItems(items);
        chain3.setMaxWeight(15);
        chain3.setMinItemsNumber(2);
        chain3.setMaxItemsNumber(5);
        
        ChainedKnapsackProblem chainedProblem = new ChainedKnapsackProblem();
        chainedProblem.addChain(chain3);
        chainedProblem.addChain(chain2);
        chainedProblem.addChain(chain1);
        chainedProblem.setMaxWeight(15);
        //chainedProblem.setInitValues(new Double[]{0d,0d,0d,null,8d,8d,8d,8d,8d});
        
        ChainKnapsackProblemSolver solver = new ChainKnapsackProblemSolver();
        boolean success = solver.solve(chainedProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(chainedProblem));
        System.out.println("Finished: " + success);

    }
    

    private static void runSimpleGroupExample() {
        Item item1 = new Item(1,30,3);
        Item item2 = new Item(2,50,4);
        Item item3 = new Item(3,60,14);
        Item item4 = new Item(4,20,3);
        Item item5 = new Item(5,20,5);
        Item item6 = new Item(6,10,1);
        Item item7 = new Item(7,10,5);
        Item item8 = new Item(8,73,100);
        
        List<Item> items1 = new ArrayList<>();
        items1.add(item1);
        items1.add(item2);
        items1.add(item3);
        GroupItemKnapsack groupItem1 = new GroupItemKnapsack(1, items1);
        groupItem1.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 1 -20%", null, k -> k > 2 ? 0.75d : k > 1 ? 0.85d : 1d));

        List<Item> items2 = new ArrayList<>();
        items2.add(item4);
        items2.add(item5);
        items2.add(item6);
        GroupItemKnapsack groupItem2 = new GroupItemKnapsack(2, items2);
        groupItem2.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 2 +50% value", k -> k > 1 ? 1.5d : 1d, null));

        List<Item> items3 = new ArrayList<>();
        items3.add(item7);
        items3.add(item8);
        GroupItemKnapsack groupItem3 = new GroupItemKnapsack(3, items3);
        groupItem3.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 3 -10%", null, k -> k > 1 ? 0.96d : 1d));

        List<GroupItem> groupItems = Arrays.asList(groupItem1, groupItem2, groupItem3);

        // 1. Solve group problem
        System.out.println("\n=========== GROUP KANPSACK RESULT ============\n");
        IntervalKnapsackWithGroupsProblem groupProblem = new IntervalKnapsackWithGroupsProblem();
        groupProblem.setGroupItems(groupItems);
        groupProblem.setMaxWeight(100);
        groupProblem.setMinItemsNumber(3);
        groupProblem.setMaxItemsNumber(4);
        
        GroupItemIntervalKnapsackSolver groupSolver = new GroupItemIntervalKnapsackSolver();
        Long startTime = System.nanoTime();
        boolean groupSuccess = groupSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);

        // 2. Solve plain problem for control
        System.out.println("\n=========== PLAIN KANPSACK RESULT ============\n");
        List<Item> allItems = groupProblem.getGroupItems().stream()
                                .flatMap(g -> g.collectInnerSubItems().stream())
                                .collect(Collectors.toList());

        IntervalItemsNumberKnapsackProblem plainProblem = new IntervalKnapsackWithGroupsProblem();
        plainProblem.setItems(allItems);
        plainProblem.setMaxWeight(groupProblem.maxWeight);
        plainProblem.setMinItemsNumber(groupProblem.getMinItemsNumber());
        plainProblem.setMaxItemsNumber(groupProblem.getMaxItemsNumber());
        
        IKnapsack01MultiWeightsSolver fixedSolver = new IntervalItemsNumberKnapsackSolver();
        startTime = System.nanoTime();
        boolean plainSuccess = fixedSolver.solve(plainProblem);
        endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(plainProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);

        // 3. Solve with brute
        System.out.println("\n=========== BRUTE FORCE RESULT ============\n");
        BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
        startTime = System.nanoTime();
        boolean bruteSuccess = bruteSolver.solve(groupProblem);
        endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime) * 1d/1000000);
        System.out.println("\nFinished: " + groupSuccess + " " + plainSuccess + " " + bruteSuccess);
        
    }

    private static void runSimplestGroupExample() {
        Item item1 = new Item(1,28,250);
        Item item2 = new Item(2,25,400);
        Item item3 = new Item(3,42,500);
        Item item4 = new Item(4,30,1000);
        Item item5 = new Item(5,24,250);
        Item item6 = new Item(6,50,400);
        Item item7 = new Item(7,28,500);
        Item item8 = new Item(8,40,1000);
        
        List<Item> items1 = new ArrayList<>();
        items1.add(item1);
        items1.add(item2);
        items1.add(item3);
        items1.add(item4);
        GroupItemKnapsack groupItem1 = new GroupItemKnapsack(1, items1);
        groupItem1.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 1 SALE", null, k -> k > 2 ? 0.75d : k > 1 ? 0.85d : 1d));

        List<Item> items2 = new ArrayList<>();
        items2.add(item5);
        items2.add(item6);
        items2.add(item7);
        items2.add(item8);
        GroupItemKnapsack groupItem2 = new GroupItemKnapsack(2, items2);
        groupItem2.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 2 BONUS PERF", k -> k > 1 ? 1.2d : 1d, null));

        List<GroupItem> groupItems = Arrays.asList(groupItem1, groupItem2);

        // 1. Solve group problem
        System.out.println("\n=========== GROUP KANPSACK RESULT ============\n");
        IntervalKnapsackWithGroupsProblem groupProblem = new IntervalKnapsackWithGroupsProblem();
        groupProblem.setGroupItems(groupItems);
        groupProblem.setMaxWeight(100);
        groupProblem.setMinItemsNumber(3);
        groupProblem.setMaxItemsNumber(3);
        
        GroupItemIntervalKnapsackSolver groupSolver = new GroupItemIntervalKnapsackSolver();
        Long startTime = System.nanoTime();
        boolean groupSuccess = groupSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime)/1000000d);

        

        // 3. Solve with brute
        System.out.println("\n=========== BRUTE FORCE RESULT ============\n");
        BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
        startTime = System.nanoTime();
        boolean bruteSuccess = bruteSolver.solve(groupProblem);
        endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime) * 1d/1000000);
        System.out.println("\nFinished: " + groupSuccess + " " + bruteSuccess);
        
    }


    /** TODO
     * 0. Seems we can't use addition and multiplier group managers together as order of their application in a single hierarchy matters
     * 1. There's a bug on calculating total improved value for hierarchy of multiplier managers as improved value is calculated and added separately for each manager.. need a hierarchy of multiplication
     * 2. So addition-based group managers should work fine
      */
    private static void runSimpleHierarchicalExample() {
        Item item1 = new Item(1,28,250);
        Item item2 = new Item(2,25,400);
        Item item3 = new Item(3,42,500);
        Item item4 = new Item(4,30,1000);
        Item item5 = new Item(5,24,250);
        Item item6 = new Item(6,50,400);
        Item item7 = new Item(7,28,500);
        Item item8 = new Item(8,40,600);
        Item item9 = new Item(9,35,300);

        List<Item> items11 = new ArrayList<>();
        items11.add(item1);
        items11.add(item2);
        items11.add(item3);
        GroupItemKnapsack groupItem11 = new GroupItemKnapsack(11, items11);
        //groupItem11.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 11 Bonus Perf", k -> k > 2 ? 1.9d : k > 1 ? 1.3d : 1d, null));
        groupItem11.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 11 Addition Perf", k -> k > 1 ? (k - 1) * 80d : 0d, null));

        List<Item> items12 = new ArrayList<>();
        items12.add(item4);
        items12.add(item5);
        GroupItemKnapsack groupItem12 = new GroupItemKnapsack(12, items12);
        //groupItem12.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 12 Bonus Perf", k -> k > 1 ? 1.15d : 1d, null));
        groupItem12.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 12 Addition Perf", k -> k > 1 ? (k - 1) * 150d : 0d, null));

        List<GroupItem> groupItems1 = new ArrayList<>();
        groupItems1.add(groupItem11);
        groupItems1.add(groupItem12);
        GroupItemGroupKnapsack groupItem1 = new GroupItemGroupKnapsack(1, groupItems1);
        //groupItem1.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 1 Bonus Perf", k -> k > 1 ? 1.05d : 1d, null));
        groupItem1.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 1 Addition Perf", k -> k > 1 ? (k - 1) * 40d : 0d, null));

        List<Item> items21 = new ArrayList<>();
        items21.add(item6);
        items21.add(item7);
        GroupItemKnapsack groupItem21 = new GroupItemKnapsack(21, items21);
        //groupItem21.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 21 Bonus Perf", k -> k > 1 ? 1.2d : 1d, null));
        groupItem21.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 21 Addition Perf", k -> k > 1 ? (k - 1) * 100d : 0d, null));

        List<Item> items22 = new ArrayList<>();
        items22.add(item8);
        items22.add(item9);
        GroupItemKnapsack groupItem22 = new GroupItemKnapsack(22, items22);
        //groupItem22.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 22 Bonus Perf", k -> k > 0 ? 1.05d : 1d, null));
        groupItem22.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 22 Addition Perf", k -> k > 1 ? (k - 1) * 200d : 0d, null));

        List<GroupItem> groupItems2 = new ArrayList<>();
        groupItems2.add(groupItem21);
        groupItems2.add(groupItem22);
        GroupItemGroupKnapsack groupItem2 = new GroupItemGroupKnapsack(2, groupItems2);
        //groupItem2.setGroupPropertyManager(new QuantityMultiplierWeightGroupManager("Group 2 Bonus Perf", k -> k > 1 ? 1.1d : 1d, null));
        groupItem2.setGroupPropertyManager(new QuantityAdditionWeightGroupManager("Group 2 Addition Perf", k -> k > 1 ? (k - 1) * 50d : 0d, null));

        List<GroupItem> groupItems = Arrays.asList(groupItem1, groupItem2);

        // 1. Solve group problem
        System.out.println("\n=========== GROUP KANPSACK RESULT ============\n");
        IntervalKnapsackWithGroupsProblem groupProblem = new IntervalKnapsackWithGroupsProblem();
        groupProblem.setGroupItems(groupItems);
        groupProblem.setMaxWeight(100);
        groupProblem.setMinItemsNumber(3);
        groupProblem.setMaxItemsNumber(3);

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
        System.out.println("\n=========== BRUTE FORCE RESULT ============\n");
        BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
        startTime = System.nanoTime();
        boolean bruteSuccess = bruteSolver.solve(groupProblem);
        endTime = System.nanoTime();
        System.out.println(KnapsackAnalysis.getSolutionInfo(groupProblem));
        System.out.println("\nTime, ms: " + (endTime - startTime) * 1d/1000000);
        System.out.println("\nFinished: " + groupSuccess + " " + bruteSuccess);

        QuantityMultiplierWeightGroupManager.PRINT_LOGS = true;
        groupProblem.calculateStats();
        QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;

    }

    
    private static void runSimpleMultExample() {
        Item item1 = new Item(1,5,1);
        Item item2 = new Item(2,6,0.9);
        Item item3 = new Item(3,6,0.9);
        Item item4 = new Item(3,1,0.3);
        
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);

        /* Fixed Item Number Knapsack Problem */
        FixedItemsNumberKnapsackProblem windowProblem = new FixedItemsMultiplicativeProblem();
        windowProblem.setItems(items);
        windowProblem.setMaxWeight(10);
        windowProblem.setItemsRequiredNumber(2);
        
        /* 1st iteration  */
        IKnapsack01MultiWeightsSolver fixedSolver = new FixedItemsMultiplicativeSolver();
        //IKnapsack01MultiWeightsSolver fixedSolver = new FixedItemsNumberKnapsackSolver();
        boolean success = fixedSolver.solve(windowProblem);
        System.out.println(KnapsackAnalysis.getSolutionInfo(windowProblem));
        System.out.println("Finished: " + success);
        
    }
}
