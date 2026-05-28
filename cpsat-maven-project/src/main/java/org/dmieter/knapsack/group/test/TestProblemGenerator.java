package org.dmieter.knapsack.group.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dmieter.knapsack.group.manager.OneLevelQuantityBonusGroupManager;
import org.dmieter.knapsack.group.manager.OneLevelQuantityProportionalBonusGroupManager;
import org.dmieter.knapsack.group.manager.OneLevelQuantityProportionalDiscountGroupManager;

import com.dmieter.algorithm.opt.knapsack.Item;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItem;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemGroupKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.GroupItemKnapsack;
import com.dmieter.algorithm.opt.knapsack.knapsack01.multiweights.group.IntervalKnapsackWithGroupsProblem;

public class TestProblemGenerator {
    
    /**
     * Generates a test problem for a two-level hierarchical knapsack.
     * 
     * @param numVendors Number of vendors (top-level zones)
     * @param minDataCentersPerVendor Minimum number of data centers per vendor
     * @param maxDataCentersPerVendor Maximum number of data centers per vendor
     * @param minVMsPerDC Minimum number of virtual machines per data center
     * @param maxVMsPerDC Maximum number of virtual machines per data center
     * @param minVMCost Minimum cost of a virtual machine
     * @param maxVMCost Maximum cost of a virtual machine
     * @param minVMValue Minimum value of a virtual machine
     * @param maxVMValue Maximum value of a virtual machine
     * @param budget Total budget constraint
     * @param minSelectedItems Minimum number of items to select
     * @param maxSelectedItems Maximum number of items to select
     * @param useHomogeneousVMs Whether VMs within a DC should be homogeneous (same cost/value) or heterogeneous
     * @param vendorDiscountThresholds Array of possible thresholds for vendor discount (quantity of VMs needed for discount)
     * @param vendorDiscounts Array of possible discount amounts for vendors when threshold is met
     * @param dcBonusThresholds Array of possible thresholds for DC bonus (quantity of VMs needed for bonus)
     * @param dcBonuses Array of possible bonus amounts for DCs when threshold is met
     * @return Generated IntervalKnapsackWithGroupsProblem instance
     */
    public IntervalKnapsackWithGroupsProblem generateHierarchicalTestProblem(
            int numVendors,
            int minDataCentersPerVendor,
            int maxDataCentersPerVendor,
            int minVMsPerDC,
            int maxVMsPerDC,
            int minVMCost,
            int maxVMCost,
            int minVMValue,
            int maxVMValue,
            int budget,
            int minSelectedItems,
            int maxSelectedItems,
            boolean useHomogeneousVMs,
            int[] vendorDiscountThresholds,
            int[] vendorDiscounts,
            int[] dcBonusThresholds,
            int[] dcBonuses) {
        return generateHierarchicalTestProblem(
            numVendors, minDataCentersPerVendor, maxDataCentersPerVendor, 
            minVMsPerDC, maxVMsPerDC, minVMCost, maxVMCost, minVMValue, 
            maxVMValue, budget, minSelectedItems, maxSelectedItems, 
            useHomogeneousVMs, vendorDiscountThresholds, vendorDiscounts, 
            dcBonusThresholds, dcBonuses, System.currentTimeMillis(), 0.5);
    }
    
