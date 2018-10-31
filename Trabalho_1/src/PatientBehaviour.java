import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class PatientBehaviour extends ContractNetInitiator {

    public PatientBehaviour(Agent a, ACLMessage cfp) {
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
