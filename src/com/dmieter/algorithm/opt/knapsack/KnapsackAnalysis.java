package com.dmieter.algorithm.opt.knapsack;

import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ChainedKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.chain.ProblemChain;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.ItemMultiWeighted;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.MultiWeightsKnapsackProblem;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author dmieter
 */
public class KnapsackAnalysis {

    public static boolean validateSolution(KnapsackProblem problem) {

        if (!validateDuplicatedItems(problem)) {
            return false;
        }

        if (!validateWeights(problem)) {
            return false;
        }

        return true;

    }

    public static boolean validateSolution(MultiWeightsKnapsackProblem problem) {

        if (!validateDuplicatedItems(problem)) {
            return false;
        }

        if (!validateWeights(problem)) {
            return false;
        }

        return true;

    }

    public static boolean validateSolution(FixedItemsNumberKnapsackProblem problem) {

        if (!validateDuplicatedItems(problem)) {
            System.out.println("duplicating items check failed");
            return false;
        }

        if (!validateWeights(problem)) {
            System.out.println("weight check is failed");
            return false;
        }

        if (!validateItemsNumber(problem)) {
            System.out.println("items number check is failed");
            return false;
        }

        return true;

    }

    public static boolean validateDuplicatedItems(KnapsackProblem problem) {
        Set<Integer> resultingItems = new HashSet<>(problem.getSelectedItems().size());
        for (Item item : problem.getSelectedItems()) {
            if (!resultingItems.contains(item.id)) {
                resultingItems.add(item.id);
            } else {
                return false;
            }
        }

        return true;
    }

    public static boolean validateWeights(KnapsackProblem problem) {
        return validateWeights(problem.getMaxWeight(), problem.getSelectedItems());
    }

    protected static boolean validateWeights(int maxWeight, List<Item> items) {
        int sumWeight = 0;
        for (Item item : items) {
            sumWeight += item.getWeight();
        }

        if (sumWeight > maxWeight) {
            return false;
        }

        return true;
    }

    public static boolean validateWeights(MultiWeightsKnapsackProblem problem) {

        int weightsNum = problem.getMaxWeights().size();
        int[] sumWeights = new int[weightsNum];

        for (Item item : problem.getSelectedItems()) {
            ItemMultiWeighted itemMW = (ItemMultiWeighted) item;
            for (int i = 0; i < weightsNum; i++) {
                sumWeights[i] += itemMW.getWeight(i);
            }
        }

        for (int i = 0; i < weightsNum; i++) {
            if (sumWeights[i] > problem.getMaxWeight(i)) {
                return false;
            }
        }

        return true;
    }

    public static boolean validateItemsNumber(FixedItemsNumberKnapsackProblem problem) {

        if (problem instanceof IntervalItemsNumberKnapsackProblem) {
            IntervalItemsNumberKnapsackProblem intervalProblem = (IntervalItemsNumberKnapsackProblem) problem;

            if (problem.getSelectedItems().size() < intervalProblem.getMinItemsNumber()
                    || problem.getSelectedItems().size() > intervalProblem.getMaxItemsNumber()) {
                return false;
            } else {
                return true;
            }
            
        }

        if (problem.getItemsRequiredNumber() != problem.getSelectedItems().size()) {
            return false;
        }

        return true;
    }


    public static String getSolutionInfo(KnapsackProblem problem) {
        StringBuilder result = new StringBuilder();
        problem.calculateStats();
        result.append("Problem solution value: ").append(problem.getTotalValue())
                .append(", weight: ").append(problem.getTotalWeight())
                .append("\nSelected items are the following:");

        for (Item item : problem.getSelectedItems()) {
            result.append("\n Item number ").append(item.id).append(" value: ").append(item.getValue())
                    .append(" weight:").append(item.getWeight());
        }

        return result.toString();
    }

    public static String getSolutionInfo(ChainedKnapsackProblem problem) {
        StringBuilder result = new StringBuilder();

        result.append("Whole Problem solution value: ").append(problem.getTotalValue())
                .append(", weight: ").append(problem.getTotalWeight());

        int i = 0;
        for (ProblemChain chain : problem.getProblemChains()) {
            chain.calculateStats();
            result.append("\nProblem chain" + i + " solution value: ").append(chain.getTotalValue())
                    .append(", weight: ").append(chain.getTotalWeight())
                    .append("\nSelected items are the following:");

            for (Item item : chain.getSelectedItems()) {
                result.append("\n Item number ").append(item.id).append(" value: ").append(item.getValue())
                        .append(" weight:").append(item.getWeight());
            }

            i++;
        }

        return result.toString();
    }
}