    /**
     * Generates a test problem for a two-level hierarchical knapsack with a specified random seed.
     * 
     * @param numVendors Number of vendors (top-level zones)
     * @param minDataCentersPerVendor Minimum number of data centers per vendor
     * @param maxDataCentersPerVendor Maximum number of data centers per vendor
     * @param minVMsPerDC Minimum number of virtual machines per data center
     * @param maxVMsPerDC Maximum number of virtual machines per data center
     * @param minVMCost Minimum cost of a virtual machine
     * @param maxVMCost Maximum cost of a virtual machine
     * @param minVMValue Minimum value of a virtual machine
     * @param maxVMValue Maximum value of a virtual machine
     * @param budget Total budget constraint
     * @param minSelectedItems Minimum number of items to select
     * @param maxSelectedItems Maximum number of items to select
     * @param useHomogeneousVMs Whether VMs within a DC should be homogeneous (same cost/value) or heterogeneous
     * @param vendorDiscountThresholds Array of possible thresholds for vendor discount (quantity of VMs needed for discount)
     * @param vendorDiscounts Array of possible discount amounts for vendors when threshold is met
     * @param dcBonusThresholds Array of possible thresholds for DC bonus (quantity of VMs needed for bonus)
     * @param dcBonuses Array of possible bonus amounts for DCs when threshold is met
     * @param seed Random seed for reproducible generation
     * @return Generated IntervalKnapsackWithGroupsProblem instance
     */
    public IntervalKnapsackWithGroupsProblem generateHierarchicalTestProblem(
            int numVendors,
            int minDataCentersPerVendor,
            int maxDataCentersPerVendor,
            int minVMsPerDC,
            int maxVMsPerDC,
            int minVMCost,
            int maxVMCost,
            int minVMValue,
            int maxVMValue,
            int budget,
            int minSelectedItems,
            int maxSelectedItems,
            boolean useHomogeneousVMs,
            int[] vendorDiscountThresholds,
            int[] vendorDiscounts,
            int[] dcBonusThresholds,
            int[] dcBonuses,
            long seed) {
        return generateHierarchicalTestProblem(
            numVendors, minDataCentersPerVendor, maxDataCentersPerVendor, 
            minVMsPerDC, maxVMsPerDC, minVMCost, maxVMCost, minVMValue, 
            maxVMValue, budget, minSelectedItems, maxSelectedItems, 
            useHomogeneousVMs, vendorDiscountThresholds, vendorDiscounts, 
            dcBonusThresholds, dcBonuses, seed, 0.5);
    }
    
