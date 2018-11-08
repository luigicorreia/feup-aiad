import com.sun.javafx.binding.StringFormatter;
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
            System.out.println("Central received call.");
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

            System.out.println("Sending ambulance request");

            cfp.setContent("need an ambulance");

            v.add(cfp);

            return v;
        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {

            System.out.println("Central got " + responses.size() + " responses!");

            Vector<String[]> allTokens = new Vector<>();

            try {
                for (int i = 0; i < responses.size(); i++) {
                    ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();
                    String ambulanceResponse = ((ACLMessage) responses.get(i)).getContent();

                    /* teste */
                    System.out.println("");
                    System.out.println("* TESTE * MSG AMBULACIA : " + ambulanceResponse);
                    System.out.println("");

                    String[] tokens = ambulanceResponse.split("-");
                    allTokens.add(tokens);

                    /*
                    if( ambulanceRespotnse.equals(pacientIllness)) {
                        msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        msg.setContent("heart");
                        acceptances.add(msg);
                    }
                    else if(i+1 == responses.size()) {
                        msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        acceptances.add(msg);
                    }
                    */
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

            int id;
            id = analyzeAmbulanceResponse(allTokens);

            ACLMessage msg = ((ACLMessage) responses.get(id)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            acceptances.add(msg);
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

        protected int analyzeAmbulanceResponse(Vector<String[]> allTokens){
            int min = 100;
            int id = 0;

            for(int i = 0; i < allTokens.size(); i++) {
                if (Integer.parseInt(allTokens.get(i)[1]) < min && pacientIllness.equals(allTokens.get(i)[0])) {
                    min = Integer.parseInt(allTokens.get(i)[1]);
                    id = i;
                }
            }

            return id;
        }

    }
}
