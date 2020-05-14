package question;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JProgressBar;

import java.util.Random;

// Import the student's solution to the assignment
import answer.WarehouseLedger;
import answer.WarehouseTransactions;


/**
 * The actual GUI components used to run the simulation.  (You should
 * study this code to see how your classes will be used.)
 * 
 * @author CAB302
 * @version 1.0
 */
@SuppressWarnings("serial") // We don't care about binary i/o here
public class SimulationComponents extends JPanel implements ActionListener
{

	// Default values for the simulation parameters
	public static final Long DefaultRandomSeed = 100L;
	public static final Integer DefaultWarehouseCapacity = 20; // boxes
	public static final Integer DefaultCashReserve = 80; // dollars
	public static final Integer DefaultMaxOrder = 15; //boxes
	public static final Integer DefaultWholesaleCostPerBox = 5; // dollars
	public static final Integer DefaultRetailPricePerBox = 8; // dollars
	public static final Integer DefaultDeliveryCharge = 50; // dollars
	public static final Integer DefaultJobDuration = 7; // days
	
	// Buttons
	private JButton startButton;
	private JButton restockButton;
	private JButton doNotRestockButton;
	private JPanel buttons;

	// Display for error messages
	private JTextArea display;           
	private JScrollPane textScrollPane; 

	// Mutable text fields for simulation parameters
	private JTextField seedText;       
	private JTextField capacityText;
	private JTextField cashText;
	private JTextField maxOrderText;       
	private JTextField wholesaleText;
	private JTextField retailText;       
	private JTextField deliveryText;       
	private JTextField durationText;
	
	// Progress bar for displaying warehouse stock level
	private JPanel stockPanel;
	JProgressBar stockLevel = new JProgressBar();

	// Places where we'll add components to the frame
	private enum Position {TOP, MIDDLE, BOTTOM};

	// Simulation state
	private Integer maxDailyOrder;
	private Random order;
	private WarehouseTransactions warehouse;
	private WarehouseLedger ledger;


	/*
	 * Create a new simulation and initialise all of the contained GUI components
	 */
	public SimulationComponents()
	{
		// Initialize the GUI Components
		initialiseComponents();

	}

	
	/*
	 * Initialise all the GUI components (including those that are not visible
	 * initially)
	 */
	private void initialiseComponents()
	{

		// Choose a flexible grid layout for the main frame
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);

		// Text area for displaying instructions and error messages
		display = new JTextArea(15, 40); // lines by columns
		display.setEditable(false);
		display.setLineWrap(true);
		textScrollPane = new JScrollPane(display);
		this.add(textScrollPane, makeConstraints(Position.TOP));
		resetDisplay("Set the initial simulation parameters and press 'Start'\n\n");

		// Progress bar for displaying current stock level (initially indeterminate)
		stockPanel = new JPanel();
		JLabel stockLabel = new JLabel("Current stock level:");
		stockPanel.add(stockLabel);
		stockLevel.setIndeterminate(true);
		stockLevel.setStringPainted(true);
		stockPanel.add(stockLevel);
		this.add(stockPanel, makeConstraints(Position.MIDDLE));
		
		// Add editable panels for simulation parameters
		seedText = addParameterPanel("Random number seed:", DefaultRandomSeed);
		capacityText = addParameterPanel("Warehouse capacity (boxes):", DefaultWarehouseCapacity);
		cashText = addParameterPanel("Initial cash reserve (dollars):", DefaultCashReserve);
		maxOrderText = addParameterPanel("Maximum daily order (boxes):", DefaultMaxOrder);
		wholesaleText = addParameterPanel("Wholesale cost per box (dollars):", DefaultWholesaleCostPerBox);
		retailText = addParameterPanel("Retail price per box (dollars):", DefaultRetailPricePerBox);
		deliveryText = addParameterPanel("Delivery charge (dollars):", DefaultDeliveryCharge);
		durationText = addParameterPanel("Job duration (days):", DefaultJobDuration);
		
		// Panel for buttons
		buttons = new JPanel();
		this.add(buttons, makeConstraints(Position.BOTTOM));
		buttons.setVisible(true);
		
		// Button for starting the simulation
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		this.add(startButton, makeConstraints(Position.BOTTOM));
		buttons.add(startButton);
		
