package org.dmieter.knapsack.group.test;

import org.dmieter.stat.NamedStats;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.KnapsackAnalysis;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.BruteForceIntervalKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IKnapsack01MultiWeightsSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.IntervalItemsNumberKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemIntervalKnapsackSolver;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.manager.QuantityMultiplierWeightGroupManager;
import com.google.ortools.constraintsolver.SolverParameters;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;

public class ExperimentSeriesGenerator {
    
    // Global standard parameters based on generateSimpleTestProblem as static constants
    private static final int REQUIRED_ITEMS = 10; // Common variable for both min and max
    
    private static final int NUM_VENDORS = 3;
    private static final int DATA_CENTERS_PER_VENDOR = 3; // Common variable for both min and max
    private static final int VMS_PER_DC = REQUIRED_ITEMS; // Each DC has at least REQUIRED_ITEMS VMs
    private static final int MIN_VM_COST = 4;
    private static final int MAX_VM_COST = 32;
    private static final int MIN_VM_VALUE = 3;
    private static final int MAX_VM_VALUE = 20;
    private static final int BUDGET_COEF = 14;  // so we can not afford ALL REQUIRED_ITEMS max cost items even with 50% discount
    private static final int BUDGET = REQUIRED_ITEMS * BUDGET_COEF; // so we can not afford ALL REQUIRED_ITEMS max cost items even with 50% discount
    private static final boolean USE_HOMOGENEOUS_VMS = false;
    private static final int[] VENDOR_DISCOUNT_THRESHOLDS = new int[]{5, 8, 10};
    private static final int[] VENDOR_DISCOUNTS = new int[]{25, 50};
    private static final int[] DC_BONUS_THRESHOLDS = new int[]{3, 4, 5, 6, 7, 8};
    private static final int[] DC_BONUSES = new int[]{3, 4, 5};
    private static final Double COST_VALUE_CORRELATION = 0.3;
    
    private static final List<Integer> DATACENTERS_PER_VENDOR_VALUES = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static final List<Integer> BUDGET_VALUES = Arrays.asList(60, 80, 100, 150, 200, 250, 300, 400);
    private static final List<Integer> VMS_PER_DC_VALUES = Arrays.asList(5, 10, 15, 20, 25, 30, 40, 50);
    private static final List<Integer> VENDORS_VALUES = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static final List<Integer> CORRELATION_VALUES = Arrays.asList(0, 2, 4, 6, 8, 10, -1);
    private static final List<Integer> REQUIRED_VMS_VALUES = Arrays.asList(5, 10, 11, 12, 15, 20, 30, 40, 50, 60);
    
    private static long globalSeedCounter = 100000L; // Starting seed for reproducibility
    private static final int EXPERIMENT_REPETITIONS_COUNT = 300;
    private TestProblemGenerator generator = new TestProblemGenerator();
    
    private boolean startGKA = true;
    private boolean startKnapsack = true;
    private boolean startSat = true;
    private boolean startBF = false;

    private Double SAT_MAX_WAIT_TIME_SECONDS = 11d;
    private int SAT_TURN_OFF_TIME_MS = 10000;
    

    private NamedStats runtimeStats = new NamedStats("RUNTIME stats");
    private NamedStats valueStats = new NamedStats("VALUE stats");
    private NamedStats costStats = new NamedStats("COST stats");
    private NamedStats successStats = new NamedStats("SUCCESS stats");


    public void generateSeriesWithVaryingDataCenters() {
        
        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingDataCenters, DATACENTERS_PER_VENDOR_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();

        for (int currentDataCentersPerVendor : DATACENTERS_PER_VENDOR_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + currentDataCentersPerVendor + " DC: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingDataCenters(currentSeed, currentDataCentersPerVendor), currentDataCentersPerVendor)) {
                    System.out.println("EXPERIMENT WITH " + currentDataCentersPerVendor + " TIMED OUT");
                    break;  
                }  
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH DATACENTERS_PER_VENDOR_VALUES " + DATACENTERS_PER_VENDOR_VALUES);
    }