    /**
     * Generates a test problem for a two-level hierarchical knapsack with a specified random seed and cost-value correlation.
     * 
     * @param numVendors Number of vendors (top-level zones)
     * @param minDataCentersPerVendor Minimum number of data centers per vendor
     * @param maxDataCentersPerVendor Maximum number of data centers per vendor
     * @param minVMsPerDC Minimum number of virtual machines per data center
     * @param maxVMsPerDC Maximum number of virtual machines per data center
     * @param minVMCost Minimum cost of a virtual machine
     * @param maxVMCost Maximum cost of a virtual machine
     * @param minVMValue Minimum value of a virtual machine
     * @param maxVMValue Maximum value of a virtual machine
     * @param budget Total budget constraint
     * @param minSelectedItems Minimum number of items to select
     * @param maxSelectedItems Maximum number of items to select
     * @param useHomogeneousVMs Whether VMs within a DC should be homogeneous (same cost/value) or heterogeneous
     * @param vendorDiscountThresholds Array of possible thresholds for vendor discount (quantity of VMs needed for discount)
     * @param vendorDiscounts Array of possible discount amounts for vendors when threshold is met
     * @param dcBonusThresholds Array of possible thresholds for DC bonus (quantity of VMs needed for bonus)
     * @param dcBonuses Array of possible bonus amounts for DCs when threshold is met
     * @param seed Random seed for reproducible generation
     * @param costValueCorrelation Correlation between cost and value (0.0 = no correlation, 1.0 = perfect positive correlation)
     * @return Generated IntervalKnapsackWithGroupsProblem instance
     */
    public IntervalKnapsackWithGroupsProblem generateHierarchicalTestProblem(
            int numVendors,
            int minDataCentersPerVendor,
            int maxDataCentersPerVendor,
            int minVMsPerDC,
            int maxVMsPerDC,
            int minVMCost,
            int maxVMCost,
            int minVMValue,
            int maxVMValue,
            int budget,
            int minSelectedItems,
            int maxSelectedItems,
            boolean useHomogeneousVMs,
            int[] vendorDiscountThresholds,
            int[] vendorDiscounts,
            int[] dcBonusThresholds,
            int[] dcBonuses,
            long seed,
            double costValueCorrelation) {
        
        Random random = new Random(seed);
        List<GroupItem> vendorGroups = new ArrayList<>();
        
        int itemIdCounter = 100; // Start IDs from 100 to avoid conflicts
        
        for (int vendorIdx = 0; vendorIdx < numVendors; vendorIdx++) {
            // Determine number of data centers for this vendor
            int numDataCenters = minDataCentersPerVendor + 
                random.nextInt(maxDataCentersPerVendor - minDataCentersPerVendor + 1);
            
            List<GroupItem> dcGroups = new ArrayList<>();
            
            for (int dcIdx = 0; dcIdx < numDataCenters; dcIdx++) {
                // Determine number of VMs for this DC
                int numVMs = minVMsPerDC + 
                    random.nextInt(maxVMsPerDC - minVMsPerDC + 1);
                
                List<Item> vmItems = new ArrayList<>();
                
                // Generate VMs for this DC
                for (int vmIdx = 0; vmIdx < numVMs; vmIdx++) {
                    int vmCost, vmValue;
                    
                    if (useHomogeneousVMs) {
                        // Use the same cost and value for all VMs in this DC
                        if (vmIdx == 0) {
                            // Generate base cost and value for this DC
                            // Ensure cost is a multiple of 4 for proper discount calculations to support 50% and 25% discounts to integer numbers
                            int rawCost = minVMCost + random.nextInt(maxVMCost - minVMCost + 1);
                            vmCost = ((rawCost + 3) / 4) * 4; // Round up to nearest multiple of 4 to support 50% and 25% discounts to integer numbers
                            
                            // Apply correlation between cost and value
                            vmValue = calculateCorrelatedValue(vmCost, minVMCost, maxVMCost, minVMValue, maxVMValue, costValueCorrelation, random);
                        } else {
                            // Use same values as first VM in DC
                            vmCost = (int) vmItems.get(0).getWeight();
                            vmValue = (int) vmItems.get(0).getValue();
                        }
                    } else {
                        // Each VM can have different cost and value
                        // Ensure cost is a multiple of 4 for proper discount calculations
                        int rawCost = minVMCost + random.nextInt(maxVMCost - minVMCost + 1);
                        vmCost = ((rawCost + 3) / 4) * 4; // Round up to nearest multiple of 4
                        
                        // Apply correlation between cost and value
                        vmValue = calculateCorrelatedValue(vmCost, minVMCost, maxVMCost, minVMValue, maxVMValue, costValueCorrelation, random);
                    }
                    
                    Item vmItem = new Item(itemIdCounter++, vmCost, vmValue);
                    vmItems.add(vmItem);
                }
                
                // Select random DC bonus parameters
                int selectedDcBonusThreshold = dcBonusThresholds.length > 0 ? 
                    dcBonusThresholds[random.nextInt(dcBonusThresholds.length)] : 0;
                int selectedDcBonus = dcBonuses.length > 0 ? 
                    dcBonuses[random.nextInt(dcBonuses.length)] : 0;
                
                // Create DC group
                GroupItemKnapsack dcGroup = new GroupItemKnapsack(itemIdCounter++, vmItems);
                
                // Add DC bonus manager for joint VM usage in the same DC
                if (selectedDcBonusThreshold > 0 && selectedDcBonus > 0) {
                    dcGroup.setGroupPropertyManager(
                        new OneLevelQuantityProportionalBonusGroupManager(
                            "DC-" + vendorIdx + "-" + dcIdx, selectedDcBonusThreshold, selectedDcBonus));
                }
                
                dcGroups.add(dcGroup);
            }
            
            // Select random vendor discount parameters
            int selectedVendorDiscountThreshold = vendorDiscountThresholds.length > 0 ? 
                vendorDiscountThresholds[random.nextInt(vendorDiscountThresholds.length)] : 0;
            int selectedVendorDiscount = vendorDiscounts.length > 0 ? 
                vendorDiscounts[random.nextInt(vendorDiscounts.length)] : 0;
            
            // Create vendor group containing DC groups
            GroupItemGroupKnapsack vendorGroup = new GroupItemGroupKnapsack(itemIdCounter++, dcGroups);
            
            // Add vendor discount manager for selecting multiple VMs from the same vendor
            if (selectedVendorDiscountThreshold > 0 && selectedVendorDiscount > 0) {
                vendorGroup.setGroupPropertyManager(
                    new OneLevelQuantityProportionalDiscountGroupManager(
                        "Vendor-" + vendorIdx, selectedVendorDiscountThreshold, selectedVendorDiscount));
            }
            
            vendorGroups.add(vendorGroup);
        }
        
        // Create the main problem
        IntervalKnapsackWithGroupsProblem groupProblem = new IntervalKnapsackWithGroupsProblem();
        groupProblem.setGroupItems(vendorGroups);
        groupProblem.setMaxWeight(budget);
        groupProblem.setMinItemsNumber(minSelectedItems);
        groupProblem.setMaxItemsNumber(maxSelectedItems);
        
        return groupProblem;
    }
    
