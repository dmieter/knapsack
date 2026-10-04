package com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.FixedItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IKnapsack01MultiWeightsSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.MultiWeightsKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem.ItemVariant;

// Greedy heuristic for IntervalKnapsackWithGroupsProblem: optimistic discounts + threshold greedy + swap improvement.
public class GroupItemIntervalKnapsackGreedySolver implements IKnapsack01MultiWeightsSolver {

    private static final int MAX_ITERATIONS = 10;
    private static final double EPS = 1e-9;

    public boolean solve(IntervalKnapsackWithGroupsProblem problem) {
        problem.resetSolution();

        List<Item> items = collectLeafItems(problem);
        int requiredItems = problem.getMaxItemsNumber();
        int maxWeight = problem.getMaxWeight();

        if (items.size() < requiredItems) {
            return false;
        }

        // Step 1 + 2: greedy with maximum (optimistic) group discounts/bonuses, then swap improvement.
        List<Item> selected = greedyAndImprove(problem, items, computeFactors(problem, null), requiredItems, maxWeight);
        boolean feasible = isFeasible(problem, selected);

        // Step 3: retry with real discounts for groups that contributed to the selection.
        for (int iteration = 0; !feasible && iteration < MAX_ITERATIONS; iteration++) {
            List<Item> next = greedyAndImprove(problem, items,
                    computeFactors(problem, countSelected(problem, selected)), requiredItems, maxWeight);
            if (isFeasible(problem, next)) {
                selected = next;
                feasible = true;
            } else if (next.equals(selected)) {
                break;
            } else {
                selected = next;
            }
        }

        if (!feasible) {
            return false;
        }

        return isFeasible(problem, selected);
    }

    // Run greedy construction and immediately improve the intermediate solution with swaps.
    private List<Item> greedyAndImprove(IntervalKnapsackWithGroupsProblem problem, List<Item> items,
            Map<Item, double[]> factors, int requiredItems, int maxWeight) {
        List<Item> selection = greedy(items, factors, requiredItems, maxWeight);
        return improveBySwaps(problem, items, selection, maxWeight);
    }

    // Replace the worst selected items by unselected ones with the best real value that still fit.
    private List<Item> improveBySwaps(IntervalKnapsackWithGroupsProblem problem, List<Item> items,
            List<Item> selection, int maxWeight) {

        List<Item> selected = new ArrayList<>(selection);
        boolean changed = true;

        while (changed) {
            changed = false;

            List<Item> removable = new ArrayList<>(selected);
            removable.sort(Comparator.comparingDouble(this::efficiency));

            evaluate(problem, selected);
            double currentValue = problem.getImprovedTotalValue();

            for (Item out : removable) {
                Item bestIn = null;
                double bestValue = currentValue;

                for (Item candidate : items) {
                    if (selected.contains(candidate)) {
                        continue;
                    }
                    List<Item> trial = new ArrayList<>(selected);
                    trial.remove(out);
                    trial.add(candidate);

                    int weight = evaluate(problem, trial);
                    double value = problem.getImprovedTotalValue();
                    if (weight <= maxWeight && value > bestValue + EPS) {
                        bestValue = value;
                        bestIn = candidate;
                    }
                }

                if (bestIn != null) {
                    selected.remove(out);
                    selected.add(bestIn);
                    changed = true;
                    break;
                }
            }
        }

        return selected;
    }

    // Take n items with effective weight below C/n, preferring highest effective value.
    private List<Item> greedy(List<Item> items, Map<Item, double[]> factors, int requiredItems, int maxWeight) {
        double threshold = (double) maxWeight / requiredItems;

        List<Item> eligible = new ArrayList<>();
        for (Item item : items) {
            if (effectiveWeight(item, factors) < threshold) {
                eligible.add(item);
            }
        }
        eligible.sort(Comparator.comparingDouble((Item item) -> effectiveValue(item, factors)).reversed());

        List<Item> selected = new ArrayList<>();
        for (Item item : eligible) {
            if (selected.size() >= requiredItems) {
                break;
            }
            selected.add(item);
        }

        // Fill up to n with the lightest remaining items if the threshold was too strict.
        if (selected.size() < requiredItems) {
            List<Item> remaining = new ArrayList<>();
            for (Item item : items) {
                if (!selected.contains(item)) {
                    remaining.add(item);
                }
            }
            remaining.sort(Comparator.comparingDouble(item -> effectiveWeight(item, factors)));
            for (Item item : remaining) {
                if (selected.size() >= requiredItems) {
                    break;
                }
                selected.add(item);
            }
        }

        return selected;
    }

    // Step 1: per-item weight/value factors, using maximum group discounts/bonuses when counts is null.
    private Map<Item, double[]> computeFactors(IntervalKnapsackWithGroupsProblem problem, Map<GroupItem, Integer> counts) {
        Map<Item, double[]> factors = new HashMap<>();
        for (GroupItem groupItem : problem.getGroupItems()) {
            accumulateFactors(groupItem, counts, factors);
        }
        return factors;
    }

