import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class Network {
    //this class contains a network of Events linked by Probs
    //in order to better traverse graph, construct probabilities in adjacency list
    HashMap<Event, LinkedList<Prob>> probabilities;
    
    public Network() {
		//generate events
		//must assign into probabilities at the same time
		probabilities = new HashMap<Event, LinkedList<Prob>>();
		byte letter = (byte)'A';
		addEvent(new Event((char)letter+"", 0.7f));
		letter++;
		addEvent(new Event((char)letter+"", 0.3f));
		letter++;
		addEvent(new Event((char)letter+"", 0.25f));
		letter++;
		for (int i=3; i<11; i++) {
		    addEvent(new Event((char)letter+""));
		    letter++;
		}
		//check the last event
		System.out.println("The last event is labelled as " + getLast().getName());

		//put in the probabilities
		addConditionalProbability("D", "A", 0.4f);
		addConditionalProbability("E", "B", 0.9f);
		addConditionalProbability("E", "C", 0.5f);
		addConditionalProbability("E", "!C", 0.7f);
		addConditionalProbability("F", "E", 0.9f);
		addConditionalProbability("G", "E", 0.7f);
		addConditionalProbability("H", "F", 0.1f);
		addConditionalProbability("I", "F", 0.4f);
		addConditionalProbability("I", "G", 0.3f);
		addConditionalProbability("I", "H", 0.8f);
		addConditionalProbability("J", "H", 0.75f);
		addConditionalProbability("J", "I", 0.5f);
		addConditionalProbability("G", "!E", 0.3f);
    }
    
    public static void main(String[] args) {
		//let's create a network for demonstration reasons
		Network net = new Network();
		net.showConnections();
		net.findProbability("D");
		net.findProbability("G");
		net.findProbability("E");
		
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
    	probabilities.put(e, new LinkedList<Prob>());
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
    					return;
    				} else if (e.not().getName().equals(A)) {
    					entry.getValue().add(new Prob(entry.getKey(), e.not(), prob));
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