    /**
     * Calculates a value that has a specified correlation with the cost
     * @param cost The cost of the VM
     * @param minCost Minimum possible cost
     * @param maxCost Maximum possible cost
     * @param minValue Minimum possible value
     * @param maxValue Maximum possible value
     * @param correlation Correlation factor (0.0 = no correlation, 1.0 = perfect correlation)
     * @param random Random number generator
     * @return Calculated value with the specified correlation to cost
     */
    private int calculateCorrelatedValue(int cost, int minCost, int maxCost, int minValue, int maxValue, double correlation, Random random) {
        // Calculate the proportional position of the cost in its range
        double costPosition = (double)(cost - minCost) / (maxCost - minCost);
        
        // Calculate the correlated component of the value (perfectly correlated value)
        int perfectlyCorrelatedValue = minValue + (int)(costPosition * (maxValue - minValue));
        
        // When correlation is 1.0, return the perfectly correlated value
        if (correlation >= 1.0) {
            return perfectlyCorrelatedValue;
        }
        
        // When correlation is 0.0, return a completely random value in the range
        if (correlation <= 0.0) {
            return minValue + random.nextInt(maxValue - minValue + 1);
        }
        
        // For correlations between 0 and 1, interpolate between the correlated value and a random value
        // Higher correlation means closer to the perfectly correlated value
        int randomValue = minValue + random.nextInt(maxValue - minValue + 1);
        
        // Weighted interpolation: correlation portion from correlated value, (1-correlation) from random value
        double interpolatedValue = correlation * perfectlyCorrelatedValue + (1.0 - correlation) * randomValue;
        
        int calculatedValue = (int) Math.round(interpolatedValue);
        
        // Ensure the value stays within bounds
        calculatedValue = Math.max(minValue, Math.min(maxValue, calculatedValue));
        
        return calculatedValue;
    }
    
    /**
     * Generates a test problem similar to the original hardcoded version
     * for backward compatibility
     */
    public IntervalKnapsackWithGroupsProblem generateTestProblem() {
        return generateHierarchicalTestProblem(
            3,                      // 3 vendors (zones)
            1,                      // Min 1 DC per vendor
            3,                      // Max 3 DCs per vendor
            8,                      // Min 8 VMs per DC
            8,                      // Max 8 VMs per DC (fixed)
            4,                      // Min VM cost
            16,                     // Max VM cost
            3,                      // Min VM value
            10,                     // Max VM value
            50,                     // Budget
            8,                      // Min selected items
            8,                      // Max selected items
            false,                  // Heterogeneous VMs
            new int[]{3},           // Vendor discount thresholds
            new int[]{50},           // Vendor discounts
            new int[]{3},           // DC bonus thresholds
            new int[]{4},           // DC bonuses
            12345L,                 // Fixed seed for reproducibility
            0.5                     // Medium correlation between cost and value
        );
    }
    
    /**
     * Generates a simple hierarchical test problem with specific parameters
     */
    public IntervalKnapsackWithGroupsProblem generateSimpleTestProblem() {
        return generateHierarchicalTestProblem(
            2,                      // 2 vendors
            2,                      // Min 2 DCs per vendor
            2,                      // Max 2 DCs per vendor (fixed)
            5,                      // Min 3 VMs per DC
            8,                      // Max 5 VMs per DC
            4,                      // Min VM cost
            32,                     // Max VM cost
            3,                      // Min VM value
            20,                     // Max VM value
            80,                     // Budget
            8,                     // Min selected items
            8,                     // Max selected items
            false,                  // Homogeneous VMs
            new int[]{3, 4},        // Vendor discount thresholds
            new int[]{25,50},       // Vendor discounts
            new int[]{2, 3},        // DC bonus thresholds
            new int[]{3, 5},        // DC bonuses
            67890L,                 // Fixed seed for reproducibility
            0.3                     // Higher correlation between cost and value
        );
    }
}
