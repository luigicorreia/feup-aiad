import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;

import java.util.Random;
import java.util.Vector;


/**
 * This class represents the Ambulance.
 */
public class AmbulanceAgent extends Agent{
    String typeOfAmbulance = "";
    String illness = "heart";

    public void setup() {
        addBehaviour(new CallResponseBehaviour(this, MessageTemplate.MatchPerformative((ACLMessage.CFP))));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("ambulance");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            // registar ambulancia
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        Random r = new Random();
        int random = r.nextInt(4) + 1;

        switch(random) {
            //Ambulance specialized in heart problems
            case 1:
                typeOfAmbulance = "heart";
                break;
            //Ambulance specialized in brain problems (like a stroke)
            case 2:
                typeOfAmbulance = "brain";
                break;
            //Ambulance specialized in dealing with broken bones or other bone health problems
            case 3:
                typeOfAmbulance = "bones";
                break;
            //Ambulance specialized in dealing with large hemorrhaging problems
            case 4:
                typeOfAmbulance = "blood";
                break;
        }
    }

    /**
     * Behaviour Ambulance uses to communicate with Hospital
     */
    public class AmbulanceBehaviour extends  ContractNetInitiator {
        public AmbulanceBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        /**
         * Prepares the message to send to the hospital
         * @param cfp - message to delivery hospitals
         * @return v - all the hospital that receive the message
         */
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("hospital");
            template.addServices(sd);

            //Adds all the hospitals as receivers
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                for(int i=0; i<result.length; ++i) {
                    cfp.addReceiver(result[i].getName());
                }
            } catch(FIPAException fe) {
                fe.printStackTrace();
            }

            System.out.println("Looking for hospitals ...");
            System.out.println("");

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
                System.out.println("");
                System.out.println("Information about the available Hospitals:");

                for (int i = 0; i < responses.size(); i++) {
                    hospitalInfo = ((ACLMessage) responses.get(i)).getContent();

                    String[] tokens = hospitalInfo.split("-");
                    allTokens.add(tokens);

                    System.out.println(" > Hospital " + ((ACLMessage) responses.get(i)).getSender().getLocalName() +
                            " is specialist in " + tokens[0] + " and the " + "patient's distance is " + tokens[1] +
                            " km");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            System.out.println("");

            id = analyzeInfo(allTokens);

            ACLMessage msg = ((ACLMessage) responses.get(id)).createReply();
            msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
            acceptances.add(msg);

            for (int i = 0; i < responses.size(); i++) {
                if (i != id){
                    ACLMessage msg2 = ((ACLMessage) responses.get(i)).createReply();
                    msg2.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    acceptances.add(msg2);
                }
            }
        }

        /**
         * This functions analyze the distance from ambulance to hospital and pick th closest one
         * @param tokens - information about hospital (specialty and distance)
         * @return
         */
        protected int analyzeInfo(Vector<String[]> tokens) {
            int min = 100;
            int id = -1;
            int min2op = 100;
            int id2op = 0;

            for(int i = 0; i < tokens.size(); i++) {
                int num = Integer.parseInt(tokens.get(i)[1]);

                if(num < min && illness.equals(tokens.get(i)[0]) ) {
                    min = Integer.parseInt(tokens.get(i)[1]);
                    id = i;
                }

                if(num < min2op){
                    min2op = Integer.parseInt(tokens.get(i)[1]);
                    id2op = i;
                }
            }

            if(id == -1){
                id = id2op;
            }

            return id;
        }

        /**
         *
         * @param resultNotifications - vector with all the answers
         */
        protected void handleAllResultNotifications(Vector resultNotifications) {
            //System.out.println("got " + resultNotifications.size() + " result notifs!");
        }

        public int onEnd(){
            System.out.println("*ambulancia: ContractNetInitiator exit" + myAgent.getLocalName() + "*");
            return super.onEnd();
        }
    }

    /**
     * Handles with the call from Central
     */
    public class CallResponseBehaviour extends ContractNetResponder {
        private Agent myAgent;

        public CallResponseBehaviour(Agent a, MessageTemplate mt) {
            super(a, mt);
            myAgent = a;
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            illness = cfp.getContent();
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

            int distance = (int )(Math.random() * 75 + 1);

            String info = typeOfAmbulance + "-" + distance;
            System.out.println(myAgent.getName() + " " + info);
            reply.setContent(info);

            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            try {
                System.out.println(myAgent.getLocalName() + " got a reject...");

                /*
                addBehaviour(new AmbulanceBehaviour(myAgent, new ACLMessage(ACLMessage.CFP)));
                 */

                ACLMessage result = reject.createReply();
                result.setPerformative(ACLMessage.INFORM);
                result.setContent("this is the result");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            ACLMessage nullMessage = new ACLMessage();

            try {
                System.out.println(myAgent.getLocalName() + " got an accept!");

                addBehaviour(new AmbulanceBehaviour(myAgent, new ACLMessage(ACLMessage.CFP)));

                ACLMessage result = accept.createReply();
                result.setPerformative(ACLMessage.INFORM);
                result.setContent("this is the result");

                return result;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return nullMessage;
        }

        public int onEnd(){
            System.out.println("*Ambulancias: ContractNetResponder exit" + myAgent.getLocalName() + "*");
            return super.onEnd();
        }
    }
}