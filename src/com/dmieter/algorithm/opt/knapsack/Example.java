
package com.dmieter.algorithm.opt.knapsack;

import com.dmieter.algorithm.opt.knapsack.knapsack01.IKnapsack01Solver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.Knapsack01Solver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ChainKnapsackProblemSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ChainedKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.FixedItemsNumberKnapsackChainSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.FixedItemsNumberKnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.IntervalItemsNumberKnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.KnapsackProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackSolverOpt;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative.FixedItemsMultiplicativeProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.multiplicative.FixedItemsMultiplicativeSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IKnapsack01MultiWeightsSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.TwoWeightsKnapsackSolver;
import java.util.ArrayList;
import java.util.List;

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
        runSimpleChainExample3();
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
