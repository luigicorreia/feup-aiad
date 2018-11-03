import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

public class AmbulanceAgent extends Agent{
    private Agent myAgent;

    public void setup() {
        myAgent = this;

        addBehaviour(new AmbulanceBehaviour(this, new ACLMessage(ACLMessage.CFP)));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ambulance");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public class AmbulanceBehaviour extends ContractNetInitiator {
        public AmbulanceBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("hospital");
            template.addServices(sd);


            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                for(int i=0; i<result.length; ++i) {
                    System.out.println(result[i].getName().toString());

                    cfp.addReceiver(result[i].getName());
                }

            } catch(FIPAException fe) {
                fe.printStackTrace();
            }


            cfp.setContent("need a hospital");

            v.add(cfp);

            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");

            try {
                for (int i = 0; i < responses.size(); i++) {
                    ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                    System.out.println(((ACLMessage) responses.get(i)).getContent());
                    msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
                    acceptances.add(msg);
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

    }
}