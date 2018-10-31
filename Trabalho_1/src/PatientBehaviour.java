import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import java.util.Vector;

public class PatientBehaviour extends AchieveREInitiator {
    public PatientBehaviour(Agent a, ACLMessage msg) {
        super(a, msg);
    }
    protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
        Vector<ACLMessage> v = new Vector<ACLMessage>();
        // ...
        return v;
    }
    protected void handleAgree(ACLMessage agree) {
        // ...
    }
    protected void handleRefuse(ACLMessage refuse) {
        // ...
    }
    protected void handleInform(ACLMessage inform) {
        // ...
    }
    protected void handleFailure(ACLMessage failure) {
        // ...
    }
}

