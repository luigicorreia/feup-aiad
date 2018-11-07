import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import javafx.beans.binding.IntegerBinding;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class AmbulanceAgent extends Agent{


    public void setup() {
        addBehaviour(new AmbulanceBehaviour(this, new ACLMessage(ACLMessage.CFP)));
        addBehaviour(new CallResponseBehaviour(this, MessageTemplate.MatchPerformative((ACLMessage.CFP))));

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

    //Behaviour Ambulance uses to communicate with Hospital
    public class AmbulanceBehaviour extends ContractNetInitiator {
        public AmbulanceBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        //Prepares the message to send to the hospital

        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("hospital"); //Needs to search for hospital
            template.addServices(sd);

            //Adds all the hospitals as receivers
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                for(int i=0; i<result.length; ++i) {
                    //System.out.println("AmbulanceAgent search: " + result[i].getName().toString());

                    cfp.addReceiver(result[i].getName());
                }

            } catch(FIPAException fe) {
                fe.printStackTrace();
            }


            cfp.setContent("need a hospital");

            v.add(cfp);

            return v;
        }

        /*
        * Handles the responses from the hospital
        */
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            String hospitalInfo;
            Vector<String[]> allTokens = new Vector<>();
            int id;

            try {
                for (int i = 0; i < responses.size(); i++) {


                    hospitalInfo = ((ACLMessage) responses.get(i)).getContent();
                    String[] tokens = hospitalInfo.split("-");
                    allTokens.add(tokens);
                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

            id = analyzeInfo(allTokens);
            ACLMessage msg = ((ACLMessage) responses.get(id)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL); // OR NOT!
            acceptances.add(msg);
        }

        protected int analyzeInfo(Vector<String[]> tokens) {

            int min = Integer.parseInt(tokens.get(0)[2]);
            int id = Integer.parseInt(tokens.get(0)[0]);

            for(int i = 0; i < tokens.size(); i++) {
                System.out.println(tokens.get(i)[2]);

                if(Integer.parseInt(tokens.get(i)[2]) < min) {
                    min = Integer.parseInt(tokens.get(i)[2]);
                    id = i;
                }
            }

            System.out.println("minimo + id" + min + id);

            return id;
        }

        protected void handleAllResultNotifications(Vector resultNotifications) {
            //System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

    }

    public class CallResponseBehaviour extends ContractNetResponder {
        private Agent myAgent;

        public CallResponseBehaviour(Agent a, MessageTemplate mt) {
            super(a, mt);
            myAgent = a;
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {

            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

            int random = (int )(Math.random() * 4 + 1);

            if (random == 1)
                reply.setContent("heart"); //Ambulance specialized in heart problems
            else if (random == 2)
                reply.setContent("brain"); //Ambulance specialized in brain problems (like a stroke)
            else if (random == 3)
                reply.setContent("bones"); //Ambulance specialized in dealing with broken bones or other bone health problems
            else if (random == 4)
                reply.setContent("blood"); //Ambulance specialized in dealing with large hemorrhaging problems

            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            System.out.println(myAgent.getLocalName() + " got a reject...");
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            ACLMessage nullMessage = new ACLMessage();
            try {
                System.out.println(myAgent.getLocalName() + " got an accept!");
                System.out.println(accept.getContent());
                ACLMessage result = accept.createReply();
                result.setPerformative(ACLMessage.INFORM);
                result.setContent("this is the result");

                return result;
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }
            return nullMessage;
        }
    }
}