import java.util.LinkedList;
import java.util.Map;

public class Network {
    //this class contains a network of Events linked by Probs
    LinkedList<Event> events;
    //in order to better traverse graph, construct probabilities in adjacency list
    Map<Event, LinkedList<Prob>> probabilities;
    Prob[] probs;
    
    public Network() {
		//generate events
		events = new LinkedList<Event>();
		//must assign into probabilities at the same time
		probabilities = new Map<Event, LinkedList<Prob>>();
		byte letter = (byte)'A';
		events.add(new Event((char)letter+"", 0.7f));
		letter++;
		events.add(new Event((char)letter+"", 0.3f));
		letter++;
		events.add(new Event((char)letter+"", 0.25f));
		letter++;
		for (int i=3; i<events.length; i++) {
		    events.add(new Event((char)letter+"");
		    letter++;
		}
		System.out.println("The last event is labelled as " + events.getLast().getName());

		/*
		probabilities = new Prob[13];
		probabilities[0] = new Prob(events[3], events[0], 0.4f);
		probabilities[1] = new Prob(events[4], events[1], 0.9f);
		probabilities[2] = new Prob(events[4], events[2], 0.5f);
		probabilities[3] = new Prob(events[4], events[2].not(), 0.7f);
		probabilities[4] = new Prob(events[5], events[4], 0.9f);
		probabilities[5] = new Prob(events[6], events[4], 0.7f);
		probabilities[6] = new Prob(events[7], events[5], 0.1f);
		probabilities[7] = new Prob(events[8], events[5], 0.4f);
		probabilities[8] = new Prob(events[8], events[6], 0.3f);
		probabilities[9] = new Prob(events[8], events[7], 0.8f);
		probabilities[10] = new Prob(events[9], events[7], 0.75f);
		probabilities[11] = new Prob(events[9], events[8], 0.5f);
		probabilities[12] = new Prob(events[6], events[4].not(), 0.3f);
		*/
    }
    
    public static void main(String[] args) {
		//let's create a network for demonstration reasons
		Network net = new Network();
		net.showConnections();
		net.findProbability(3);
		net.findProbability(6);
		net.findProbability(4);
		
    }
    
    public void showConnections() {
		for (int i=0; i<probabilities.length; i++) {
		    System.out.println("P(" + probabilities[i].getEvent().getName() + "|" + 
			probabilities[i].getConditional().getName() + ") = " + probabilities[i].getProb());
		}
    }
    
    //calculate P(B|A)
    public void calculateProbability(Event A, Event B) {
		//need to look for A having prior probability
		
		
		//find B as a resultant in conditional probability listing
    }
    
    //calculate P(A)
    private float calculateProbability(Event A) {
		//can only make use of prior probabilities
		//check if A has a prior probability
		if (A.hasPrior()) {
		    return A.getProb();
		} else {
		    //this means we need to look through the conditional probability table
		    for (int i=0; i<probabilities.length; i++) {
				if (probabilities[i].getEvent().equals(A)) {
				    //must calculate this
				    System.out.println("Searching for P(" + probabilities[i].getEvent().getName() + "|!" + 
						probabilities[i].getConditional().getName() + ")");
				    Event counterA = probabilities[i].getConditional().not();
				    for (int j=i+1; j<probabilities.length; j++) {
				    	if (probabilities[j].getConditional().equals(counterA)
				    			&& probabilities[i].getEvent().equals(A)) {
							A.setProb((probabilities[i].getProb() 
								* calculateProbability(probabilities[i].getConditional()))
								+  (probabilities[j].getProb() 
								* calculateProbability(probabilities[j].getConditional())), true);
							return A.getProb();
				    	}
				    }
				}
		    }
		    
		}
		return -1;
    }
    
    public void findProbability(String eventName) {
    	Event event = events.findEvent(eventName);
    	System.out.println("Calculating probability for " + event.getName());
    	float prob = calculateProbability(event);
    	if (prob == -1) {
    		System.out.println("Probability incalculable");
    	} else {
			System.out.println("The probability of " + event.getName() 
				+ " is " + prob);
		}
    }

    private Event findEvent(String name) {
    	//traverses event list to find the event
    	ListIterator iterator = events.getIterator();
    	while (iterator.hasNext()) {
    		Event A = iterator.next();
    		if (A.getName().equals(name))
    			return A;
    	}

    	//if we complete the iterator and can't find the event, we don't have it
    	System.err.println("Couldn't find event " + name);
    	return null;
    }

}