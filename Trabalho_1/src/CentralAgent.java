import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CentralAgent extends Agent {
    private Agent myAgent;
    private String pacientIllness;

    public void setup(){
        myAgent = this;
        addBehaviour(new CentralBehaviour(this, MessageTemplate.MatchPerformative((ACLMessage.REQUEST))));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("central");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        myAgent = this;
    }

    public class CentralBehaviour extends AchieveREResponder {

        public CentralBehaviour(Agent a, MessageTemplate msg){
            super(a, msg);
        }

        protected ACLMessage handleRequest(ACLMessage msg) {
            ACLMessage reply = msg.createReply();
            pacientIllness = msg.getContent();
            addBehaviour(new CallBehaviour(myAgent, new ACLMessage(ACLMessage.CFP)));
            System.out.println(pacientIllness);
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("ambulance on the way!");
            return reply;
        }

        protected  ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            return response;
        }
    }

    public class CallBehaviour extends ContractNetInitiator{

        public CallBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("ambulance");
            template.addServices(sd);


            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                for(int i=0; i<result.length; ++i) {
                    cfp.addReceiver(result[i].getName());
                }

            } catch(FIPAException fe) {
                fe.printStackTrace();
            }


            cfp.setContent("need an ambulance");

            v.add(cfp);

            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {

            System.out.println("got " + responses.size() + " responses!");

            try {
                for (int i = 0; i < responses.size(); i++) {
                    ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                    String illness = ((ACLMessage) responses.get(i)).getContent();

                    if( illness.equals(pacientIllness)) {
                        msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        msg.setContent("heart");
                        acceptances.add(msg);
                    }
                    else if(i+1 == responses.size()) {
                        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.add(msg);
                    }
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
