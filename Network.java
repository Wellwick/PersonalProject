import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Scanner; //temporary user input

//everything needed for visual element
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Network implements ActionListener {
    //this class contains a network of Events linked by Probs
    //in order to better traverse graph, construct probabilities in adjacency list
    HashMap<Event, LinkedList<Prob>> probabilities;
    DrawPanel dp;
    JFrame frame;
    
    public Network() {
	//means we are started with a empty network
	probabilities = new HashMap<Event, LinkedList<Prob>>();
	makeGUI();
	frame.setContentPane(getContentPane());
    }

    public Network(String filename) {
	makeGUI();
	load(filename);
    }
    
    public static void main(String[] args) {
	if (args.length > 0) {
	    if (args[0].equals("-l") && args.length > 1) {
		//load in from the file specified
		Network net = new Network(args[1]);
		net.scanUserInput();
	    } else if (args[0].equals("-n")) {
		//allow user to create a new thing
		Network net = new Network();
		net.scanUserInput();
	    } else {
		System.out.println("To load an existing file use -l <FILENAME>");
		System.out.println("To begin a new network use -n");
	    } 
	} else {
	    //let's create a network for demonstration reasons
	    Network net = new Network("DEFAULT.bys");
	    net.showConnections();
	    net.findProbability("D");
	    //net.findProbability("G");
	    net.findProbability("E");
	    net.findProbability("F", "C");
	    net.scanUserInput();
	}
    }
    
    //create and display the GUI
    private void makeGUI() {
	frame = new JFrame("Bayesian Network");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	frame.setJMenuBar(getMenuBar());
	frame.setContentPane(getContentPane());

	frame.setSize(1280, 720);
	frame.setVisible(true);
    }
    
    //making the menu bar
    private JMenuBar getMenuBar() {
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem; //stores new items temporarily for addition
	
	menuBar = new JMenuBar();
	
	//menu specifically for file operations
	menu = new JMenu("File");
	menuBar.add(menu);
	
	menuItem = new JMenuItem("New Network");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	
	menuItem = new JMenuItem("Load");
	menuItem.addActionListener(this);
	menu.add(menuItem);

	menuItem = new JMenuItem("Save");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	
	menuItem = new JMenuItem("Quit");
	menuItem.addActionListener(this);
	menu.add(menuItem);
	
	return menuBar;
    }
    
    //get the content pane -- currently empty
    private Container getContentPane() {
	dp = new DrawPanel();
	return dp;
    }
    
    //catches action events from the menu
    public void actionPerformed(ActionEvent e) {
	JMenuItem source = (JMenuItem)(e.getSource());
	switch (source.getText()) {
	case "New Network":
	    //make a new network
	    System.out.println("Making a new network");
	    load(null);
	    break;
	case "Load":
	    //load a previous network
	    System.out.println("Loading a network");
	    FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
	    fd.setDirectory(".");
	    fd.setFilenameFilter((dir, name) -> name.endsWith(".bys")); //requires Java 8
	    fd.setVisible(true);
	    String filename = fd.getFile();
	    //fd.dispatchEvent(new WindowEvent(fd, WindowEvent.WINDOW_CLOSING));
	    if (filename == null)
		System.out.println("Load was cancelled");
	    else
		load(filename);
	    break;
	case "Save":
	    //load a previous network
	    System.out.println("Saving network");
	    frame.setAlwaysOnTop(false);
	    fd = new FileDialog(frame, "Save Network", FileDialog.SAVE);
	    fd.setDirectory(".");
	    fd.setFilenameFilter((dir, name) -> name.endsWith(".bys"));
	    fd.setVisible(true);
	    filename = fd.getFile();
	    //Save dialog makes sure that the file doesn't already exist
	    
	    if (filename == null)
		System.out.println("Save was cancelled");
	    else
		save(filename);
	    break;
	case "Quit":
	    System.out.println("Quitting");
	    System.exit(0);
	}
    }
    
    //method to handle reading of user requests
    private void scanUserInput() {
	Scanner s = new Scanner(System.in);
	boolean alive = true;
	boolean changes = false;
	while (alive) {
	    try {
		System.out.println();
		System.out.print("-> ");
		String parse = s.nextLine() + ' ';
		System.out.println();
		String command = parse.substring(0, parse.indexOf(' '));
		switch (command) { //requires jdk version 7 and up
		case "help": //lists the commands for the client
		    System.out.println("These are the various commands for creating Bayesian Networks");
		    System.out.println();
		    System.out.println("New Event (no prior probability):   ne \"<EVENT NAME>\"");
		    System.out.println("New Event (with prior probability): nep \"<EVENT NAME>\" <PROBABILITY>");
		    System.out.println("New Conditional Probability:        ncp \"<EVENT>\"|\"<COND EVENT>\" <PROB>");
		    System.out.println("Show all probabilities known:       list");
		    System.out.println("Calculate probability for event:    get \"<EVENT>\"");
		    System.out.println("Save Network:                       save \"<FILENAME>\"");
		    System.out.println("Load Network:                       load \"<FILENAME>\"");
		    System.out.println("Quit Program:                       exit");
		    break;
		case "ne": //new event
		    String event = parse.substring(parse.indexOf('\"')+1);
		    String pos = event.substring(event.indexOf('\"')+2);
		    event = event.substring(0, event.indexOf('\"'));
		    int x = Integer.parseInt(pos.substring(0,pos.indexOf(' ')));
		    String yPos = pos.substring(pos.indexOf(' ')+1);
		    int y = Integer.parseInt(yPos.substring(0,yPos.indexOf(' ')));
		    if (addEvent(new Event(event, x, y))) changes = true;
		    dp.updateUI();
		    break;
		case "nep": //new event with probability
		    event = parse.substring(parse.indexOf('\"')+1);
		    String extra = event.substring(event.indexOf('\"')+2);
		    float prob = Float.parseFloat(extra.substring(0, extra.indexOf(' ')));
		    extra = extra.substring(extra.indexOf(' ')+1);
		    x = Integer.parseInt(extra.substring(0, extra.indexOf(' ')));
		    extra = extra.substring(extra.indexOf(' ')+1);
		    y = Integer.parseInt(extra.substring(0, extra.indexOf(' ')));
		    event = event.substring(0, event.indexOf('\"'));
		    if (addEvent(new Event(event, prob, x, y))) changes = true;
		    dp.updateUI();
		    break;
		case "ncp":
		    event = parse.substring(parse.indexOf('\"')+1);
		    String condEvent = event.substring(event.indexOf("|\"")+2);
		    String val = condEvent.substring(condEvent.indexOf('\"')+2);
		    System.out.println(val);
		    prob = Float.parseFloat(val);
		    event = event.substring(0, event.indexOf('\"'));
		    condEvent = condEvent.substring(0, condEvent.indexOf('\"'));
		    if (addConditionalProbability(event, condEvent, prob)) changes = true;
		    dp.updateUI();
		    break;
		case "list":
		    showConnections();
		    break;
		case "get":
		    event = parse.substring(parse.indexOf('\"')+1);
		    event = event.substring(0, event.indexOf('\"'));
		    if (findProbability(event)) changes = true;
		    break;
		case "save": //save the file
		    String filename = parse.substring(parse.indexOf('\"')+1);
		    filename = filename.substring(0, filename.indexOf('\"'));
		    if (save(filename)) changes = false;
		    break;
		case "load":
		    filename = parse.substring(parse.indexOf('\"')+1);
		    filename = filename.substring(0, filename.indexOf('\"'));
		    if ((new File(filename).exists()) && changes) {
			System.out.print("Unsaved changes will be lost. Continue y/n ");
			String answer = s.next();
			System.out.println();
			if (!answer.equals("y")) {
			    //load has been cancelled
			    //treat as a cancel if they don't input y
			    if (!answer.equals("n")) System.err.println("Unrecognised response");
			    break;
			}
		    }
		    load(filename);
		    break;
		case "exit": //quit the program
		    alive = false;
		    break;
		default: 
		    System.err.println("Unrecognised command");
		    System.out.println("Use command \"help\" for usage");
		}
	    } catch (StringIndexOutOfBoundsException e) {
		System.err.println("Paramaters specification not matched");
		System.out.println("Use command \"help\" for usage");
	    } 
	}
	System.exit(0);
    }
    
    //method to load a file
    private void load(String filename) {
	//generate events
	//must assign into probabilities at the same time
	probabilities = new HashMap<Event, LinkedList<Prob>>();
	//null can be passed to load to begin a new network
	if (filename == null) {
	    dp.updateUI();
	    return;
	}
	FileInputStream is = null;
	try {
	    //read in from the file
	    is = new FileInputStream(new File(filename));
	    
	    //initially have to read in the Event names
	    boolean events = true;
	    String newEvent = "";
	    char nextChar;
	    try {
		try {
		    //always get the first character for 
		    nextChar = (char)is.read();
		    if (nextChar == '\\')
			nextChar = (char)is.read();
		    
		    newEvent = newEvent + nextChar;
		} catch (EOFException e) { 
		    //just means we have an empty file with no events
		    return;
		}
		
		while (events) {
		    try {
			while ((nextChar = (char)is.read()) != ',' && nextChar != '=') {
			    //make sure the newly read character isn't escape character
			    if (nextChar == '\\') 
				nextChar = (char)is.read();
			    newEvent = newEvent + nextChar;
			}
		    } catch (EOFException e) {
			//reached the end of the file while producing a new event
			throw new IOException();
		    }
		    float val = -1;
		    switch (nextChar) {
		    case '=':
			//time to parse some numbers
			byte[] prob = new byte[4];
			prob[0] = (byte)is.read();
			prob[1] = (byte)is.read();
			prob[2] = (byte)is.read();
			prob[3] = (byte)is.read();
			//need to carry on and read the next 2 ints
			val = ByteBuffer.wrap(prob).getFloat();
		    case ',':
			//now need to parse two ints
			byte[] x = new byte[4];
			byte[] y = new byte[4];
			x[0] = (byte)is.read();
			x[1] = (byte)is.read();
			x[2] = (byte)is.read();
			x[3] = (byte)is.read();
			y[0] = (byte)is.read();
			y[1] = (byte)is.read();
			y[2] = (byte)is.read();
			y[3] = (byte)is.read();
			if (val != -1)
			    addEvent(new Event(newEvent, val, ByteBuffer.wrap(x).getInt(), ByteBuffer.wrap(y).getInt()));
			else
			    addEvent(new Event(newEvent, ByteBuffer.wrap(x).getInt(), ByteBuffer.wrap(y).getInt()));
			break;
		    }
		    //need to continue reading events
		    try {
			nextChar = (char)is.read();
			if (nextChar == '#') {
			    //time to move on to conditional events
			    events = false;
			} else if (nextChar !='/') {
			    //we have a problem!
			    //may just be at the end of the file
			    if ((byte)nextChar == -1) throw new EOFException();
			    else throw new UnexpectedCharacterException();
			}
		    } catch (EOFException e) {
			//all finished with events and no conditional events
			return;
		    } 
		    
		    newEvent = "";
		}
	    } catch (UnexpectedCharacterException e) {
		System.err.println("Unexpected character in the file, crashing");
		System.exit(1);
	    } 
	    
	    System.out.println("Reading conditional events");
	    //now it will begin expressing conditional events
	    boolean eof = false;
	    while (!eof) {
		nextChar = (char)is.read();
		//treat it like we are starting a new probability
		if (nextChar == '\\')
		    nextChar = (char)is.read();
		String event = "" + nextChar;
		while ((nextChar = (char)is.read()) != '|') {
		    if (nextChar == '\\')
			nextChar = (char)is.read();
		    event = event + nextChar;
		}
		String condEvent = "";
		while ((nextChar = (char)is.read()) != '=') {
		    if (nextChar == '\\')
			nextChar = (char)is.read();
		    condEvent = condEvent + nextChar;
		}
		//now have both events, read the actual probability
		byte[] prob = new byte[4];
		prob[0] = (byte)is.read();
		prob[1] = (byte)is.read();
		prob[2] = (byte)is.read();
		prob[3] = (byte)is.read();
		nextChar = (char)is.read();
		if ((byte)nextChar == -1) eof = true;
		addConditionalProbability(event, condEvent, ByteBuffer.wrap(prob).getFloat());
	    }
	} catch (FileNotFoundException e) {
	    System.err.println("Error occured while reading the file");
	    System.exit(1);
	} catch (IOException e) {
	    System.err.println("Error occured while reading the file");
	    System.exit(1);
	} finally {
	    try { if (is != null) is.close(); } catch (IOException e) { }
	}
	dp.updateUI();
    }
    
    //method to save the file, returns true on success
    private boolean save(String filename) {
	FileOutputStream os = null;
	try {
	    //prepare the output stream
	    /*
	    File file = new File(filename);
	    if (file.exists()) {
		//make sure the user doesn't want to overwrite this file
		System.out.print("The file " + filename + " already exists. Are you sure you want to replace it y/n ");
		JOptionPane.showMessageDialog(null, "The file " + filename + " already exists. Are you sure you want to replace it y/n ");
		Scanner s = new Scanner(System.in);
		String answer = s.next();
		System.out.println();
		if (!answer.equals("y")) {
		    //save has been cancelled
		    //treat failed y as exit
		    if (!answer.equals("n")) System.err.println("Unrecognised response");
		    return false;
		}
	    }
	    */
	    os = new FileOutputStream(new File(filename));
	    
	    //work through the events first
	    Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	    while (iterator.hasNext()) {
		Event event = iterator.next().getKey();
		//need to output to file and escape special characters
		String name = event.getName();
		if (!writeEvent(name, os)) return false;
		
		//event may have prior probability
		if (event.hasPrior()) {
		    os.write((byte)'=');
		    byte[] prob = ByteBuffer.allocate(4).putFloat(event.getProb()).array();
		    os.write(prob[0]);
		    os.write(prob[1]);
		    os.write(prob[2]);
		    os.write(prob[3]);
		} else {
		    //have to seperate for x and y coordinates
		    os.write((byte)',');
		}
		byte[] x = ByteBuffer.allocate(4).putInt(event.getX()).array();
		byte[] y = ByteBuffer.allocate(4).putInt(event.getY()).array();
		os.write(x[0]);
		os.write(x[1]);
		os.write(x[2]);
		os.write(x[3]);
		os.write(y[0]);
		os.write(y[1]);
		os.write(y[2]);
		os.write(y[3]);
		if (iterator.hasNext()) os.write((byte)'/');
	    }
	    //now all events have been written we write out the conditional 
	    //probabilities, reset iterator
	    iterator = probabilities.entrySet().iterator();
	    //may not need to seperate if there are no events
	    boolean seperator = false;
	    while (iterator.hasNext()) {
		Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
		Iterator<Prob> iter = entry.getValue().descendingIterator();
		while (iter.hasNext()) {
		    if (!seperator) {
			//means we need to specify there will be some conditional 
			//probability
			os.write('#');
			seperator = true;
		    } else {
			os.write((byte)'/');
		    } 
		    Prob condProb = iter.next();
		    writeEvent(condProb.getEvent().getName(), os);
		    os.write('|');
		    writeEvent(condProb.getConditional().getName(), os);
		    os.write('=');
		    byte[] prob = ByteBuffer.allocate(4).putFloat(condProb.getProb()).array();
		    os.write(prob[0]);
		    os.write(prob[1]);
		    os.write(prob[2]);
		    os.write(prob[3]);
		}
	    }
	    
	} catch (FileNotFoundException e) { 
	    System.out.println("File doesn't exist");
	    return false;
	} catch (IOException e) {
	    //something bad happened
	    return false;
	} finally {
	    try {if (os != null) os.close(); } catch (IOException e) { }
	} 
	return true;
    }
    
    //method to output event string with proper escaping characters
    private boolean writeEvent(String name, FileOutputStream os) {
	try {
	    for (int i = 0; i < name.length(); i++) {
		char character = name.charAt(i);
		switch (character) {
		case '#':
		case '/':
		case '=':
		case '|':
		    os.write((byte)'\\');
		}
		os.write((byte)character);
	    }
	} catch (IOException e) { return false; }
	return true;
    }
    
    public void showConnections() {
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	//show the base events first
	while (iterator.hasNext()) {
	    Event e = iterator.next().getKey();
	    if (e.hasPrior())
		System.out.println("P(" + e.getName() + ") = " + e.getProb());
	    else //demonstrate this is missing
		System.out.println("P(" + e.getName() + ") = ?");
	}
	iterator = probabilities.entrySet().iterator(); //reset iterator
	while (iterator.hasNext()) {
	    Map.Entry<Event, LinkedList<Prob>> event = iterator.next(); {
		if (event.getValue() != null) {
		    Iterator<Prob> iter = event.getValue().descendingIterator();
		    while (iter.hasNext()) {
			Prob p = iter.next();
			System.out.println("P(" + p.getEvent().getName() + "|" +
			    p.getConditional().getName() + ") = " + p.getProb());
		    }
		}
	    }
	}
    }
    
    //calculate P(B|A)
    public float calculateProbability(Event A, Event B) {
	/**
	*** This is calculated by the formula
	*** P(B|A) = P(AnB)/P(A) = P(A|B)*P(B) / (P(A|B)*P(B) + P(A|!B)*P(!B))
	*** This may require forward checking
	*** We should initially check to see if the conditional probability already exists
	**/
	//may be a trivial case
	if (A.equals(B)) return 1.0f;
	else if (A.equals(B.not())) return 0.0f;
	
	//store entry for A if we arrive at it
	boolean otherEventChecked = false;
	Map.Entry<Event, LinkedList<Prob>> entryA;
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
	    if (entry.getKey().equals(A) || entry.getKey().equals(A.not())) {
		entryA = entry;
		if (otherEventChecked) break;
		else otherEventChecked = true;
	    } else if (entry.getKey().equals(B) || entry.getKey().equals(B.not())) {
		Iterator<Prob> iter = entry.getValue().descendingIterator();
		while (iter.hasNext()) {
		    Prob prob = iter.next();
		    if (prob.getEvent().equals(B) && prob.getConditional().equals(A))
			return prob.getProb();
		    else if (prob.getEvent().equals(B.not()) && prob.getConditional().equals(A))
			return 1.0f - prob.getProb();
		}
	    } 
	}
	return -1;
    }
    
    //calculate P(A)
    private float calculateProbability(Event B) {
	//can only make use of prior probabilities
	//check if A has a prior probability
	if (B.hasPrior()) {
	    return B.getProb();
	} else if (B.not().hasPrior()) {
	    return B.not().getProb();
	} else {
	    //this means we need to look through the conditional probability table
	    Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	    while (iterator.hasNext()) {
		//looking for B, where is it stored
		Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
		if (entry.getKey().equals(B)) {
		    Iterator<Prob> iter = entry.getValue().descendingIterator();
		    while (iter.hasNext()) {
			//we must search for the reflex
			Prob prob1 = iter.next();
			Event A = prob1.getConditional();
			Event counterA = A.not();
			System.out.println("Searching for P(" + B.getName() + "|" + 
							counterA.getName() + ")");
			Iterator<Prob> iter2 = iter;
			while (iter2.hasNext()) {
			    Prob prob2 = iter2.next();
			    Event X = prob2.getConditional();
			    if (X.equals(counterA)) {
				//must calculate conditons probability
				float prob = calculateProbability(A);
				if (prob == -1) { //just in case we are dealing with a !NOT event
				    prob = calculateProbability(A.not());
				    B.setProb((prob1.getProb() * (1.0f - prob))
					    + (prob2.getProb() * prob), true);
				} else {
				    B.setProb((prob1.getProb() * prob)
					+ (prob2.getProb() * (1.0f - prob)), true);
				}
				return B.getProb();
			    }
			}
		    }
		}
	    }
	    
	}
	return -1;
    }
    
    public boolean findProbability(String eventName) {
	Event event = findEvent(eventName);
	if (event.hasPrior()) {
	    System.out.println(event.getName() + " has probability " + event.getProb());
	    return true;
	}
	System.out.println("Calculating probability for " + event.getName());
	float prob = calculateProbability(event);
	if (prob == -1) {
	    System.out.println("Probability incalculable");
	    return false;
	} else {
	    System.out.println("The probability of " + event.getName() 
		    + " is " + prob);
	    return true;
	}
    }
    
    public boolean findProbability(String eventA, String eventB) {
	Event A = findEvent(eventA);
	Event B = findEvent(eventB);
	
	float prob = calculateProbability(A, B);
	if (prob == -1) {
	    System.out.println("Probability incalculable");
	    return false;
	} else {
	    System.out.println("The probability of " + B.getName() + "|" + A.getName()
		    + " is " + prob);
	    return true;
	} 
    }

    private boolean addEvent(Event e) {
	//make sure this event does not already exist
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator =
	probabilities.entrySet().iterator();
	while (iterator.hasNext()) {
	    if (iterator.next().getKey().equals(e)) {
		System.out.println("The event " + e.getName() + " already exists");
		return false;
	    }
	}
	probabilities.put(e, new LinkedList<Prob>());
	System.out.println("Added event " + e.getName() + " at " + e.getX() + ", " + e.getY());
	//if (e.hasPrior()) System.out.println("Probability = " + e.getProb());
	//force a redraw
	
	return true;
    }

    private boolean addConditionalProbability(String B, String A, float prob) {
	//stored in hashmap under B
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	while (iterator.hasNext()) {
	    Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
	    if (entry.getKey().getName().equals(B) || entry.getKey().not().getName().equals(B)) {
		//can add the probability here
		//need to find the other event first
		Iterator<Map.Entry<Event, LinkedList<Prob>>> iter = probabilities.entrySet().iterator();
		while (iter.hasNext()) {
		    Event e = iter.next().getKey();
		    if (e.getName().equals(A)) {
			entry.getValue().add(new Prob(entry.getKey(), e, prob));
			System.out.println("Added conditional event " + B + "|" + A 
					    + "=" + prob);
			return true;
		    } else if (e.not().getName().equals(A)) {
			entry.getValue().add(new Prob(entry.getKey(), e.not(), prob));
			System.out.println("Added conditional event " + B + "|" + A 
					    + "=" + prob);
			return true;
		    }
		}
		//if we reach here, means that event A doesn't exist
		System.err.println("Event '" + A + "' doesn't exist");
		return false;
	    }
	}
	//finishing the iterator means we didn't find event B
	System.err.println("Event '" + B + "' doesn't exist");
	return false;
    }
    
    private boolean removeConditionalProb(Prob prob) {
	if (prob == null) return true;
	else {
	    //needs to iterate through and remove
	    Event event = prob.getEvent();
	    Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	    while (iterator.hasNext()) {
		Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
		if (entry.getKey().equals(event)) {
		    LinkedList<Prob> probs = entry.getValue();
		    return probs.remove(prob);
		}
	    }
	}
	return false;
    }
    
    private Event findEvent(String name) {
	//traverses event list to find the event
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	while (iterator.hasNext()) {
	    Event A = iterator.next().getKey();
	    if (A.getName().equals(name))
		return A;
	    else if (A.not().getName().equals(name))
		return A.not();
	}

	//if we complete the iterator and can't find the event, we don't have it
	System.err.println("Couldn't find event " + name);
	return null;
    }

    //iterate through the hashmap and find the last added event
    private Event getLast() {
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	Event e = null;
	while (iterator.hasNext()) {
	    e = iterator.next().getKey();
	}
	return e;
    }
    
    private class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {
	
	int mouseX = 0;
	int mouseY = 0;
	boolean eventSelected = false;
	boolean shiftDown = false;
	boolean addingItem = false;
	JButton calcButton = null;
	
	public DrawPanel() {
	    super();
	    addMouseListener(this);
	    addMouseMotionListener(this);
	
	    getInputMap().put(KeyStroke.getKeyStroke("control SPACE"), "pressed");
	    getInputMap().put(KeyStroke.getKeyStroke("control released SPACE"), "released");
	    getActionMap().put("pressed", new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			shiftDown = true;
			updateUI();
		    }
	    });
	    getActionMap().put("released", new AbstractAction() {
		    public void actionPerformed(ActionEvent e) {
			shiftDown = false;
			updateUI();
		    }
	    });
	}
	
	private void possEventCalc(Event event) {
	    //ask if they want to attempt a calculation
	    if (calcButton != null) //need to remove existing button
		super.remove(calcButton);
	    calcButton = new JButton("Attempt Calculation");
	    System.out.println("Added button");
	    calcButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    if (!findProbability(event.getName())) //need to let the user know the calculation was not possible
			JOptionPane.showMessageDialog(null, "Probability for " + event.getName() + " was incalculable");
		    else {
			//need to redraw and remove the button
			removeButton();
		    }
		}
	    });
	    super.add(calcButton);
	}
	
	private void removeButton() {
	    if (calcButton != null) //need to remove existing button
		super.remove(calcButton);
	    calcButton = null;
	    super.updateUI();
	}
	
	//override the paint method
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setPaint(Color.black);
	    //for each event we need to draw an Ellipse
	    Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	    while (iterator.hasNext()) {
		Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
		Event event = entry.getKey();
		if (event.getSelected()) { //set the colour to red
		    g2.setPaint(Color.red);
		    if (shiftDown) {
			g2.draw(new Line2D.Double(event.getX()+100, event.getY()+30, mouseX, mouseY));
		    }
		} else
		    g2.setPaint(Color.black);
		Ellipse2D.Double item = event.getEllipse();
		g2.draw(item);
		String name = event.getName();
		//need equal space either side on x
		FontMetrics fm = g2.getFontMetrics();
		int x = event.getX() + (50) - (fm.stringWidth(name)/2);
		int y = event.getY() + (60/2) + 5;
		g2.drawString(name, x, y);
		if (event.getSelected()) {
		    if (event.hasPrior()) {
			//print out the probability
			String newString = "P("+name+") = "+event.getProb();
			int newX = event.getX() + 50 - (fm.stringWidth(newString)/2);
			g2.drawString(newString,newX,y-35);
		    }
		}
		//now have to step through and display each showConnections
		Iterator<Prob> probs = entry.getValue().descendingIterator();
		while (probs.hasNext()) {
		    Prob prob = probs.next();
		    Event cond = prob.getConditional();
		    Event next = prob.getEvent();
		    g2.draw(new Line2D.Double(cond.getX()+100, cond.getY()+30, next.getX(), next.getY()+30));
		    //print out info if the event is selected
		    if (next.getSelected()) {
			int yChange = (next.getY()+30) - (cond.getY()+30);
			int xChange = (next.getX()) - (cond.getX()+100);
			double rotation = Math.atan2(yChange,xChange);
			//make sure rotation doesn't end up upside down
			if (rotation > Math.PI/2) 
			    rotation -= Math.PI;
			String probString = "P("+next.getName()+"|"+cond.getName()+") = "+prob.getProb();
			int stringLength = fm.stringWidth(probString)/2;
			AffineTransform orig = g2.getTransform();
			g2.translate(next.getX() - (xChange/2), next.getY()+30 - (yChange/2));
			g2.rotate(rotation);
			if (cond.getName().charAt(0) == '!') {
			    //go below the line to draw
			    g2.translate(-stringLength,15);
			    g2.drawString(probString, 0, 0);
			} else {
			    g2.translate(-stringLength,-5);
			    g2.drawString(probString, 0, 0);
			}
			g2.setTransform(orig);
		    }
		}
		
	    }
	    
	}
	
	
	public void mouseClicked(MouseEvent e) {
	    if (calcButton != null) super.remove(calcButton);
	    if (addingItem) return;
	    boolean addingConditional = false;
	    if (eventSelected && shiftDown) {
		//may be dealing with adding a new conditional event
		addingConditional = true;
	    } else eventSelected = false;
	    //make sure a newly generated event will not intersect an existing one!
	    boolean intersection = false;
	    Ellipse2D.Double newEllipse = new Ellipse2D.Double(e.getX()-50, e.getY()-30, 100, 60);
	    Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator = probabilities.entrySet().iterator();
	    Event cond = null;
	    Event connect = null;
	    Map.Entry<Event, LinkedList<Prob>> ent = null;
	    while (iterator.hasNext()) {
		Map.Entry<Event, LinkedList<Prob>> entry = iterator.next();
		Event event = entry.getKey();
		if (event.getSelected()) cond = event; //in case conditional event is added
		if (event.getEllipse().contains(e.getX(), e.getY())) {
		    if (eventSelected && shiftDown && !event.getSelected()) {
			//time to try and add a new event
			connect = event;
			ent = entry;
		    } else {
			System.out.println("You just clicked event " + event.getName());
			event.setSelected(true);
			if (event.hasPrior()) {
			    System.out.println(event.getName() + " has probability " + event.getProb());
			} else {
			    possEventCalc(event);
			} 
		    } 
		    eventSelected = true;
		    intersection = true;
		} else
		    event.setSelected(false);
		if (!eventSelected && event.getEllipse().intersects(e.getX()-50, e.getY()-30, 100, 60))
		    intersection = true;
	    }
	    dp.updateUI();
	    if (connect != null && cond != null) {
		addingItem = true;
		//generate a new frame
		JFrame probFrame = new JFrame("Add new conditional probability");
		probFrame.setAlwaysOnTop(true);
		//make sure addingItem is changed when the JFrame closes
		probFrame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
			addingItem = false;
			shiftDown = false;
		    }
		});
		JPanel probPanel = new JPanel();
		JLabel probLabel = new JLabel("P("+connect.getName()+"|"+cond.getName()+") =");
		JTextField prob = new JTextField(5);
		JLabel counterProbLabel = new JLabel("P("+connect.getName()+"|"+cond.not().getName()+") =");
		JTextField counterProb = new JTextField(5);
		JButton addProb = new JButton("Add Conditional Probability");
		
		probFrame.setContentPane(probPanel);
		probFrame.setLocation(e.getX(), e.getY());
		probFrame.setSize(350, 100);
		probFrame.setVisible(true);
		
		probPanel.add(probLabel);
		probPanel.add(prob);
		probPanel.add(counterProbLabel);
		probPanel.add(counterProb);
		probPanel.add(addProb);
		
		//check if these already exist in some form
		Prob priorA = null;
		Prob priorB = null;
		Iterator<Prob> iter = ent.getValue().descendingIterator();
		while (iter.hasNext()) {
		    Prob p = iter.next();
		    if (p.getEvent().equals(connect) && p.getConditional().equals(cond)) {
			//fill in with the existing value
			priorA = p;
			prob.setText("" + p.getProb());
		    } else if (p.getEvent().equals(connect) && p.getConditional().equals(cond.not())) {
			priorB = p;
			counterProb.setText("" + p.getProb());
		    }
		}
		final Prob pA = priorA;
		final Prob pB = priorB;
		final String eventName = connect.getName();
		final String condName = cond.getName();
		final String condNotName = cond.not().getName();
		
		addProb.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
			//allow possibility for both empty so that unwanted conditionals can be removed
			try {
			    if (!prob.getText().equals("")) {
				float probability = Float.parseFloat(prob.getText());
				addConditionalProbability(eventName, condName, probability);
			    }
			    removeConditionalProb(pA);
			    if (!counterProb.getText().equals("")) {
				float probability = Float.parseFloat(counterProb.getText());
				addConditionalProbability(eventName, condNotName, probability);
			    }
			    removeConditionalProb(pB);
			    dp.updateUI();
			    probFrame.dispatchEvent(new WindowEvent(probFrame, WindowEvent.WINDOW_CLOSING));
			} catch (NumberFormatException nf) {
			    JOptionPane.showMessageDialog(null, "The probability was not a number");
			} 
		    }
		});
	    }
	    //if we haven't found an existing event or intersecting a previous one, we can make a new event
	    if (!eventSelected && !intersection) {
		addingItem = true;
		//generate a new frame
		JFrame eventFrame = new JFrame("Add new event");
		eventFrame.setAlwaysOnTop(true);
		//make sure addingItem is changed when the JFrame closes
		eventFrame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
			addingItem = false;
			shiftDown = false;
		    }
		});
		
		JPanel eventPanel = new JPanel();
		JLabel eventNameLabel = new JLabel("Event Name: ");
		JTextField eventName = new JTextField(15);
		JLabel eventProbabilityLabel = new JLabel("Probability (optional): ");
		JTextField eventProbability = new JTextField(10);
		JButton addEvent = new JButton("Add Event");
		
		eventFrame.setContentPane(eventPanel);
		eventFrame.setLocation(e.getX(), e.getY());
		eventFrame.setSize(350, 100);
		eventFrame.setVisible(true);
		
		eventNameLabel.setBounds(0, 20, 10, 25);
		eventName.setBounds(30, 20, 100, 25);
		eventProbabilityLabel.setBounds(0, 60, 10, 25);
		eventProbability.setBounds(30, 60, 100, 25);
		addEvent.setBounds(150, 80, 60, 25);
		
		eventPanel.add(eventNameLabel);
		eventPanel.add(eventName);
		eventPanel.add(eventProbabilityLabel);
		eventPanel.add(eventProbability);
		eventPanel.add(addEvent);
		
		addEvent.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
			if (eventName.getText().equals("")) {
			    //need to make sure that there is an event name
			    JOptionPane.showMessageDialog(null, "The event name can not be empty");
			} else if (!eventProbability.getText().equals("")) {
			    try {
				float prob = Float.parseFloat(eventProbability.getText());
				if (prob > 1 || prob < 0)
				    JOptionPane.showMessageDialog(null, "Probability must fall within 0-1");
				else {
				    //time to add the event
				    addEvent(new Event(eventName.getText(), prob, e.getX()-50, e.getY()-30));
				    dp.updateUI();
				    eventFrame.dispatchEvent(new WindowEvent(eventFrame, WindowEvent.WINDOW_CLOSING));
				}
			    } catch (NumberFormatException nf) {
				JOptionPane.showMessageDialog(null, "The probability was not a number");
			    } 
			} else {
			    //add a non probability event
			    addEvent(new Event(eventName.getText(), e.getX()-50, e.getY()-30));
			    dp.updateUI();
			    eventFrame.dispatchEvent(new WindowEvent(eventFrame, WindowEvent.WINDOW_CLOSING));
			} 
		    }
		});
	    }
	}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	
	public void mouseMoved(MouseEvent e) {
	    mouseX = e.getX();
	    mouseY = e.getY();
	    if (shiftDown) {
		updateUI();
	    }
	}
	public void mouseDragged(MouseEvent e) { } 
	
    }
}

class UnexpectedCharacterException extends IOException { }

