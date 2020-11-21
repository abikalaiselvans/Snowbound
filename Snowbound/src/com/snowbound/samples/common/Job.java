/**
 * Copyright (C) 2012-2017 by Snowbound Software Corp. All rights reserved.
 * @author Bismark Frimpong
 * 
 */
package com.snowbound.samples.common;

/**
 * @author Bismark Frimpong
 *
 */
public class Job
{
    // Identifier for the job
    private int jobNumber;
    // Estimated remaining CPU usage time
    private int maxTimeRemaining;
    // Current status of the process
    private int status;
    // Time at which the job entered the system
    private int timeEntered;
    // Amount of time spent on the ready queue waiting for the CPU
    private double waitTime;
    // Time from entrance to completion
    private double turnAroundTime;
    // Number of times this process had the CPU allocated to it 
    private int cpuCount;
    // Actual CPU usage time 
    //private int cpuTime;
    private long cpuTime = System.nanoTime();
    // The identifier for the next job to enter the system
    private static int nextJob = 1;
    private int timeStamp = 0;
    private double totalTurnaroundTime;

    /**
     * Create a new process with the
     * estimated CPU usage, and entrance time
     * @param est       estimated CPU usage for the new job
     * @param timeIn    time the new job entered the system
     */
    public Job(int est, int timeIn)
    {
        // Assign this job an id
        this.jobNumber = Job.nextJob;
        // Increment the job counter
        Job.nextJob ++;
        this.maxTimeRemaining = est;
        this.timeEntered = timeIn;
        //this.status = Scheduler.READY;
        
        // Initialize the statistics for this job
        this.initializeStatistics();
    }

    /**
     * Sets all the statistics for this job
     * to zero at its entrance into the
     * simulation.
     */
    private void initializeStatistics()
    {
        this.waitTime = 0.0;
        this.turnAroundTime = 0.0;
        this.totalTurnaroundTime = 0.0;
        this.cpuCount = 0;
        //this.cpuTime = System.nanoTime();
    }

    /**
     * Changes the current status of the this
     * job as it is moved between the CPU and the
     * ready and waiting queues of the simulation
     * @param newStatus the new status for the job
     */
    private void setStatus(int newStatus)
    {
        this.setStatus(status);
    }

   /**
     * Retrieves the identifier for this job
     * @return  the job number for this process
     */
    public int getJobNum()
    {
		return this.jobNumber;
		
    }

    /**
     * Retrieves the maximum remaining CPU usage time
     * for this job
     * @return  the max remaining CPU usage
     */
   /* public long getRemainingCPUTime()
    {
        //double elapsedTimeInSec = (System.nanoTime() - cpuTime) * 1.0e-9;
        //System.out.println(elapsedTimeInSec);
		return (int) this.cpuTime;
		
        
    }*/

    /**
     * Retrieves the total wait time for this
     * job
     * @return  the total time this job spent waiting
     */
    public int getWaitTime()
    {
		return (int) this.waitTime;
        
    }

    /**
     * Retrieves the turnaround time for this job
     * @return  the turnaround time for this job
     */
    public int getTurnAroundTime()
    {
		return (int) this.turnAroundTime;
        
    }

    /**
     * Update the statistics for this job
     * after it waits for a tick on the I/O
     * queue
     */
    public void waitTick()
    {
    	//waitTime = (turnAroundTime - maxTimeRemaining - timeEntered) ;
    	waitTime = (turnAroundTime + maxTimeRemaining + timeEntered) ;
    	//turnAroundTime = (int) (waitTime + maxTimeRemaining);
    	turnAroundTime = (int) (waitTime + maxTimeRemaining);
    }

    /**
     * Update the statistics for this job
     * after it sits for a tick on the ready
     * queue
     */
    public void readyTick()
    {
    	status = (int) (waitTime - cpuTime);
    }

    /**
     * Update the statistics for this job
     * after it executes for a tick on the CPU
     */
    public void cpuTick()
    {
        cpuTime = jobNumber + cpuCount;
    }

    /**
     * Mark this job as successfully completed
     */
    public void complete()
    {
        System.out.println("This Job was Successfully Completed!!!");
    }

    /**
     * Mark this job as terminated
     */
    public void terminate()
    {
        //System.exit(0);
        System.out.println("This Job was Terminated!!!");
    }

    //@Override
    public String toString()
    {
        return (" Job Number: " + jobNumber + "\n" + "CPU Time: " + (double)(System.nanoTime() - cpuTime) * 1.0e-9 + "\n" + 
        		" Wait Time: " + (double) waitTime/1000 + "\n" + " Turn Around Time: " + (double) turnAroundTime/ 1000);
    }

    /**
     * Retrieves the running statistics for this job
     * @return  a string containing the statistics for this job
     */
    public String getStatistics()
    {
        return(toString());
    }
}