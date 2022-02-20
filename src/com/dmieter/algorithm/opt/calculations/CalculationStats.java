
package com.dmieter.algorithm.opt.calculations;

/**
 *
 * @author emelyanov
 */
public class CalculationStats {
    protected long initOperations = 0;
    protected long optOperations = 0;
    protected long operations = 0;
    protected long iterations = 0;
    
    public void addInitOperation(){
        initOperations++;
    }
    
    public void addOptOperation(){
       optOperations++;
    }
    
    public void addOptOperation(int toAdd){
       optOperations += toAdd;
    }
    
    public void addOperation(){
        operations++;
    }
    
    public void addIteration(){
        iterations++;
    }
    
    public void resetInitOperations(){
        initOperations = 0;
    }
    
    public void resetOptOperations(){
        optOperations = 0;
    }
    
    public void resetOperations(){
        operations = 0;
    }
    
    public void resetIterations(){
        iterations = 0;
    }
    
    public void resetAllOperations(){
        resetInitOperations();
        resetOptOperations();
        resetOperations();
        resetIterations();
    }
    
    public long getInitOperations(){
        return initOperations;
    }
    
    public long getOptOperations(){
        return optOperations;
    }
    
    public long getOperations(){
        return operations;
    }
    
    public long getAllOperations(){
        return getInitOperations() + getOperations() + getOptOperations();
    }
    
    public long getIterations(){
        return iterations;
    }
}
