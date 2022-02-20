package com.dmieter.algorithm.opt.knapsack.knapsack01.chain;

import java.util.ListIterator;

/**
 *
 * @author emelyanov
 */
public class ChainKnapsackProblemSolver {

    public boolean solve(ChainedKnapsackProblem chainedProblem) {

        Double[] interChainValues = new Double[chainedProblem.getMaxWeight()+1];
        if(chainedProblem.getInitValues() != null){
            interChainValues = chainedProblem.getInitValues();
        }else{
            for (int w = 0; w <= chainedProblem.getMaxWeight(); w++) {
                interChainValues[w] = 0d;
            }
        }

        /* chained forward propagation */
        for (ProblemChain problem : chainedProblem.getProblemChains()) {

            /* provide init */
            Double[] initValues = new Double[chainedProblem.getMaxWeight()+1];
            for (int w = 0; w <= chainedProblem.getMaxWeight(); w++) {
                initValues[w] = interChainValues[w];
            }
            problem.setInitValues(initValues);

            ChainSolver solver = KnapsackSolverHelp.getSolver(problem);

            /* forward induction for current chain */
            if (!solver.performForwardPropagation(problem)) {
                return false;
            }
            KnapsackSolverHelp.describeValues(problem.getOutputValues(), chainedProblem.getMaxWeight());

            /* getting interchain values for the next chain */
            interChainValues = new Double[chainedProblem.getMaxWeight()+1];
            Double[] outValues = problem.getOutputValues();
            for (int w = 0; w <= chainedProblem.getMaxWeight(); w++) {
                interChainValues[w] = outValues[w];
            }
        }

        /* in future we should check.. if some chains may have apriori smaller budget */
        /* 1. Finding a tight solution */
        int tightWeight = chainedProblem.getMaxWeight();
        Double[] outputValues = chainedProblem.getProblemChains().get(chainedProblem.getProblemChains().size() - 1).getOutputValues();
        
        for (int w = tightWeight; w > 0; w--) {
            double curWeightValue = outputValues[w];
            double nextWeightValue = outputValues[w-1];

            if (nextWeightValue < curWeightValue) {
                tightWeight = w;
                break;
            }
        }
        
        /* result stats variables */
        int solutionWeight = 0;
        Double solutionValue = 0d;
        
        
        /* chained backward induction (backward cycle) */
        for (ListIterator<ProblemChain> it = chainedProblem.getProblemChains()
                .listIterator(chainedProblem.getProblemChains().size()); it.hasPrevious();) {
            ProblemChain problem = it.previous();

            ChainSolver solver = KnapsackSolverHelp.getSolver(problem);
            System.out.println("Tight Weight: " + tightWeight);
            if (!solver.performBackwardInduction(problem, tightWeight)) {
                return false;
            }

            tightWeight -= problem.getTotalWeight();
            tightWeight = calculateTightWeight(problem.getInitValues(), tightWeight);
            
            solutionWeight += problem.getTotalWeight();
            solutionValue += problem.getTotalValue();
        }
        
        if(chainedProblem.getInitValues() != null){
            solutionWeight += tightWeight;
            solutionValue += chainedProblem.getInitValues()[tightWeight];
        }

        chainedProblem.totalValue = solutionValue;
        chainedProblem.totalWeight = solutionWeight;
        
        return true;
    }
    
     protected int calculateTightWeight(Double[] iterChainValues, int weight) {
        int tightWeight = weight;

        for (int w = weight; w > 0; w--) {

            /* if there's no solution with less weight then current weight is tight */
            if (iterChainValues[w - 1] == null) {
                tightWeight = w;
                break;
            }

            double curWeightValue = iterChainValues[w];
            double nextWeightValue = iterChainValues[w - 1];


            /* if solution with less weight is smaller then current weight is tight */
            if (nextWeightValue < curWeightValue) {
                tightWeight = w;
                break;
            }
        }

        return tightWeight;
    }
}
