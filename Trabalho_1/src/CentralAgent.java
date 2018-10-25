import jade.core.Agent;

public class CentralAgent extends Agent {
    public void setup(){
        addBehaviour(new CentralBehaviour(this));
    }
}
