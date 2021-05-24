package question;

/**
 * This interface defines the daily transactions associated with managing 
 * a buying-and-selling business, in this case a warehouse. 
 * The assumed scenario is that each morning you may choose whether or not to 
 * restock the warehouse, based on the current cash reserve and the number 
 * of items in stock. Later in the day an order for items will be received 
 * which must be fulfilled. At the end of each day the ledger is turned to the 
 * next page. Early the next morning your boss will check the ledger, to 
 * ensure that you have not bankrupted the warehouse's cash reserve, or failed to 
 * fulfill a client's order. Either of these shortcomings will result in 
 * immediate dismissal with no pay!
 * Your third and fourth tasks in the assignment are to implement this 
 * interface as class WarehouseTransactions and produce a 
 * comprehensive unit test suite for it. 
 * Constructor:
 * To connect to the provided GUI, your 
 * WarehouseTransactions class must provide a constructor with the following 
 * signature, as well as all the methods below. 
 * Parameters:
 * Integer warehouseCapacity - the maximum capacity of the warehouse, in items
 * Integer jobDuration - the duration of your fixed-term job as 
 * warehouse manager, in days WarehouseLedger ledger - a ledger for recording the daily transactions performed 
 * Throws:
 * WarehouseException if the warehouse capacity is negative or the job * duration is not strictly positive (greater than zero) *
 * 
 * 
 * @author CAB302
 * @version 1.0
 *
 */
public interface Transactions {
	
	/**
	 * Returns true if the business is currently insolvent, i.e., if the cash
	 * reserve is negative.
	 * 
	 * @return true if the ledger says that we are bankrupt, false otherwise
	 */
	public boolean insolvent();
	
	
	/**
	 * Returns true if the last order processed, using methods <code>sellStock</code>
	 * or <code>restockAndSellStock</code>, was not fulfilled, i.e., if more items
	 * were ordered than were in stock at the time.  False is returned if no
	 * <code>sellStock</code> or <code>restockAndSellStock</code> operations have
	 * been performed yet.
	 * 
	 * @return true if the last order processed was not fulfilled, false otherwise
	 */
	public boolean orderUnfulfilled();
	
	
	/**
	 * Returns true if the business simulation has ended because the number of
	 * days originally allocated for the job (when this class was constructed) has been
	 * exceeded.
	 * 
	 * @return true iff the current day in the ledger is greater than the initial
	 * duration for the job, false otherwise
	 */
	public boolean jobDone();
	
	
	/**
	 * Complete a day's transactions by (1)&nbsp;selling the ordered number of items, and
	 * (2)&nbsp;by turning the page in the ledger, in that order.
	 * 
	 * @param todaysOrder the number of items ordered for retail sale today
	 * @throws WarehouseException if today's order was negative
	 */
	public void sellStock(Integer todaysOrder) throws WarehouseException;
	
	
	/**
	 * Complete a day's transactions by (1)&nbsp;restocking the warehouse to
	 * capacity, (2)&nbsp;selling the ordered number of items, and (3)&nbsp;by
	 * turning the page in the ledger, in that order.  (It doesn't matter
	 * if the first step temporarily sends the cash reserve into the red, provided the
	 * balance is positive at the end of the day.)
	 * 
	 * @param todaysOrder the number of items ordered for retail sale today
	 * @throws WarehouseException if today's order was negative
	 */
	public void restockAndSellStock(Integer todaysOrder) throws WarehouseException;
		
}
