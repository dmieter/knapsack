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
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityAdditionWeightGroupManager;

// Greedy heuristic for IntervalKnapsackWithGroupsProblem: optimistic discounts + threshold greedy + swap improvement.
public class GroupItemIntervalKnapsackGreedySolver implements IKnapsack01MultiWeightsSolver {

    private static final int MAX_ITERATIONS = 10;
    private static final double EPS = 1e-9;

    // Maximum number of improving swaps performed by the local-search stage; Integer.MAX_VALUE means
    // "until no single swap improves the solution". 0 disables swaps entirely (pure greedy).
    private int maxSwapIterations = Integer.MAX_VALUE;

    public GroupItemIntervalKnapsackGreedySolver() {
    }

    public GroupItemIntervalKnapsackGreedySolver(int maxSwapIterations) {
        this.maxSwapIterations = maxSwapIterations;
    }

    public void setMaxSwapIterations(int maxSwapIterations) {
        this.maxSwapIterations = maxSwapIterations;
    }

    public int getMaxSwapIterations() {
        return maxSwapIterations;
    }

    public boolean solve(IntervalKnapsackWithGroupsProblem problem) {
        problem.resetSolution();

        List<Item> items = collectLeafItems(problem);
        int minItems = problem.getMinItemsNumber();
        int maxItems = Math.min(problem.getMaxItemsNumber(), items.size());
        int maxWeight = problem.getMaxWeight();

        if (items.size() < minItems) {
            return false;
        }

        // Try every allowed item count in [minItems, maxItems] and keep the best feasible solution.
        List<Item> best = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int requiredItems = minItems; requiredItems <= maxItems; requiredItems++) {
            List<Item> candidate = solveForItemsNumber(problem, items, requiredItems, maxWeight);
            if (candidate == null) {
                continue;
            }
            evaluate(problem, candidate);
            double value = problem.getImprovedTotalValue();
            if (value > bestValue + EPS) {
                bestValue = value;
                best = candidate;
            }
        }

        if (best == null) {
            return false;
        }

        evaluate(problem, best);
        return true;
    }

    // Fixed-point iteration for a fixed item count: start with maximum (optimistic) group
    // discounts/bonuses and monotonically tighten the assumed usage of groups actually selected.
    // Returns a feasible selection, or null if none was found for this item count.
    private List<Item> solveForItemsNumber(IntervalKnapsackWithGroupsProblem problem, List<Item> items,
            int requiredItems, int maxWeight) {
        Map<GroupItem, Integer> assumedCounts = new HashMap<>();
        List<Item> selected = null;

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            selected = greedyAndImprove(problem, items, computeFactors(problem, assumedCounts),
                    requiredItems, maxWeight);

            if (isFeasible(problem, selected)) {
                return selected;
            }

            if (!refineAssumptions(assumedCounts, countSelected(problem, selected))) {
                break;
            }
        }

        return isFeasible(problem, selected) ? selected : null;
    }

    // Monotonically tighten per-group assumptions: observed usage can only lower the assumed count,
    // groups with zero usage keep their previous assumption (never reset back to the optimistic max).
    private boolean refineAssumptions(Map<GroupItem, Integer> assumed, Map<GroupItem, Integer> actual) {
        boolean changed = false;
        for (Map.Entry<GroupItem, Integer> entry : actual.entrySet()) {
            int used = entry.getValue();
            if (used <= 0) {
                continue;
            }
            Integer previous = assumed.get(entry.getKey());
            if (previous == null || used < previous) {
                assumed.put(entry.getKey(), used);
                changed = true;
            }
        }
        return changed;
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
        if (maxSwapIterations <= 0) {
            return selected;
        }

        boolean changed = true;
        int performedSwaps = 0;

        while (changed && performedSwaps < maxSwapIterations) {
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
                    performedSwaps++;
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

    // Per-item {weightFactor, valueFactor, valueAdd} for the given per-group assumed usage; groups
    // absent from counts (never observed) use the maximum optimistic group discounts/bonuses.
    // Multiplicative value boosts accumulate into valueFactor; additive bonuses into valueAdd.
    private Map<Item, double[]> computeFactors(IntervalKnapsackWithGroupsProblem problem, Map<GroupItem, Integer> counts) {
        Map<Item, double[]> factors = new HashMap<>();
        for (GroupItem groupItem : problem.getGroupItems()) {
            accumulateFactors(groupItem, counts, factors);
        }
        return factors;
    }

    // Recursively derive group factors from manager.createItemVariant: multiplicative boosts are
    // multiplied along the tree, additive bonuses are summed as absolute per-item increments.
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
        double valueAdd = 0d;
        if (group.groupPropertyManager != null && rawWeight > 0) {
            // Maximum optimistic assumption for groups that were never observed; otherwise the
            // (monotonically tightened) real usage. Zero usage means the group is not improved.
            int assumedCount = counts != null && counts.containsKey(group) ? counts.get(group) : leaves.size();
            int usedCount = Math.min(assumedCount, leaves.size());
            ItemVariant variant = group.groupPropertyManager.createItemVariant(usedCount, (int) rawWeight, rawValue);
            weightFactor = variant.weight / rawWeight;
            if (group.groupPropertyManager instanceof QuantityAdditionWeightGroupManager) {
                // Additive group bonuses are absolute amounts (not multipliers) and cannot be folded
                // into a per-item factor; distribute the group bonus equally among its items instead.
                if (usedCount > 0) {
                    valueAdd = (variant.value - rawValue) / usedCount;
                }
            } else if (rawValue != 0) {
                valueFactor = variant.value / rawValue;
            }
        }

        for (Item item : leaves) {
            double[] factor = factors.computeIfAbsent(item, key -> new double[] { 1d, 1d, 0d });
            factor[0] *= weightFactor;
            factor[1] *= valueFactor;
            factor[2] += valueAdd;
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
        return factor == null ? item.getValue() : item.getValue() * factor[1] + factor[2];
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
