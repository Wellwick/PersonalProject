import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Scanner; //temporary user input

public class Network {
    //this class contains a network of Events linked by Probs
    //in order to better traverse graph, construct probabilities in adjacency list
    HashMap<Event, LinkedList<Prob>> probabilities;
	
    public Network() {
	//means we are started with a empty network
	probabilities = new HashMap<Event, LinkedList<Prob>>();
    }

    public Network(String filename) {
	//generate events
	//must assign into probabilities at the same time
	probabilities = new HashMap<Event, LinkedList<Prob>>();
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
			while ((nextChar = (char)is.read()) != '/' && 
				    nextChar != '#' && nextChar != '=') {
			    //make sure the newly read character isn't escape character
			    if (nextChar == '\\') 
				nextChar = (char)is.read();
			    newEvent = newEvent + nextChar;
			}
		    } catch (EOFException e) {
			//reached the end of the file while producing a new event
			addEvent(new Event(newEvent));
			return;
		    }
		    switch (nextChar) {
		    case '=':
			//time to parse some numbers
			String probability = "";
			try {
			    while ((nextChar = (char)is.read()) == '.' || 
					    Character.isDigit(nextChar)) {
				probability = probability + nextChar;
			    }
			    if (nextChar == '/' || nextChar == '#') {
				//expected scenario, parse the float
				float prob = Float.parseFloat(probability);
				addEvent(new Event(newEvent, prob));
			    } else {
				throw new UnexpectedCharacterException();
			    }
			} catch (EOFException e) {
			    //finished file on a probability with existing probability
			    float prob = Float.parseFloat(probability);
			    addEvent(new Event(newEvent, prob));
			} 
			break;
		    case '#': //time for no more events
			events = false;
		    default: //this will be a new event character, carry on
			if (!newEvent.equals(""))
			    addEvent(new Event(newEvent));
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
		String probability = "";
		while ((nextChar = (char)is.read()) == '.' ||
			    Character.isDigit(nextChar)) {
		    probability = probability + nextChar;
		}
		if ((byte)nextChar == -1) eof = true;
		float prob = Float.parseFloat(probability);
		addConditionalProbability(event, condEvent, prob);
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
	    net.findProbability("G");
	    net.findProbability("E");
	}
    }

    //method to handle reading of user requests
    private void scanUserInput() {
	Scanner s = new Scanner(System.in);
	boolean alive = true;
	while (alive) {
	    String parse = s.nextLine() + ' ';
	    System.out.println();
	    String command = parse.substring(0, parse.indexOf(' '));
	    switch (command) { //requires jdk version 7 and up
	    case "help": //lists the commands for the client
		System.out.println();
		System.out.println("These are the various commands for creating Bayesian Networks");
		System.out.println();
		System.out.println("New Event (no prior probability):    ne \"<EVENT NAME>\"");
		System.out.println("New Event (with prior probability):  ne \"<EVENT NAME>\" <PROBABILITY>");
		System.out.println("New Conditional Probability:         ncp \"<EVENT>\"|\"<COND EVENT>\" <PROB>");
		System.out.println("Show all probabilities known:        list");
		System.out.println("Get/Calculate probability for event: get \"<EVENT>\"");
		System.out.println("Save Network:                        save \"<FILENAME>\"");
		System.out.println("Load Network:                        load \"<FILENAME>\"");
		System.out.println("Quit Program:                        exit");
		break;
	    case "ne": //new event
		String event = parse.substring(parse.indexOf('\"')+1);
		event = event.substring(0, event.indexOf('\"'));
		addEvent(new Event(event));
		break;
	    case "nep": //new event with probability
		event = parse.substring(parse.indexOf('\"')+1);
		float prob = Float.parseFloat(event.substring(event.indexOf('\"')+2));
		event = event.substring(0, event.indexOf('\"'));
		addEvent(new Event(event, prob));
		break;
	    
	    case "exit": //quit the program
		alive = false;
		break;
	    default: 
		System.err.println("Unrecognised command");
		System.out.println("Use command \"help\" for usage");
	    }
	}
    }
    
    //method to save the file, returns true on success
    private boolean save(String filename) {
	FileOutputStream os = null;
	try {
	    //prepare the output stream
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
		    String prob = "" + event.getProb();
		    for (int j = 0; j < prob.length(); j++) {
			os.write((byte)prob.charAt(j));
		    }
		}
		os.write((byte)'/');
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
		    }
		    Prob condProb = iter.next();
		    writeEvent(condProb.getEvent().getName(), os);
		    os.write('|');
		    writeEvent(condProb.getConditional().getName(), os);
		    os.write('=');
		    String prob = "" + condProb.getProb();
		    for (int k = 0; k < prob.length(); k++) {
			os.write((byte)prob.charAt(k));
		    }
		    os.write((byte)'/');
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
    public void calculateProbability(Event A, Event B) {
	//need to look for A having prior probability
	
	
	//find B as a resultant in conditional probability listing
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
    
    public void findProbability(String eventName) {
	Event event = findEvent(eventName);
	System.out.println("Calculating probability for " + event.getName());
	float prob = calculateProbability(event);
	if (prob == -1) {
	    System.out.println("Probability incalculable");
	} else {
	    System.out.println("The probability of " + event.getName() 
		    + " is " + prob);
	}
    }

    private void addEvent(Event e) {
	//make sure this event does not already exist
	Iterator<Map.Entry<Event, LinkedList<Prob>>> iterator =
	probabilities.entrySet().iterator();
	while (iterator.hasNext()) {
	    if (iterator.next().getKey().equals(e)) {
		System.out.println("The event " + e.getName() + " already exists");
		return;
	    }
	}
	probabilities.put(e, new LinkedList<Prob>());
	System.out.println("Added event " + e.getName());
	 
	
    }

    private void addConditionalProbability(String B, String A, float prob) {
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
			return;
		    } else if (e.not().getName().equals(A)) {
			entry.getValue().add(new Prob(entry.getKey(), e.not(), prob));
			System.out.println("Added conditional event " + B + "|" + A 
					    + "=" + prob);
			return;
		    }
		}
		//if we reach here, means that event A doesn't exist
		System.err.println("Event '" + A + "' doesn't exist");
		return;
	    }
	}
	//finishing the iterator means we didn't find event B
	System.err.println("Event '" + B + "' doesn't exist");
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
	Event e = new Event("<NO EVENTS>");
	while (iterator.hasNext()) {
	    e = iterator.next().getKey();
	}
	return e;
    }
}

class UnexpectedCharacterException extends IOException { }