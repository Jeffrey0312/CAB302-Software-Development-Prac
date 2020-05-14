package asgn1Solution;

import asgn1Question.Transactions;
import asgn1Question.WarehouseException;
import asgn1Solution.WarehouseLedger; // Unnecessary, but used in broken programs

/**
 * A solution to the "warehouse transactions" part of INB370 Assignment 1.
 * 
 * @author CAB302
 * @version 1.0
 */
public class WarehouseTransactions implements Transactions {

	private Integer maxCapacity;
	private Integer maxDays;
	private WarehouseLedger theLedger;
	private boolean lastOrderFulfilled = true;

	/**
	 * Define the transactions you can perform while managing a warehouse of items
	 * awaiting retail sale.
	 * 
	 * @param warehouseCapacity - the maximum capacity of the warehouse, in items
	 * @param jobDuration - the duration of your fixed-term job as
	 * warehouse manager, in days
	 * @param cleanLedger - a ledger for recording the daily transactions performed
	 * @throws WarehouseException if the warehouse capacity is negative or the job
	 * duration is not positive (greater than zero)
	 */
	public WarehouseTransactions(
			Integer warehouseCapacity,
			Integer jobDuration,
			WarehouseLedger cleanLedger) throws WarehouseException {
		// Sanity checks on transaction parameters
		if (warehouseCapacity < 0) {
			throw new WarehouseException("Warehouse capacity may not be negative");
		};
		if (jobDuration <= 0) {
			throw new WarehouseException("Job duration must be positive");
		};
		// Save the parameters
		maxCapacity = warehouseCapacity;
		maxDays = jobDuration;
		theLedger = cleanLedger;
	}

	
	public boolean insolvent() {
		return theLedger.cashAvailable() < 0;
	}

	public boolean orderUnfulfilled() {
		return !lastOrderFulfilled;
	}

	public boolean jobDone() {
		return theLedger.currentDay() > maxDays;
	}
	
	public void restockAndSellStock(Integer todaysOrder) throws WarehouseException {
		// Sanity check on parameter
		if (todaysOrder < 0) {
			throw new WarehouseException("Sales order cannot be negative");
		};
		// Perform today's transactions
		theLedger.buyItems(maxCapacity - theLedger.inStock());
		lastOrderFulfilled = theLedger.sellItems(todaysOrder);
		// "Tomorrow is another day" - Scarlett O'Hara
		theLedger.nextDay();
	}

	public void sellStock(Integer todaysOrder) throws WarehouseException {
		// Sanity check on parameter
		if (todaysOrder < 0) {
			throw new WarehouseException("Sales order cannot be negative");
		};
		// Perform today's transaction
		lastOrderFulfilled = theLedger.sellItems(todaysOrder);
		// "Tomorrow is another day" - Scarlett O'Hara
		theLedger.nextDay();
	}



}
