
package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.KnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.IKnapsack01Solver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.Knapsack01Solver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author dmieter
 */
public class TwoWeightsKnapsackSolver implements IKnapsack01MultiWeightsSolver{

    @Override
    public boolean solve(MultiWeightsKnapsackProblem problem) {
        if(problem.getMaxWeights().size() != 2){
            throw new UnsupportedOperationException("Supporting knapsack with two weights only!");
        }
        
        KnapsackProblem commonProblem = convertToSingleCostraintProblem(problem);
        
        IKnapsack01Solver commonSolver = new Knapsack01Solver();
        if(!commonSolver.solve(commonProblem)){
            return false;
        }
        
        decodeSolution(problem, commonProblem);
        
        return KnapsackAnalysis.validateSolution(problem);
    }

    protected KnapsackProblem convertToSingleCostraintProblem(MultiWeightsKnapsackProblem problem) {
        int lambda0 = calculateLambda(problem, 0);
        int lambda1 = calculateLambda(problem, 1);
        
        KnapsackProblem commonProblem = new KnapsackProblem();
        commonProblem.setMaxWeight(problem.getMaxWeight(1)+problem.getMaxWeight(0)*lambda1);
        
        List<Item> commonItems = new ArrayList<>(problem.getItems().size());

        for(int i = 0; i < problem.getItems().size(); i++){
            ItemMultiWeighted itemMV = (ItemMultiWeighted)problem.getItem(i);
            Item newItem = new Item(i, itemMV.getWeight(1) + itemMV.getWeight(0)*lambda1, itemMV.getValue());
            commonItems.add(newItem);
        }
        
        commonProblem.setItems(commonItems);
        
        return commonProblem;
    }

    protected void decodeSolution(KnapsackProblem problem, KnapsackProblem refProblem) {
        for(Item refItem : refProblem.getSelectedItems()){
            Item item = problem.getItem(refItem.id);
            problem.getSelectedItems().add(item);
        }
    }

    protected int calculateLambda(MultiWeightsKnapsackProblem problem, int baseWeightNum) {
        
        int maxWeight = problem.getMaxWeight(baseWeightNum);
        int lambdaSum = -maxWeight;
        
        for(Item item : problem.getItems()){
            ItemMultiWeighted itemMW = (ItemMultiWeighted)item;
            lambdaSum += itemMW.getWeight(baseWeightNum);
        }
        
        return Math.max(maxWeight, lambdaSum)+1;
    }

    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        MultiWeightsKnapsackProblem mwProblem = convertToMultiWeightsProblem(problem);
        if(!solve(mwProblem)){
            return false;
        }
        decodeSolution(problem, mwProblem);
        
        return KnapsackAnalysis.validateSolution(problem);
    }

    private MultiWeightsKnapsackProblem convertToMultiWeightsProblem(FixedItemsNumberKnapsackProblem problem) {
        MultiWeightsKnapsackProblem mwProblem = new MultiWeightsKnapsackProblem();
        
        List<Integer> maxWeights = new ArrayList<>(2);
        maxWeights.add(problem.getMaxWeight());
        maxWeights.add(problem.getItemsRequiredNumber());
        mwProblem.setMaxWeights(maxWeights);
        
        List<Item> mwItems = new ArrayList<>(problem.getItems().size());
        for(int i = 0; i < problem.getItems().size(); i++){
            Item item = problem.getItem(i);
            List<Integer> weights = Arrays.asList(item.getWeight(), 1);
            ItemMultiWeighted mwItem = new ItemMultiWeighted(i, weights, item.getValue());
            mwItems.add(mwItem);
        }
        mwProblem.setItems(mwItems);
        
        return mwProblem;
    }
    
    @Override
    public void flush() {
        // no variables to flush
    }
    
}
