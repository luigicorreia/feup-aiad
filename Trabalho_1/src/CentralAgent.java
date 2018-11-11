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
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CentralAgent extends Agent {
    private Agent myAgent;
    private Vector<String> patientIllnesses = new Vector();

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
            patientIllnesses.add(msg.getContent());
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
            cfp.setContent("Patient needs help");
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
                    String[] tokens = ambulanceResponse.split("-");
                    allTokens.add(tokens);
                    System.out.println(ambulanceResponse);
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

            Vector<Pair<Integer,Integer>> ambulanceAssignements = analyzeInfo(allTokens);

            for(int i = 0; i < ambulanceAssignements.size(); i++) {
                int id = ambulanceAssignements.get(i).getValue();
                ACLMessage msg = ((ACLMessage) responses.get(id)).createReply();
                msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                msg.setContent(Integer.toString(ambulanceAssignements.get(i).getKey()));
                acceptances.add(msg);
            }
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

        protected Vector<Pair<Integer,Integer>> analyzeInfo(Vector<String[]> tokens) {


            Vector<Pair<Integer,Integer>> ambulanceAssignments = new Vector();


            for(int j = 0; j < patientIllnesses.size(); j++) {
                int min = 100;
                int id = -1;
                int min2op = 100;
                int id2op = 0;

                for (int i = 0; i < tokens.size(); i++) {
                    if (Integer.parseInt(tokens.get(i)[1]) < min && patientIllnesses.get(j).equals(tokens.get(i)[0])) {
                        min = Integer.parseInt(tokens.get(i)[1]);
                        id = i;
                    }

                    if (Integer.parseInt(tokens.get(i)[1]) < min2op) {
                        min2op = Integer.parseInt(tokens.get(i)[1]);
                        id2op = i;
                    }
                }
                if(id == -1){
                    id = id2op;
                }
                String[] replacement = new String[]{tokens.get(id)[0], "100"};
                tokens.set(id,replacement);
                ambulanceAssignments.add(new Pair<Integer, Integer>(j,id));
            }

            return ambulanceAssignments;
        }
    }
}
