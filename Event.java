public class Event {
    float probability;
    String name;
    boolean hasPrior = false;
    
    //constructor for event with no prior probability
    public Event(String n) { name = n; }
    //constructor for event with prior probability
    public Event(String n, float prob) {
	name = n;
	probability = prob;
	hasPrior = true;
    }
    
    public String getName() { return name; }
    public boolean hasPrior() { return hasPrior; }
    public boolean equals(Event A) { return A.getName() == name; }
    public float getProb() { return probability; }
}