package answer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import static java.lang.Math.ceil;

import question.WarehouseException;

/*
 * Unit tests for the WarehouseTransactions class
 * 
 * NB: Our tests concentrate on the WarehouseTransactions class, under the
 * assumption that the WarehouseLedger class works correctly (which will
 * have already been tested by LedgerTest).  The tests below don't access
 * the ledger directly except for calls to methods "inStock" and "toString",
 * both of which are considered fair game because they're called by the
 * warehouse simulator.  (Conceivably, however, a student could implement
 * the WarehouseTransactions class successfully without making "sensible"
 * use of the given ledger, by duplicating its functions.)
 * 
 */
public class TransactionsTest {

	/*
	 * Define initial values for a "typical" set of warehouse transactions
	 */
	final Integer capacity = 300; // items
	final Integer duration = 31; // days
	
	final Integer stock = capacity; // items
	final Integer cash = 1760; // dollars
	final Integer wholesale = 46; // dollars
	final Integer retail = 54; // dollars
	final Integer delivery = 99; // dollars
	
	/*
	 * Define some commonly-used Integer constants
	 */
	final Integer negative = -1;
	final Integer zero = 0;
	final Integer one = 1;
	final Integer positive = 3;
	
	/* 
	 * Declare warehouse ledger and transactions objects (note comment
	 * below)
	 */
	WarehouseLedger typicalLedger;
	WarehouseLedger anotherTypicalLedger;
	WarehouseTransactions typicalTransactions;

	
	/*
	 * Create a typical ledger, for use in constructor tests
	 * 
	 * NB: Since a WarehouseTransactions object needs a WarehouseLedger object,
	 * we have to construct a WarehouseLedger first.  Although we could use our
	 * own WarehouseLedger class for this purpose, this would doubly penalise
	 * students who implemented the WarehouseLedger class incorrectly but then
	 * relied on their buggy implementation in their WarehouseTransactions class.
	 * Therefore, the following WarehouseTransactions tests make use of the
	 * student's own WarehouseLedger class.
	 */
	@BeforeEach @Test
	public void WarehouseLedgerConstructed() throws WarehouseException {
		anotherTypicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
	}

	
	/*
	 * Test that a warehouse transactions object can be constructed
	 */
	@Test
	public void JobCompletionInitialised() throws WarehouseException {
		WarehouseTransactions typicalTransactions = 
			new WarehouseTransactions(capacity, duration, anotherTypicalLedger);
		assertFalse(typicalTransactions.jobDone()); // job must last at least one day
	}

	@Test
	public void OrderFulfillmentInitialised() throws WarehouseException {
		WarehouseTransactions typicalTransactions = 
			new WarehouseTransactions(capacity, duration, anotherTypicalLedger);
		assertFalse(typicalTransactions.orderUnfulfilled()); // no orders yet
	}
	
