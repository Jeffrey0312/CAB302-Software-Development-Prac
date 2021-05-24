package answer;

import static org.junit.jupiter.api.Assertions.*;

import answer.WarehouseLedger;
import org.junit.jupiter.api.*;
import question.WarehouseException;

/*
 * Unit tests for the WarehouseLedger class
 */
public class LedgerTest {
	
	/* 
	 * Define a typical warehouse ledger for use in various tests
	 */
	WarehouseLedger typicalLedger;
	final Integer stock = 10; // items
	final Integer cash = 100; // dollars
	final Integer wholesale = 20; // dollars
	final Integer retail = 25; // dollars
	final Integer delivery = 40; // dollars
	
	/*
	 * Define some commonly-used Integer constants
	 */
	final Integer negative = -1;
	final Integer zero = 0;
	final Integer one = 1;
	final Integer two = 2;
	
	/*
	 * Test that a typical warehouse ledger can be constructed successfully
	 */
	@Test
	public void DayInitialised() throws WarehouseException {
		WarehouseLedger typicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
		assertEquals(typicalLedger.currentDay(), one);
	}

	@Test
	public void CashInitialised() throws WarehouseException {
		WarehouseLedger typicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
		assertEquals(typicalLedger.cashAvailable(), cash);
	}
	
	@Test
	public void StockInitialised() throws WarehouseException {
		WarehouseLedger typicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
		assertEquals(typicalLedger.inStock(), stock);
	}	
	
	@Test
	public void InsolventLedgerConstructed() throws WarehouseException {
		WarehouseLedger redLedger = new WarehouseLedger(stock, negative, wholesale, retail, delivery);
		assertEquals(redLedger.cashAvailable(), negative);
	}
	
