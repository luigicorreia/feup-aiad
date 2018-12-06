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
    boolean available = true;
    private int x;
    private int y;

    private int patientX;
    private int patientY;

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

        int aux = calculateCoordinate();
        setX(aux);

        aux = calculateCoordinate();
        setY(aux);

        System.out.println("");
        System.out.println("Ambulância");
        System.out.println("x = " + getX());
        System.out.println("y = " + getY());
        System.out.println("");

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int calculateCoordinate(){
        Random r = new Random();
        //int aux = r.nextInt(4) + 1;

        int aux = r.nextInt(8) + 1;
        //int aux = r.nextInt(16) + 1;

        return aux;
    }

    private int calculateDistance(int x1, int y1, int x2, int y2) {
        int dist;

        int diff_x = Math.abs(x2 - x1);
        int diff_y = Math.abs(y2 - y1);

        dist = (int) Math.sqrt(diff_x*diff_x + diff_y*diff_y);

        return dist;
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

            System.out.println("");
            System.out.println("Hospital data:");
            System.out.println("");
            System.out.println("| name | specialisty | position x | position y | distance |");
            System.out.println("|------|-------------|------------|------------|----------|");

            try {
                for (int i = 0; i < responses.size(); i++) {
                    hospitalInfo = ((ACLMessage) responses.get(i)).getContent();

                    String[] tokens = hospitalInfo.split("-");

                    int i1 = Integer.parseInt(tokens[1]);
                    int i2 = Integer.parseInt(tokens[2]);

                    int dist = calculateDistance(patientX, patientY, i1, i2);

                    tokens[1] = Integer.toString(dist);

                    allTokens.add(tokens);

                    System.out.println("|  " + ((ACLMessage) responses.get(i)).getSender().getLocalName() + "  |    "
                            + tokens[0] + "    |      " + i1 + "     |      " + i2 + "     |     " +
                            dist + "    |");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

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
        }

        public int onEnd(){
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
            String ambulanceResponse = cfp.getContent();

            String[] tokens = ambulanceResponse.split("-");

            illness = tokens[0];
            patientX = Integer.parseInt(tokens[1]);
            patientY = Integer.parseInt(tokens[2]);

            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

//            if (distance != 100) {
//                distance = (int )(Math.random() * 75 + 1);
//            }

            int distance = calculateDistance(x, y, patientX, patientY);

            String info = typeOfAmbulance + "-" + distance + "-" + isAvailable();
            System.out.println(myAgent.getName() + " " + info);
            reply.setContent(info);

            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            try {
                System.out.println(myAgent.getLocalName() + " got a reject...");

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

                setAvailable(false);

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
            return super.onEnd();
        }
    }
}