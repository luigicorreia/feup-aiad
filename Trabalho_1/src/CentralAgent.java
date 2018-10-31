import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CentralAgent extends Agent {

    public void setup(){
        addBehaviour(new CentralBehaviour(this));
    }
}
