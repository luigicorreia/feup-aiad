import jade.core.Agent;
import jade.lang.acl.ACLMessage;

public class PatientAgent extends Agent {
    public void setup(){
        addBehaviour(new PatientBehaviour(this, new ACLMessage(ACLMessage.CFP)));
    }
}
