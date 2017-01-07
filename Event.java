//need a way to demonstrate that an event has a probability of not occuring
public class Event {
    float probability;
    String name;
    boolean hasPrior = false;
    Event negation; //an event which is the counter 1 - probability
    
    //constructor for event with no prior probability
    public Event(String n) { 
        name = n;
        //make assumption that each event forces the creation of it's negation
        negation = new Event("!"+n, this); // !<Event> produces the negation
    }
    
    //constructor for negation
    public Event(String n, Event neg) {
        name = n;
        negation = neg;
    }

    //constructor for event with prior probability
    public Event(String n, float prob) {
        //if the probability is not between 0 and 1, then it is not a probability
        if (prob < 0 || prob > 1) {
            System.out.println(n + " can not have a probability outside of [0-1], removed prior probability");
            name = n;
            negation = new Event("!"+n, this);
        } else {
            name = n;
	    probability = prob;
	    hasPrior = true;
            negation = new Event("!"+n, 1.0f-prob, this); //needs a different
        }
    }

    //constructor for negation event
    public Event(String n, float prob, Event neg) {
        name = n;
        probability = prob;
        hasPrior = true;
        negation = neg;
    }
    
    //setting the probability for 
    public void setProb(float prob, boolean needsNegation) {
	hasPrior = true;
	probability = prob;
	if (needsNegation)
	    negation.setProb(1.0f-prob, false);
    }
    
    public String getName() { return name; }
    public boolean hasPrior() { return hasPrior; }
    public boolean equals(Event A) { return A.getName().equals(name); }
    public float getProb() { return probability; }
    public Event not() { return negation; }
}