	/*
	 * Test that all expected exceptions are thrown when an invalid
	 * warehouse ledger is constructed
	 * 
	 * NB: There is no separate test for a negative retail price because this
	 * possibility is precluded by the requirements that (a) the wholesale cost
	 * must be non-negative and (b) the retail price may not be less than the
	 * wholesale cost. 
	 */
	@Test
	public void NegativeInitialStock() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseLedger badLedger = new WarehouseLedger(negative, cash, wholesale, retail, delivery);
		});
	}
	
	@Test
	public void NegativeWholesaleCost() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseLedger badLedger = new WarehouseLedger(stock, cash, negative, retail, delivery);
		});
	}
	
	@Test
	public void WholesaleCostExceedsRetailPrice() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseLedger badLedger = new WarehouseLedger(stock, cash, wholesale, wholesale - 1, delivery);
		});
	}
	
	@Test
	public void NegativeDeliverySurcharge() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			@SuppressWarnings("unused")
			WarehouseLedger badLedger = new WarehouseLedger(stock, cash, wholesale, retail, negative);
		});
	}
	
	/*
	 * Test boundary cases for warehouse ledger construction
	 */
	@Test
	public void WholesaleEqualsRetail() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(stock, cash, retail, retail, delivery);
	}
	
	@Test
	public void NoInitialStock() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(0, cash, wholesale, retail, delivery);
	}
	
	@Test
	public void NoInitialCashReserve() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(stock, 0, wholesale, retail, delivery);
	}
	
	@Test
	public void GettingStockForFree() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(stock, cash, 0, retail, delivery);
	}
	
	@Test
	public void GivingFreeStockAway() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(stock, cash, 0, 0, delivery);
	}
	
	@Test
	public void NoDeliveryCharge() throws WarehouseException {
		@SuppressWarnings("unused")
		WarehouseLedger extremeLedger = new WarehouseLedger(stock, cash, wholesale, retail, 0);
	}

	
	/*
	 * Construct a typical ledger, for use in subsequent tests
	 */
	@BeforeEach @Test
	public void WarehouseLedgerConstructed() throws WarehouseException {
		typicalLedger = new WarehouseLedger(stock, cash, wholesale, retail, delivery);
	}
	
	
	/*
	 * Tests for the normal behaviour of ledger methods
	 */
	@Test
	public void NextDayChangesDay() {
		typicalLedger.nextDay();
		assertEquals(typicalLedger.currentDay(), two);
	}
	
	@Test
	public void SellingItemsDayUnchanged() throws WarehouseException {
		final Integer sold = 2;
		assertTrue(typicalLedger.sellItems(sold)); // sell
		assertEquals(typicalLedger.currentDay(), one); // day unchanged
	}
	
	@Test
	public void SellingItemsCashIncreased() throws WarehouseException {
		final Integer sold = 2;
		assertTrue(typicalLedger.sellItems(sold)); // sell
		assertEquals(typicalLedger.cashAvailable(), Integer.valueOf(cash + (sold * retail))); // cash increased
	}
	
	@Test
	public void SellingItemsStockReduced() throws WarehouseException {
		final Integer sold = 2;
		assertTrue(typicalLedger.sellItems(sold)); // sell
		assertEquals(typicalLedger.inStock(), Integer.valueOf(stock - sold)); // stock reduced
	}

	@Test
	public void BuyingItemsDayUnchanged() throws WarehouseException {
		final Integer bought = 3;
		typicalLedger.buyItems(bought); // buy (and pay for delivery)
		assertEquals(typicalLedger.currentDay(), one); // day unchanged
	}
	
	@Test
	public void BuyingItemsCashReduced() throws WarehouseException {
		final Integer bought = 3;
		typicalLedger.buyItems(bought); // buy (and pay for delivery)
		assertEquals(typicalLedger.cashAvailable(),
				Integer.valueOf(cash - (bought * wholesale) - delivery)); // cash reduced
	}
	
	@Test
	public void BuyingItemsStockIncreased() throws WarehouseException {
		final Integer bought = 3;
		typicalLedger.buyItems(bought); // buy (and pay for delivery)
		assertEquals(typicalLedger.inStock(), Integer.valueOf(stock + bought)); // stock increased
	}
	
	@Test
	public void RunningOutOfStockCashIncreased() throws WarehouseException {
		final Integer sold = stock + 2;
		assertFalse(typicalLedger.sellItems(sold)); // sell more items than we have
		assertEquals(typicalLedger.cashAvailable(),
				Integer.valueOf(cash + (stock * retail))); // cash increased
	}	
	
	@Test
	public void RunningOutOfStockNoStockLeft() throws WarehouseException {
		final Integer sold = stock + 2;
		assertFalse(typicalLedger.sellItems(sold)); // sell more items than we have
		assertEquals(typicalLedger.inStock(), zero); // no stock left
	}
	
	@Test
	public void GoingIntoDebtCashNegative() throws WarehouseException {
		final Integer bought = 7;
		typicalLedger.buyItems(bought); // buy too many items (and pay for delivery)
		assertEquals(typicalLedger.cashAvailable(),
				Integer.valueOf(cash - ((bought * wholesale) + delivery))); // cash is negative
	}
	
	@Test
	public void GoingIntoDebtStockIncreased() throws WarehouseException {
		final Integer bought = 7;
		typicalLedger.buyItems(bought); // buy too many items (and pay for delivery)
		assertEquals(typicalLedger.inStock(), Integer.valueOf(stock + bought)); // stock increased
	}
	
	// A utility method to perform a few days' transactions for use in
	// tests that need a well-worn ledger
	public void PerformTransactions() throws WarehouseException {
		// Begin day 1 (cash = $100, stock = 10 items)
		typicalLedger.sellItems(1);
		typicalLedger.buyItems(2);
		typicalLedger.sellItems(2);
		typicalLedger.nextDay();
		// Begin day 2 (cash = $95, stock = 9 items)
		typicalLedger.buyItems(3);
		typicalLedger.buyItems(1);
		typicalLedger.nextDay();
		// Begin day 3 (cash = $-65, stock = 13 items)
		typicalLedger.sellItems(2);
		typicalLedger.sellItems(5);
		// Current time (cash = $110, stock = 6 items)
	}
	

	@Test
	public void PreviousDaysCashRemembered() throws WarehouseException {
		PerformTransactions();
		// End of day 1
		assertEquals(typicalLedger.cashAvailable(1), Integer.valueOf(95));
		// End of day 2
		assertEquals(typicalLedger.cashAvailable(2), Integer.valueOf(-65));
	}
	
	@Test
	public void PreviousDaysStockRemembered() throws WarehouseException {
		PerformTransactions();
		// End of day 1
		assertEquals(typicalLedger.inStock(1), Integer.valueOf(9));
		// End of day 2
		assertEquals(typicalLedger.inStock(2), Integer.valueOf(13));
	}
	
	@Test
	public void TransactionsCarryAcrossDays() throws WarehouseException {
		PerformTransactions();
		assertEquals(typicalLedger.currentDay(), Integer.valueOf(3));
		assertEquals(typicalLedger.cashAvailable(), Integer.valueOf(110));
		assertEquals(typicalLedger.inStock(), Integer.valueOf(6));
	}
	
	@Test
	public void LedgerIsLegible() throws WarehouseException {
		PerformTransactions();
		assertEquals(typicalLedger.toString(),
				"Day 3: Cash reserve = $110; Items in stock = 6\n");
	}
	
	/*
	 * Tests for ledger operation exceptions
	 */
	@Test
	public void AttemptToSellNegativeItems() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.sellItems(negative);
		});
	}
	
	@Test
	public void AttemptToBuyNegativeItems() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.buyItems(negative);
		});
	}
	
	@Test
	public void AttemptToSeeCashBeforeLedgerStarted() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.cashAvailable(zero); // no such day
		});
	}
	
	@Test
	public void AttemptToSeeStockBeforeLedgerStarted() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.inStock(negative);
		});
	}
	
	@Test
	public void AttemptToSeeCashInFuture() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.cashAvailable(typicalLedger.currentDay() + 1);
		});
	}
	
	@Test
	public void AttemptToSeeStockInFuture() throws WarehouseException {
		assertThrows(WarehouseException.class, () -> {
			typicalLedger.inStock(typicalLedger.currentDay() + 2);
		});
	}
	
	/*
	 * Tests for boundary behaviours of ledger methods
	 */
	@Test
	public void SellingZeroItems() throws WarehouseException {
		assertTrue(typicalLedger.sellItems(0)); // sell nothing (successfully)
		assertEquals(typicalLedger.currentDay(), one); // day unchanged
		assertEquals(typicalLedger.cashAvailable(), cash); // cash unchanged
		assertEquals(typicalLedger.inStock(), stock); // stock unchanged
	}
	
	@Test
	public void BuyingZeroItems() throws WarehouseException {
		typicalLedger.buyItems(0); // buy nothing (but still pay for delivery)
		assertEquals(typicalLedger.currentDay(), one); // day unchanged
		assertEquals(typicalLedger.cashAvailable(),
				Integer.valueOf(cash - delivery)); // cash reduced
		assertEquals(typicalLedger.inStock(), stock); // stock unchanged
	}
	
	@Test
	public void SellingEntireStock() throws WarehouseException {
		PerformTransactions();
		final Integer onShelves = typicalLedger.inStock();
		final Integer cashReserve = typicalLedger.cashAvailable();
		final Integer today = typicalLedger.currentDay();
		assertTrue(typicalLedger.sellItems(onShelves)); // sell everything (successfully)
		assertEquals(typicalLedger.currentDay(), today); // day unchanged
		assertEquals(typicalLedger.cashAvailable(),
				Integer.valueOf(cashReserve + (retail * onShelves))); // cash increased
		assertEquals(typicalLedger.inStock(), zero); // nothing left on the shelves
	}
	
	@Test
	public void CheckingTodaysCashExplicitly() throws WarehouseException {
		// Check that both versions of overloaded method work identically
		PerformTransactions();
		assertEquals(typicalLedger.cashAvailable(),
				typicalLedger.cashAvailable(typicalLedger.currentDay()));
	}
	
	@Test
	public void CheckingTodaysStockExplicitly() throws WarehouseException {
		// Check that both versions of overloaded method work identically
		PerformTransactions();
		assertEquals(typicalLedger.inStock(),
				typicalLedger.inStock(typicalLedger.currentDay()));
	}
	
}

