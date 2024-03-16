package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;

public class BruteForceIntervalKnapsackSolver implements IKnapsack01MultiWeightsSolver {

    SolutionStats solution = new SolutionStats(-1d, 0, Collections.emptyList(), false);

    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        
        /* 0. Convering problem to Interval problem */
        IntervalItemsNumberKnapsackProblem intervalProblem = (IntervalItemsNumberKnapsackProblem) problem;
        

        checkNextPermutation(problem.getItems(), new ArrayList<>(), intervalProblem);


        problem.setSelectedItems(solution.selectedItems);
        problem.calculateStats();
        return solution.isFeasible;

    }

    protected void checkNextPermutation(List<Item> inputList, List<Item> currentCombination, IntervalItemsNumberKnapsackProblem intervalProblem) {

        if(currentCombination.size() <= intervalProblem.getMaxItemsNumber() && currentCombination.size() >= intervalProblem.getMinItemsNumber()){
            //System.out.println(currentCombination.stream().map(i -> i.id).collect(Collectors.toSet()));
            analyzeSolution(currentCombination, intervalProblem);
        }

        if (inputList.isEmpty() || currentCombination.size() >= intervalProblem.getMaxItemsNumber()) {
            return; // nothing to add
        }

        Item firstElement = inputList.get(0);
        List<Item> remainingElements = inputList.subList(1, inputList.size());

        // add the first element to the current combination
        currentCombination.add(firstElement);
        checkNextPermutation(remainingElements, currentCombination, intervalProblem);

        // remove the first element from the current combination
        currentCombination.remove(currentCombination.size() - 1);

        // recursively generate combinations for the remaining elements
        checkNextPermutation(remainingElements, currentCombination, intervalProblem);
    }

    private void analyzeSolution(List<Item> currentCombination, IntervalItemsNumberKnapsackProblem intervalProblem) {
        intervalProblem.setSelectedItems(currentCombination);
        intervalProblem.calculateStats();

        if(KnapsackAnalysis.validateSolution(intervalProblem)) {
            SolutionStats currentSolution;
            if(intervalProblem instanceof IntervalKnapsackWithGroupsProblem) {
                IntervalKnapsackWithGroupsProblem groupProblem = (IntervalKnapsackWithGroupsProblem) intervalProblem;
                currentSolution = new SolutionStats(groupProblem.getImprovedTotalValue(), groupProblem.getImprovedTotalWeight(), currentCombination, true);
            } else {
                currentSolution = new SolutionStats(intervalProblem.getTotalValue(), intervalProblem.getTotalWeight(), currentCombination, true);
            }

            if(!solution.isFeasible || solution.value < currentSolution.value) {
                solution = currentSolution;
            }

        }
    }

    @Override
    public boolean solve(MultiWeightsKnapsackProblem problem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'solve MultiWeightsKnapsackProblem'");
    }

    @Override
    public void flush() {
        solution = new SolutionStats(-1d, 0, Collections.emptyList(), false);
    }

    public class SolutionStats {

        public SolutionStats(Double value, Integer weight, List<Item> items, boolean isFeasible) {
            this.value = value;
            this.weight = weight;
            this.selectedItems = new ArrayList<Item>(items);
            this.isFeasible = isFeasible;
        }

        boolean isFeasible;
        Double value;
        Integer weight;
        List<Item> selectedItems;
    }
}