     public void generateSeriesWithVaryingBudgets() {
        
        // in this series we want to explore all budgets without turning off the SAT solver
        SAT_MAX_WAIT_TIME_SECONDS = 6d;

        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingBudget, BUDGET_VALUES.get(3));
        // Clear stats after warm-up
        clearStats();
        
        for (int budget : BUDGET_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + budget + " budget: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingBudget(currentSeed, budget), budget)) {
                    System.out.println("EXPERIMENT WITH " + budget + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH BUDGET_VALUES " + BUDGET_VALUES);

        SAT_MAX_WAIT_TIME_SECONDS = 11d;
    }


    public void generateSeriesWithVaryingVMs() {
        
        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingVMs, VMS_PER_DC_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();
        
        for (int vms : VMS_PER_DC_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + vms + " VMs: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingVMs(currentSeed, vms), vms)) {
                    System.out.println("EXPERIMENT for " + vms + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH VMS_PER_DC_VALUES " + VMS_PER_DC_VALUES);
    }

    public void generateSeriesWithVaryingVendors() {
        
        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingVendors, VENDORS_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();
        
        for (int vendors : VENDORS_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + vendors + " Vendors: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingVendors(currentSeed, vendors), vendors)) {
                    System.out.println("EXPERIMENT for " + vendors + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH VENDORS_VALUES " + VENDORS_VALUES);
    }

    public void generateSeriesWithVaryingCorrelation() {
        
        // in this series we want to explore all budgets without turning off the SAT solver
        SAT_MAX_WAIT_TIME_SECONDS = 3d;

        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingCorrellation, CORRELATION_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();
        
        for (int correlation : CORRELATION_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + correlation/10d + " correlation: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingCorrellation(currentSeed, correlation), correlation)) {
                    System.out.println("EXPERIMENT for " + correlation/10d + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH CORRELATION_VALUES " + CORRELATION_VALUES);

        SAT_MAX_WAIT_TIME_SECONDS = 11d;
    }

    public void generateSeriesWithVaryingRequestedVms() {
        
        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingRequestedVms, REQUIRED_VMS_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();
        
        for (int requestedVms : REQUIRED_VMS_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + requestedVms + " requestedVms: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingRequestedVms(currentSeed, requestedVms), requestedVms)) {
                    System.out.println("EXPERIMENT for " + requestedVms + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH REQUIRED_VMS_VALUES " + REQUIRED_VMS_VALUES);
    }

    public void generateSeriesWithVaryingRequestedVmsExpanding() {
        
        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingRequestedVms, REQUIRED_VMS_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();
        
        for (int requestedVms : REQUIRED_VMS_VALUES) {

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + requestedVms + " requestedVms: " + (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingRequestedVms(currentSeed, requestedVms, true), requestedVms)) {
                    System.out.println("EXPERIMENT for " + requestedVms + " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH REQUIRED_VMS_VALUES " + REQUIRED_VMS_VALUES + " AND EXPANDING DCs");
    }


    public void generateSeriesWithVaryingRequestedVmsExtended() {

        // Perform warm-up to heat Java caches/pipelines
        performWarmup(this::generateStandardProblemWithVaryingRequestedVms, REQUIRED_VMS_VALUES.get(0));
        // Clear stats after warm-up
        clearStats();

        startSat = false;

        int requestedVms = 50;
        List<Integer> dcValues =      Arrays.asList(1, 2, 3, 5, 8, 10, 14);
        List<Integer> vendorsValues = Arrays.asList(1, 2, 3, 5, 8, 10, 12);
        
        for (int j = 0; j < 7; j++) {
            
            int dcs = dcValues.get(j);
            int vendors = vendorsValues.get(j);
            int vms = ((Double) Math.ceil(500d/(dcs*vendors))).intValue();

            for(int i = 0; i < EXPERIMENT_REPETITIONS_COUNT; i++) {
                long currentSeed = globalSeedCounter++;

                System.out.println("\n ============= " + dcs + " dcs, " + vendors + " vendors, " + vms + " vms, "+ (i+1) +" iteration ==============\n");

                if(!runAllAlgorithms(() -> generateStandardProblemWithVaryingRequestedVmsExtended(currentSeed, requestedVms, vms, dcs, vendors), vms)) {
                    System.out.println("EXPERIMENT for " + dcs + " dcs, " + vendors + " vendors, " + vms + " vms, "+  " TIMED OUT");
                    break;  
                }
            }
        }

        printStats();
        System.out.println("FINISHED EXPERIMENT WITH " + dcValues + " dcs, " + vendorsValues + " vendors and 500 total vms");

    }

    private boolean runAllAlgorithms(Supplier<IntervalKnapsackWithGroupsProblem> problemSupplier, int variable) {
        if(startGKA) {
            Double gkaRuntime = solveGKA(problemSupplier.get(), variable);
            if(gkaRuntime >= 8000) {
                System.out.println("GKA TIMEOUT");
                startGKA = false;                        
            }   
        }
        
        if(startSat) {
            Double satRuntime = solveSat(problemSupplier.get(), variable);
            if(satRuntime >= SAT_TURN_OFF_TIME_MS) {
                System.out.println("SAT TIMEOUT");
                startSat = false;                        
            }   
        }

        if(startBF) {
            Double bfRuntime = solveBruteForce(problemSupplier.get(), variable);
            if(bfRuntime >= 3000) {
                System.out.println("BF TIMEOUT");
                startBF = false;                        
            }   
        }

        if(startKnapsack) {
            Double knRuntime = solveSingleKnapsack(problemSupplier.get(), variable);
            if(knRuntime >= 3000) {
                System.out.println("KNAPSACK TIMEOUT");
                startKnapsack = false;                        
            }   
        }

        return startGKA || startSat || startBF || startKnapsack;
    }

    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingDataCenters(long seed, int dataCentersPerVendor) {

        return generator.generateHierarchicalTestProblem(
                NUM_VENDORS,
                dataCentersPerVendor, // Using the input variable
                dataCentersPerVendor, // Same value for max
                VMS_PER_DC, // Same value for min and max
                VMS_PER_DC, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                BUDGET,
                REQUIRED_ITEMS, // Same value for min
                REQUIRED_ITEMS, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }


    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingBudget(long seed, int budget) {

        return generator.generateHierarchicalTestProblem(
                NUM_VENDORS,
                DATA_CENTERS_PER_VENDOR, // Using the input variable
                DATA_CENTERS_PER_VENDOR, // Same value for max
                VMS_PER_DC, // Same value for min and max
                VMS_PER_DC, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                budget,
                REQUIRED_ITEMS, // Same value for min
                REQUIRED_ITEMS, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }


    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingVMs(long seed, int vms) {

        return generator.generateHierarchicalTestProblem(
                NUM_VENDORS,
                DATA_CENTERS_PER_VENDOR, // Using the input variable
                DATA_CENTERS_PER_VENDOR, // Same value for max
                vms, // Same value for min and max
                vms, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                BUDGET,
                REQUIRED_ITEMS, // Same value for min
                REQUIRED_ITEMS, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }

    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingVendors(long seed, int vendors) {

        return generator.generateHierarchicalTestProblem(
                vendors,
                DATA_CENTERS_PER_VENDOR, // Using the input variable
                DATA_CENTERS_PER_VENDOR, // Same value for max
                VMS_PER_DC, // Same value for min and max
                VMS_PER_DC, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                BUDGET,
                REQUIRED_ITEMS, // Same value for min
                REQUIRED_ITEMS, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }

    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingCorrellation(long seed, int correlation) {

        boolean useHomogeneousVMs = false;

        // -1 means use check fully homogeneous configuration inside DCs
        if(correlation == -1) {
            correlation = COST_VALUE_CORRELATION.intValue();
            useHomogeneousVMs = true;
        }

        return generator.generateHierarchicalTestProblem(
                NUM_VENDORS,
                DATA_CENTERS_PER_VENDOR, // Using the input variable
                DATA_CENTERS_PER_VENDOR, // Same value for max
                VMS_PER_DC, // Same value for min and max
                VMS_PER_DC, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                BUDGET,
                REQUIRED_ITEMS, // Same value for min
                REQUIRED_ITEMS, // Same value for max
                useHomogeneousVMs,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                correlation/10d
            );
    }


    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingRequestedVms(long seed, int requestedVms) {

        return generateStandardProblemWithVaryingRequestedVms(seed, requestedVms, false);
    }
    
    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingRequestedVms(long seed, int requestedVms, boolean expandDcs) {


        int vms_per_dc = VMS_PER_DC;
        if(expandDcs) {
            vms_per_dc = requestedVms;
        }
        int budget = requestedVms * BUDGET_COEF;

        return generator.generateHierarchicalTestProblem(
                NUM_VENDORS,
                DATA_CENTERS_PER_VENDOR, // Using the input variable
                DATA_CENTERS_PER_VENDOR, // Same value for max
                vms_per_dc, // Same value for min and max
                vms_per_dc, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                budget,
                requestedVms, // Same value for min
                requestedVms, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }

    private IntervalKnapsackWithGroupsProblem generateStandardProblemWithVaryingRequestedVmsExtended(long seed, int requestedVms,int vmsPerDc, int dcs, int vendors) {

        int budget = requestedVms * BUDGET_COEF;

        return generator.generateHierarchicalTestProblem(
                vendors,
                dcs, // Using the input variable
                dcs, // Same value for max
                vmsPerDc, // Same value for min and max
                vmsPerDc, // Same value for min and max
                MIN_VM_COST,
                MAX_VM_COST,
                MIN_VM_VALUE,
                MAX_VM_VALUE,
                budget,
                requestedVms, // Same value for min
                requestedVms, // Same value for max
                USE_HOMOGENEOUS_VMS,
                VENDOR_DISCOUNT_THRESHOLDS,
                VENDOR_DISCOUNTS,
                DC_BONUS_THRESHOLDS,
                DC_BONUSES,
                seed,
                COST_VALUE_CORRELATION
            );
    }
    
    /**
     * Performs warm-up iterations to heat Java caches/pipelines
     * This method can be reused by other experiment methods
     * @param problemGenerator BiFunction that takes (seed, dataCenterCount) and returns IntervalKnapsackWithGroupsProblem
     */
    public void performWarmup(BiFunction<Long, Integer, IntervalKnapsackWithGroupsProblem> problemGenerator, int parameter) {
        for (int warmupIter = 0; warmupIter < 5; warmupIter++) {
            // Use a fixed value for warm-up, e.g., first value in our array
            long warmupSeed = globalSeedCounter++;
            
            // these should be equal problems prepared for different solvers
            IntervalKnapsackWithGroupsProblem problem1 = problemGenerator.apply(warmupSeed, parameter);
            IntervalKnapsackWithGroupsProblem problem2 = problemGenerator.apply(warmupSeed, parameter);
            
            // Solve problems but don't record stats for warm-up
            solveGKA(problem1, parameter);

            if(startSat) {
                solveSat(problem2, parameter);
            }
        }
    }
    
    /**
     * Sets the global seed counter to a specific value
     */
    public void setGlobalSeedCounter(long seed) {
        globalSeedCounter = seed;
    }

    public Double solveGKA(IntervalKnapsackWithGroupsProblem groupProblem, Integer variable) {
        
        QuantityMultiplierWeightGroupManager.PRINT_LOGS = false;
        GroupItemIntervalKnapsackSolver groupSolver = new GroupItemIntervalKnapsackSolver();
        Long startTime = System.nanoTime();
        boolean success = groupSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        

        groupProblem.calculateStats();
        Double runtime = (endTime - startTime)/1000000d;
        runtimeStats.addValue("GKA_" + variable, runtime);
        System.out.println(runtime);

        if(success) {
            successStats.addValue("GKA_" + variable, 1d);
            valueStats.addValue("GKA_" + variable, groupProblem.getImprovedTotalValue());
            costStats.addValue("GKA_" + variable, groupProblem.getImprovedTotalWeight().doubleValue());
        } else {
            successStats.addValue("GKA_" + variable, 0d);
        }
        
        return runtime;
    }
    
    public Double solveSat(IntervalKnapsackWithGroupsProblem groupProblem, Integer variable) {
               
        KnapsackSatConverter converter = new KnapsackSatConverter();
        CpModel model = converter.convertHierarchicalKnapsackProblem(groupProblem);
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(SAT_MAX_WAIT_TIME_SECONDS);
        
        Long startTime = System.nanoTime();
        final CpSolverStatus status = solver.solve(model);
        Long endTime = System.nanoTime();
        Double runtime = (endTime - startTime)/1000000d;

        runtimeStats.addValue("SAT_" + variable, runtime);
        System.out.println(runtime);
        

        // Check that the problem has an optimal solution.
        if (status == CpSolverStatus.OPTIMAL) {
            //System.out.println("Total packed value: " + solver.objectiveValue());
            successStats.addValue("SAT_" + variable, 1d);
            valueStats.addValue("SAT_" + variable, solver.objectiveValue());
            costStats.addValue("SAT_" + variable, converter.getTotalCost(solver)/100d);

        } else {
          successStats.addValue("SAT_" + variable, 0d);
        }

        return runtime;
    }
    
    public Double solveBruteForce(IntervalKnapsackWithGroupsProblem groupProblem, Integer variable) {
        
            BruteForceIntervalKnapsackSolver bruteSolver = new BruteForceIntervalKnapsackSolver();
            Long startTime = System.nanoTime();
            boolean bruteSuccess = bruteSolver.solve(groupProblem);
            Long endTime = System.nanoTime();

            groupProblem.calculateStats();
            Double runtime = (endTime - startTime)/1000000d;
            runtimeStats.addValue("BF_" + variable, runtime);
            System.out.println(runtime);

            if(bruteSuccess) {
                successStats.addValue("BF_" + variable, 1d);
                valueStats.addValue("BF_" + variable, groupProblem.getImprovedTotalValue());
                costStats.addValue("BF_" + variable, groupProblem.getImprovedTotalWeight().doubleValue());
            } else {
                successStats.addValue("BF_" + variable, 0d);
            }
            
            return runtime;
    }

    public Double solveSingleKnapsack(IntervalKnapsackWithGroupsProblem groupProblem, Integer variable) {
        
        IntervalItemsNumberKnapsackProblem problem = new IntervalItemsNumberKnapsackProblem();

        List<Item> items = groupProblem.getGroupItems().stream()
                .flatMap(gi -> gi.collectInnerSubItems().stream())
                .collect(Collectors.toList());

		problem.setItems(items);
		problem.setMaxWeight(groupProblem.getMaxWeight());
		problem.setMinItemsNumber(groupProblem.getMinItemsNumber());
		problem.setMaxItemsNumber(groupProblem.getMaxItemsNumber());
		IKnapsack01MultiWeightsSolver fixedSolver = new IntervalItemsNumberKnapsackSolver();

        Long startTime = System.nanoTime();
        boolean success = fixedSolver.solve(groupProblem);
        Long endTime = System.nanoTime();
        

        groupProblem.calculateStats();
        Double runtime = (endTime - startTime)/1000000d;
        runtimeStats.addValue("K_" + variable, runtime);
        System.out.println(runtime);

        if(success) {
            successStats.addValue("K_" + variable, 1d);
            valueStats.addValue("K_" + variable, groupProblem.getImprovedTotalValue());
            costStats.addValue("K_" + variable, groupProblem.getImprovedTotalWeight().doubleValue());
        } else {
            successStats.addValue("K_" + variable, 0d);
        }
        
        return runtime;
    }

    private void clearStats() {
        runtimeStats.clearStats();
        valueStats.clearStats();
        costStats.clearStats();
        successStats.clearStats();
    }

    private void printStats() {
        //System.out.println("\n=======================\n");
        //System.out.println(successStats.getData());
        System.out.println("\n=======================\n");
        System.out.println(costStats.getData());
        System.out.println("\n=======================\n");
        System.out.println(valueStats.getData());
        System.out.println("\n=======================\n");
        System.out.println(runtimeStats.getData());
    }
}

    