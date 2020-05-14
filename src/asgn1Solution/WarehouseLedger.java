package asgn1Solution;

import java.util.ArrayList;

import asgn1Question.Ledger;
import asgn1Question.WarehouseException;

/**
 * A solution to the "warehouse ledger" part of INB370 Assignment 1.
 * 
 * @author CAB302
 * @version 1.0
 */
public class WarehouseLedger implements Ledger {
	
	private Integer today = 1; // day is initially 1
	private ArrayList<Integer> cashReserve = new ArrayList<Integer>(); // boxes
	private ArrayList<Integer> stockLevel = new ArrayList<Integer>(); // dollars
	private Integer wholesaleCost; // dollars
	private Integer retailPrice; // dollars
	private Integer deliveryCost; // dollars
	
	/**
	 * Construct a warehouse's ledger with the supplied initial
	 * stock level and cash reserve.  By default, the initial
	 * day is number 1.
	 * 
	 * @param initialStock - initial stock level, in items
	 * @param initialCash - initial cash reserve, in dollars
	 * @param wholesaleCostPerItem - how much it costs to buy an item, in dollars
	 * @param retailPricePerItem - how much we get from selling an item, in dollars
	 * @param deliveryCharge - extra cost associated with buying items, in dollars 
	 * @throws WarehouseException - if the stock level, wholesale cost, retail price or
	 * delivery charge are negative, or if the wholesale cost is greater than
	 * the retail price (but we may trade while insolvent, so no exception
	 * is thrown for a negative initial cash reserve!)
	 */
	public WarehouseLedger(
			Integer initialStock,
			Integer initialCash,
			Integer wholesaleCostPerItem,
			Integer retailPricePerItem,
			Integer deliveryCharge)
	throws WarehouseException {
		// Sanity checks on parameters (NB: The second and fourth checks imply
		// that the retail price must be non-negative, so we don't need to
		// check this explicitly)
		if (initialStock < 0) {
			throw new WarehouseException("Initial stock level cannot be negative");
		};
		if (wholesaleCostPerItem < 0) {
			throw new WarehouseException("Wholesale cost cannot be negative");
		};
		if (deliveryCharge < 0) {
			throw new WarehouseException("Delivery charge cannot be negative");
		};
		if (wholesaleCostPerItem > retailPricePerItem) {
			throw new WarehouseException("Wholesale cost may not exceed retail price");
		};
		// Save initial values
		stockLevel.add(0); cashReserve.add(0); // We never use element zero
		stockLevel.add(initialStock);
		cashReserve.add(initialCash);
		wholesaleCost = wholesaleCostPerItem;
		retailPrice = retailPricePerItem;
		deliveryCost = deliveryCharge;
	}
	
	public void nextDay() {
		// Transfer today's balances to next day
		stockLevel.add(stockLevel.get(today));
		cashReserve.add(cashReserve.get(today));
		// Turn the page to the new day
		today = today + 1;
	}
	
	public boolean sellItems(Integer requested) throws WarehouseException {
		// Sanity check on parameter
		if (requested < 0) {
			throw new WarehouseException("Attempt to sell negative number of items");
		};
		// Sell however many we're asked for, or all that we have,
		// whichever is smaller
		Integer sold = Integer.valueOf(requested > stockLevel.get(today) ? stockLevel.get(today) : requested);
		stockLevel.set(today, stockLevel.get(today) - sold);
		cashReserve.set(today, cashReserve.get(today) + (sold * retailPrice));
		// Let the caller know if the order wasn't completed fully
		return requested.equals(sold);
	}
	
	public void buyItems(Integer requested) throws WarehouseException {
		// Sanity check on parameter
		if (requested < 0) {
			throw new WarehouseException("Attempt to buy negative number of items");
		};
		// We may go into debt when buying items
		stockLevel.set(today, stockLevel.get(today) + requested);
		cashReserve.set(today, cashReserve.get(today) - (requested * wholesaleCost) - deliveryCost);
	}
	
	public Integer currentDay() {
		return today;
	}
	
	public Integer cashAvailable() {
		return cashReserve.get(today);
	}
	
	public Integer cashAvailable(Integer day) throws WarehouseException {
		if (day <= 0 || day > today) {
			throw new WarehouseException("Attempt to lookup nonexistent day in ledger");
		}
		return cashReserve.get(day);
	}
	
	public Integer inStock() {
		return stockLevel.get(today);
	}
	
	public Integer inStock(Integer day) throws WarehouseException {
		if (day <= 0 || day > today) {
			throw new WarehouseException("Attempt to lookup nonexistent day in ledger");
		}
		return stockLevel.get(day);
	}
	
	public String toString() {
		return "Day " + today + ": " +
		"Cash reserve = $" + cashReserve.get(today) + "; " +
		"Items in stock = " + stockLevel.get(today) + "\n";
	}

}
