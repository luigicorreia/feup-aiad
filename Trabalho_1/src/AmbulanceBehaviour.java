import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class AmbulanceBehaviour extends ContractNetInitiator {
    public AmbulanceBehaviour(Agent a, ACLMessage cfp) {
        super(a, cfp);
    }

    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> messages = new Vector<>();
        return messages;
    }

    protected void handleAllResponses(Vector responses,
                                      Vector acceptances) {

    }
    protected void handleAllResultNotifications(Vector resultNotifications) {

    }

}
