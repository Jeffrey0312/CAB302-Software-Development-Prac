package asgn1Question;

/***

* A ledger is a big book in which companies keep a record of their
* commercial transactions, typically one page per day. 
* This interface defines the general characteristics of a 
* ledger that tracks three things: the current day; the current 
* stock level; and the current cash reserve. It allows new 
* transactions to be recorded and the current or earlier day's 
* totals to be inspected. For simplicity, days, stock levels and 
* cash reserves are all represented by type Integer. Days are 
* assumed to be numbered from 1, rather than 0. 
* Your first two tasks in the assignment are to implement this 
* interface as class WarehouseLedger, and to produce a 
* comprehensive unit test suite for it. 
* Constructor:
* To connect to the provided GUI, your 
* WarehouseLedger class must provide a constructor with 
* the following signature, as well as all the methods below. The current day 
* defaults to 1 when a ledger is constructed. 
* Parameters:
* Integer initialStock - initial stock level, in items
* Integer initialCash - initial cash reserve, in dollars
* Integer wholesaleCostPerItem - how much it costs to buy an item, in dollars
* Integer retailPricePerItem - how much we get from selling an item, in dollars
* Integer deliveryCharge - extra cost associated with buying items, in dollars *
* Throws:
* WarehouseException - if the stock level, wholesale cost, retail price or 
* delivery charge are negative, or if the wholesale cost is greater than 
* the retail price (but we may trade while insolvent, so no exception 
* is thrown for a negative initial cash reserve!) 
 * 
 * @author CAB302
 * @version 1.0
 *
 */
public interface Ledger {

	/**
	 * Increments the current day.  (The physical analogy is that it
	 * represents turning the page of the ledger.)  The previous day's
	 * stock level and cash reserve are carried over to the new day.
	 */
	public void nextDay();
	

	/**
	 * Updates the ledger to reflect the purchase of the required
	 * number of items at their fixed wholesale cost (provided when the
	 * ledger was constructed).  Both the number of items currently in
	 * stock and the current cash reserve are updated.  (The cash reserve
	 * is allowed to become negative, if necessary, indicating that
	 * we have overdrawn our budget.)
	 * 
	 * NB: Apart from the wholesale cost of the items, buying items
	 * also incurs a fixed delivery charge (provided when the ledger
	 * was constructed), regardless of how many items were bought.
	 * 
	 * @param required the number of items being purchased (at
	 * wholesale cost)
	 * @throws WarehouseException if the number of items to purchase
	 * is negative
	 */
	public void buyItems(Integer required) throws WarehouseException;
	
	
	/**
	 * Updates the ledger to reflect the sale of the requested number of
	 * items at their fixed retail price (provided when the ledger was
	 * constructed).  However, since it's impossible to sell more items than
	 * are in stock, the number of items actually sold will be the number
	 * requested or the number in stock, whichever is smaller.  Both the
	 * number of items currently in stock and the current cash reserve are
	 * updated.
	 * 
	 * @param requested the number of items which have been requested
	 * @return true if the order was filled in total, false if there
	 * were insufficient items in stock to fill the order
	 * @throws WarehouseException if the number of items to be sold is
	 * negative
	 */
	public boolean sellItems(Integer requested) throws WarehouseException;

		
	/**
	 * Returns the current day.  (The physical analogy is that it tells us
	 * which page the ledger is opened to.)
	 *  
	 * @return the current day (where days are counted starting at 1)
	 */
	public Integer currentDay();
	
	
	/**
	 * Returns the current cash reserve as recorded in the ledger.
	 * (This value may be negative if the company is trading while
	 * insolvent.)
	 * 
	 * @return the current cash reserve (or amount owing), in dollars
	 */
	public Integer cashAvailable();

	
	/**
	 * Returns the cash reserve at the end of the given day, or the
	 * current cash reserve if parameter <code>day</code> is today.
	 *  
	 * @param day the day for which we want to look up the cash total
	 * @return the amount of cash held in reserve (or owing) at the end
	 * of the given day, or the cash currently held if the
	 * day of interest is today, in dollars
	 * @throws WarehouseException if the given day is less than 1 or
	 * greater than the current day
	 */
	public Integer cashAvailable(Integer day) throws WarehouseException;
	
	
	/**
	 * Returns the number of items currently in stock as recorded in
	 * the ledger.  (The number of items in stock should never be
	 * negative.)
	 * 
	 * @return the number of items currently in stock
	 */
	public Integer inStock();

	
	/**
	 * Returns the number of items in stock at the end of the given day,
	 * or the current number in stock if parameter <code>day</code> is today.
	 * (The number of items in stock should never be negative.)
	 * 
	 * @param day the day for which we want to look up the number
	 * of items remaining in stock
	 * @return the number of items in stock at the end of the given day, or
	 * currently in stock if the day of interest is today
	 * @throws WarehouseException if the given day is less than 1 or
	 * greater than the current day
	 */
	public Integer inStock(Integer day) throws WarehouseException;
	
	
	/**
	 * Returns a human-readable representation of the current state of
	 * the ledger.  This is a string of the form<br><br>
	 * <code>"Day X: Cash reserve = $Y; Items in stock = Z\n"</code><br><br>
	 * where X is the current day, Y is the current cash reserve and
	 * Z is the current number of items in stock.
	 * 
	 * (Note: The root class <code>java.lang.Object</code> implements 
	 * a <code>toString</code> method, which is automatically available
	 * to <em>all</em> objects.  However, here we require your
	 * <code>WarehouseLedger</code> class to override the default
	 * <code>toString</code> method with one suitable for use in our
	 * warehouse simulation to display the current state of the business.)
	 * 
	 * @return a human-readable summary of the current state of the ledger
	 */
	public String toString();
	
}