    // Recursively derive group factors from manager.createItemVariant and multiply them along the tree.
    private void accumulateFactors(GroupItem group, Map<GroupItem, Integer> counts, Map<Item, double[]> factors) {
        List<Item> leaves = group.collectInnerSubItems();
        if (leaves.isEmpty()) {
            return;
        }

        double rawWeight = 0;
        double rawValue = 0;
        for (Item item : leaves) {
            rawWeight += item.getWeight();
            rawValue += item.getValue();
        }

        double weightFactor = 1d;
        double valueFactor = 1d;
        if (group.groupPropertyManager != null && rawWeight > 0) {
            int assumedCount = leaves.size();
            if (counts != null) {
                Integer selectedCount = counts.get(group);
                if (selectedCount != null && selectedCount > 0) {
                    assumedCount = selectedCount;
                }
            }
            ItemVariant variant = group.groupPropertyManager.createItemVariant(
                    Math.min(assumedCount, leaves.size()), (int) rawWeight, rawValue);
            weightFactor = variant.weight / rawWeight;
            if (rawValue != 0) {
                valueFactor = variant.value / rawValue;
            }
        }

        for (Item item : leaves) {
            double[] factor = factors.computeIfAbsent(item, key -> new double[] { 1d, 1d });
            factor[0] *= weightFactor;
            factor[1] *= valueFactor;
        }

        if (group instanceof GroupItemGroupKnapsack) {
            for (GroupItem subGroup : ((GroupItemGroupKnapsack) group).subGroupItems) {
                accumulateFactors(subGroup, counts, factors);
            }
        }
    }

    // Count selected leaves per group, used to apply real discounts on the retry.
    private Map<GroupItem, Integer> countSelected(IntervalKnapsackWithGroupsProblem problem, List<Item> selected) {
        Map<GroupItem, Integer> counts = new HashMap<>();
        for (GroupItem groupItem : problem.getGroupItems()) {
            countSelected(groupItem, selected, counts);
        }
        return counts;
    }

    private int countSelected(GroupItem group, List<Item> selected, Map<GroupItem, Integer> counts) {
        int count = 0;
        for (Item item : group.collectInnerSubItems()) {
            if (selected.contains(item)) {
                count++;
            }
        }
        counts.put(group, count);

        if (group instanceof GroupItemGroupKnapsack) {
            for (GroupItem subGroup : ((GroupItemGroupKnapsack) group).subGroupItems) {
                countSelected(subGroup, selected, counts);
            }
        }
        return count;
    }

    // Step 3: real feasibility check via calculateStats, where group property managers are applied.
    private boolean isFeasible(IntervalKnapsackWithGroupsProblem problem, List<Item> selected) {
        evaluate(problem, selected);
        return KnapsackAnalysis.validateSolution(problem);
    }

    // Set the selection and recompute real improved totals; returns real improved weight.
    private int evaluate(IntervalKnapsackWithGroupsProblem problem, List<Item> selection) {
        problem.setSelectedItems(new ArrayList<>(selection));
        problem.calculateStats();
        return problem.getImprovedTotalWeight();
    }

    private double effectiveWeight(Item item, Map<Item, double[]> factors) {
        double[] factor = factors.get(item);
        return factor == null ? item.getWeight() : item.getWeight() * factor[0];
    }

    private double effectiveValue(Item item, Map<Item, double[]> factors) {
        double[] factor = factors.get(item);
        return factor == null ? item.getValue() : item.getValue() * factor[1];
    }

    private double efficiency(Item item) {
        if (item.getWeight() <= 0) {
            return item.getValue() > 0 ? Double.MAX_VALUE : 0d;
        }
        return item.getValue() / item.getWeight();
    }

    // Flatten all leaf items of the group tree, independent of any DP solver.
    private List<Item> collectLeafItems(IntervalKnapsackWithGroupsProblem problem) {
        List<Item> items = new ArrayList<>();
        for (GroupItem groupItem : problem.getGroupItems()) {
            items.addAll(groupItem.collectInnerSubItems());
        }
        return items;
    }

    @Override
    public boolean solve(FixedItemsNumberKnapsackProblem problem) {
        if (problem instanceof IntervalKnapsackWithGroupsProblem) {
            return solve((IntervalKnapsackWithGroupsProblem) problem);
        }
        throw new UnsupportedOperationException(
                "GroupItemIntervalKnapsackGreedySolver supports only IntervalKnapsackWithGroupsProblem");
    }

    @Override
    public boolean solve(MultiWeightsKnapsackProblem problem) {
        throw new UnsupportedOperationException(
                "GroupItemIntervalKnapsackGreedySolver supports only IntervalKnapsackWithGroupsProblem");
    }

    @Override
    public void flush() {
    }
}