		// Buttons for controlling the simulation (initially unavailable)
		restockButton = new JButton("Restock");
		restockButton.addActionListener(this);
		doNotRestockButton = new JButton("Do nothing");
		doNotRestockButton.addActionListener(this);
		buttons.add(restockButton);
		buttons.add(doNotRestockButton);
		restockButton.setVisible(false);
		doNotRestockButton.setVisible(false);

	}

	
	/*
	 * Convenience method for resetting the text in the display area
	 */
	private void resetDisplay(String initialText) {
		display.setText(initialText);
	}


	/*
	 * Convenience method for adding text to the display area without
	 * overwriting what's already there
	 */
	private void appendDisplay(String newText) {
		display.setText(display.getText() + newText);
	}

	
	/*
	 * Convenience method for creating a set of positioning constraints
	 * for a new object to be added to the main frame
	 */
	private GridBagConstraints makeConstraints(Position location) {

		final Integer inset = 20; // pixels from edge of main frame
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST; // fix component on the left margin
		constraints.gridwidth = GridBagConstraints.REMAINDER; // component occupies whole row
		switch (location) {
		case TOP:
			constraints.weightx = 100; // give leftover horizontal space to this object
			constraints.weighty = 0; // no extra vertical space for this object
			constraints.insets = new Insets(inset, inset, 0, inset); // top, left, bottom, right
			break;
		case MIDDLE:
			constraints.weightx = 100; // give leftover horizontal space to this object
			constraints.weighty = 0; // no extra vertical space for this object
			constraints.insets = new Insets(0, inset, 0, inset); // top, left, bottom, right
			break;
		case BOTTOM:
			constraints.weightx = 100; // give leftover horizontal space to this object
			constraints.weighty = 0; // no extra vertical space for this object
			constraints.insets = new Insets(0, inset, inset, inset); // top, left, bottom, right
			break;
		}
		return constraints;
	}

	
	/*
	 * Convenience method to add a labelled, editable text field to the
	 * main frame, with a fixed label and a mutable default text value
	 */
	private JTextField addParameterPanel(String label, Number defaultValue) {
		// A parameter panel has two components, a label and a text field
		JPanel parameterPanel = new JPanel();
		JLabel parameterLabel = new JLabel(label);
		JTextField parameterText = new JTextField("" + defaultValue, 3);
		// Add the label to the parameter panel
		parameterLabel.setHorizontalAlignment(JTextField.RIGHT); // flush right
		parameterPanel.add(parameterLabel);
		// Add the text field
		parameterText.setEditable(true);
		parameterText.setHorizontalAlignment(JTextField.RIGHT); // flush right
		parameterPanel.add(parameterText);
		// Add the parameter panel to the main frame
		this.add(parameterPanel, makeConstraints(Position.MIDDLE));
		// Return the newly-created text field
		return parameterText;
	}

	
	/*
	 * Perform an appropriate action when a button is pushed
	 */
	public void actionPerformed(ActionEvent event) {
		
		// Get event's source 
		Object source = event.getSource(); 

		//Consider the alternatives (not all are available at once) 
		if (source == startButton)
		{
			startSimulation();
		}
		else if (source == restockButton)
		{
			restockPushed();
			endSimulation();
		}
		else if (source == doNotRestockButton)
		{
			doNotRestockPushed();
			endSimulation();
		};
	}

	
	/*
	 * Start the simulation by accepting the initial parameters
	 */
	private void startSimulation()
	{
		try
		{	
			// Get the initial parameters as set by the user
			Long randomSeed = Long.parseLong(seedText.getText().trim());
			Integer warehouseCapacity = Integer.parseInt(capacityText.getText().trim());
			Integer cashOnHand = Integer.parseInt(cashText.getText().trim());
			maxDailyOrder = Integer.parseInt(maxOrderText.getText().trim());
			Integer wholesaleCost = Integer.parseInt(wholesaleText.getText().trim());
			Integer retailPrice = Integer.parseInt(retailText.getText().trim());
			Integer deliveryCharge = Integer.parseInt(deliveryText.getText().trim());
			Integer jobDuration = Integer.parseInt(durationText.getText().trim());
			
			// Sanity check on the user's parameters
			if (maxDailyOrder > warehouseCapacity) {
				throw new WarehouseException("Maximum daily order may not exceed warehouse capacity");
			};
			if (maxDailyOrder < 0) {
				throw new WarehouseException("Maximum daily order may not be negative");
			};
			
			// Create the warehouse ledger and transactions objects
			ledger = new WarehouseLedger(
					warehouseCapacity, // warehouse is initially fully stocked
					cashOnHand,
					wholesaleCost,
					retailPrice,
					deliveryCharge);
			warehouse = new WarehouseTransactions(
					warehouseCapacity,
					jobDuration,
					ledger);

			// Prevent further changes to simulation parameters
			seedText.setEnabled(false);
			capacityText.setEnabled(false);
			cashText.setEnabled(false);
			maxOrderText.setEnabled(false);
			wholesaleText.setEnabled(false);
			retailText.setEnabled(false);
			deliveryText.setEnabled(false);
			durationText.setEnabled(false);
		    
			// Switch buttons from set-up to simulation mode
			startButton.setVisible(false);
			restockButton.setVisible(true);
			doNotRestockButton.setVisible(true);

			// Set the length and initial value of the progress bar
			stockLevel.setMaximum(warehouseCapacity);
			stockLevel.setValue(warehouseCapacity);
			stockLevel.setIndeterminate(false);
			
			// Initialise the random number generator
			order = new Random(randomSeed);
			
			// Tell the user that the simulation has started successfully
			resetDisplay("Warehouse simulation started\n");
			
			// Display the initial state as recorded in the ledger
			appendDisplay("-----\n" + ledger.toString());
			
			
		}
		catch (NumberFormatException exception) // User has entered an invalid number
		{
			appendDisplay(exception.toString() + "\n");
		}
		catch (WarehouseException exception) // Warehouse object/s could not be initialised
		{
			appendDisplay(exception.toString() + "\n");
		}
		catch (Exception exception) // Something entirely unexpected has gone wrong!
		{
			appendDisplay("Unhandled Exception: " + exception.toString() + "\n");
			throw new RuntimeException(exception);
		}
	}

	
	private void restockPushed() {
		// Display the choice of action
		appendDisplay("Action taken: Restock; ");
		// Create today's order from retailers
		Integer todaysOrder = order.nextInt(maxDailyOrder);
		appendDisplay("Today's order: " + todaysOrder + 
				(todaysOrder == 1 ? " box" : " boxes") + "\n");
		try {
			// Restock the warehouse and then try to fulfill today's order
			warehouse.restockAndSellStock(todaysOrder);
			// Display the current stock level and ledger record
			stockLevel.setValue(ledger.inStock());
			appendDisplay("-----\n" + ledger.toString());
		} catch (Exception exception) { // Something has gone wrong with the student's solution
			appendDisplay("Unexpected exception thrown!\n" + exception.toString() + "\n");
		};
	}
	
	
	private void doNotRestockPushed() {
		// Display the choice of action
		appendDisplay("Action taken: None; ");
		// Create today's order from retailers
		Integer todaysOrder = order.nextInt(maxDailyOrder);
		appendDisplay("Today's order: " + todaysOrder + 
				(todaysOrder == 1 ? " box" : " boxes") + "\n");
		try {
			// Try to fulfill today's order
			warehouse.sellStock(todaysOrder);
			// Display the current stock level and ledger record
			stockLevel.setValue(ledger.inStock());
			appendDisplay("-----\n" + ledger.toString());
		} catch (Exception exception) { // Something has gone wrong with the student's solution
			appendDisplay("Unexpected exception thrown!\n" + exception.toString() + "\n");
		}
	}

	
	/*
	 * End the simulation if one of the termination conditions has been reached
	 */
	private void endSimulation() {
		try {
			if (warehouse.insolvent()) {
				appendDisplay("We're bankrupt - you're fired!\n" +
						"-----\nClose the window to end the simulation\n");
				restockButton.setEnabled(false);
				doNotRestockButton.setEnabled(false);
			} else if (warehouse.orderUnfulfilled()) {
				appendDisplay("You let our best customer down - you're fired!\n" +
						"-----\nClose the window to end the simulation\n");
				restockButton.setEnabled(false);
				doNotRestockButton.setEnabled(false);
			} else if (warehouse.jobDone()) {
				appendDisplay("Congratulations on a job well done!  Here's your pay.\n" +
						"-----\nClose the window to end the simulation\n");
				restockButton.setEnabled(false);
				doNotRestockButton.setEnabled(false);
			}
		} catch (Exception exception) { // Something has gone wrong with the student's solution
			appendDisplay("Unexpected exception thrown!\n" + exception.toString() + "\n");
		}
	}
	
}