	@Test
	public void InsolvencyStatusInitialised() throws WarehouseException {
		WarehouseTransactions typicalTransactions = 
			new WarehouseTransactions(capacity, duration, anotherTypicalLedger);
		assertEquals(typicalTransactions.insolvent(), cash < 0); // could start in the red
	}
	
	
	/*
	 * Test that the transactions constructor throws appropriate exceptions
	 */
	@Test
	public void NegativeWarehouseCapacity() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseTransactions badTransactions =
					new WarehouseTransactions(negative, duration, anotherTypicalLedger);

		});
	}
	
	@Test
	public void NegativeJobDuration() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseTransactions badTransactions =
					new WarehouseTransactions(capacity, negative, anotherTypicalLedger);
		});
	}
	
	@Test
	public void ZeroJobDuration() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseTransactions badTransactions =
					new WarehouseTransactions(capacity, zero, anotherTypicalLedger);
		});
	}
	
	
	/*
	 * Test boundary cases for the transactions constructor
	 */
	@Test
	public void ZeroWarehouseCapacity() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseTransactions extremeTransactions = 
			new WarehouseTransactions(zero, duration, anotherTypicalLedger);
	}
	
	@Test
	public void SingleDayJob() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseTransactions extremeTransactions =
			new WarehouseTransactions(capacity, one, anotherTypicalLedger);
	}
	
	
	/*
	 * Construct a typical warehouse transactions object (and ledger) for use in subsequent tests
	 */
	@BeforeEach @Test
	public void WarehouseTransactionsConstructed() throws WarehouseException {
		typicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
		typicalTransactions = new WarehouseTransactions(capacity, duration, typicalLedger);
	}

	
	/*
	 * Test that warehouse transactions throw appropriate exceptions
	 */
	@Test
	public void NegativeWholesalePurchase() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalTransactions.sellStock(negative);
		});
	}
	
	@Test
	public void NegativeWholesalePurchaseWithRestocking() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalTransactions.restockAndSellStock(negative);
		});
	}

	
	/*
	 * Test normal behaviour of warehouse transactions
	 */
	@Test
	public void DaysProgressNormally() throws WarehouseException {
		for (int i = 1; i <= duration; i++) { // a month goes by
			assertFalse(typicalTransactions.jobDone()); // still employed
			typicalTransactions.sellStock(one);
		};
		assertTrue(typicalTransactions.jobDone()); // on the dole
	}
	
	@Test
	public void DaysProgressNormallyWithRestocking() throws WarehouseException {
		for (int i = 1; i <= duration; i++) { // a month goes by
			assertFalse(typicalTransactions.jobDone()); // still employed
			typicalTransactions.restockAndSellStock(one);
		};
		assertTrue(typicalTransactions.jobDone()); // on the dole
	}
	
	@Test
	public void RunningOutOfStock() throws WarehouseException {
		assertFalse(typicalTransactions.orderUnfulfilled());
		typicalTransactions.sellStock(stock); // sell everything
		assertFalse(typicalTransactions.orderUnfulfilled());
		typicalTransactions.sellStock(one); // try to sell one more
		assertTrue(typicalTransactions.orderUnfulfilled());
	}
	
	@Test
	public void RunningOutOfStockWithRestocking() throws WarehouseException {
		assertFalse(typicalTransactions.orderUnfulfilled());
		typicalTransactions.restockAndSellStock(capacity); // sell a whole warehouse full
		assertFalse(typicalTransactions.orderUnfulfilled());
		typicalTransactions.sellStock(one); // try to sell one more
		assertTrue(typicalTransactions.orderUnfulfilled());
	}
	
	@Test
	public void RunningOutOfCash() throws WarehouseException {
		Integer eachDaysLoss = delivery - (retail - wholesale);
		Integer daysOfProfitability = Double.valueOf(ceil((double)cash / eachDaysLoss)).intValue();
		for (int i = 1; i <= daysOfProfitability; i++) { // keep trading at a loss
			assertFalse(typicalTransactions.insolvent()); // we're not broke yet
			typicalTransactions.restockAndSellStock(one); 
		};
		assertTrue(typicalTransactions.insolvent()); // bankrupt!
	}
	
	@Test
	public void MiscellaneousTransactions() throws WarehouseException {
		// A totally arbitrary and hardwired bunch of transactions intended
		// to ensure that "mixing" operations doesn't cause any problems
		//
		assertFalse(typicalTransactions.orderUnfulfilled());
		assertFalse(typicalTransactions.insolvent());
		assertFalse(typicalTransactions.jobDone());	
		//
		// Day 1: Cash reserve = $1760; Items in stock = 300
		// Action taken: None; Today's order: 235 boxes
		typicalTransactions.sellStock(235);
		//
		// Day 2: Cash reserve = $14450; Items in stock = 65
		// Action taken: Restock; Today's order: 10 boxes
		typicalTransactions.restockAndSellStock(10);
		//
		// Day 3: Cash reserve = $4081; Items in stock = 290
		// Action taken: None; Today's order: 34 boxes
		typicalTransactions.sellStock(34);
		assertFalse(typicalTransactions.orderUnfulfilled());
		assertFalse(typicalTransactions.insolvent());
		assertFalse(typicalTransactions.jobDone());
		//
		// Day 4: Cash reserve = $5917; Items in stock = 256
		// Action taken: None; Today's order: 228 boxes
		typicalTransactions.sellStock(228);
		// 
		// Day 5: Cash reserve = $18229; Items in stock = 28
		// Action taken: Restock; Today's order: 211 boxes
		typicalTransactions.restockAndSellStock(211);
		//
		// Day 6: Cash reserve = $17012; Items in stock = 89
		// Action taken: None; Today's order: 186 boxes
		typicalTransactions.sellStock(186);
		//
		// Day 7: Cash reserve = $21818; Items in stock = 0
		// You let our best customer down - you're fired!
		assertTrue(typicalTransactions.orderUnfulfilled());
		assertFalse(typicalTransactions.insolvent());
		assertFalse(typicalTransactions.jobDone());
	}
	
	
	/*
	 * Test boundary cases for warehouse transactions
	 */
	@Test
	public void SellingNothingStockUnchanged() throws WarehouseException {
		typicalTransactions.sellStock(zero); // sell nothing
		assertEquals(typicalLedger.inStock(), stock); // stock level unchanged
	}
	
	@Test
	public void SellingNothingOrderFulfilled() throws WarehouseException {
		typicalTransactions.sellStock(zero); // sell nothing
		assertFalse(typicalTransactions.orderUnfulfilled()); // customer is easily pleased!
	}
	
	@Test
	public void SellingEverythingNoStockLeft() throws WarehouseException {
		typicalTransactions.sellStock(stock); // sell everything
		assertEquals(typicalLedger.inStock(), zero); // no stock
	}
	
	@Test
	public void SellingEverythingOrderFulfilled() throws WarehouseException {
		typicalTransactions.sellStock(stock); // sell everything
		assertFalse(typicalTransactions.orderUnfulfilled()); // order was (just!) filled
	}
	
	@Test
	public void RestockingAndSellingNothing() throws WarehouseException {
		typicalTransactions.sellStock(positive); // sell some stuff
		typicalTransactions.restockAndSellStock(zero); // restock and don't sell any more
		assertEquals(typicalLedger.inStock(), capacity); // now at capacity
	}
	
	@Test
	public void RestockingWhenAlreadyFull() throws WarehouseException {
		typicalTransactions.restockAndSellStock(positive); // pointlessly restock, then sell
		assertEquals(typicalLedger.inStock(), Integer.valueOf(stock - positive)); // restocking had no effect
		// Note: We could also directly check the cash reserve in the ledger
		// at this point to ensure that the delivery charge was paid, but because
		// the warehouse simulator never accesses the ledger directly in this way such
		// a test could be considered unfair by the students.  (This feature is
		// checked by the following test anyway.)
	}

	@Test
	public void RunningOutOfCashByPayingForEmptyDeliveryTrucks() throws WarehouseException {
		Integer maxDeliveriesAffordable = Double.valueOf(ceil((double)cash / delivery)).intValue();
		for (int i = 1; i <= maxDeliveriesAffordable; i++) { // keep paying delivery charges
			assertFalse(typicalTransactions.insolvent()); // we're not broke yet
			typicalTransactions.restockAndSellStock(zero); // pay for empty delivery truck
		};
		assertTrue(typicalTransactions.insolvent()); // go bankrupt without shifting any stock!
	}
}
