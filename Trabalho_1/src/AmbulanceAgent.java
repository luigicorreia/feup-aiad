import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AmbulanceAgent extends Agent{
    public void setup(){
        addBehaviour(new AmbulanceBehaviour(this, new ACLMessage(ACLMessage.CFP)));
    }
}