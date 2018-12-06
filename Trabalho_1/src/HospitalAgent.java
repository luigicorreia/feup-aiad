import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.Random;

/**
 * This class represents the Hospital.
 * The Hospital receive the patient and take care of him.
 */
public class HospitalAgent extends Agent {
    private int myAgentID = 0;
    private int x;
    private int y;

    public void setup(){
        addBehaviour(new HospitalBehaviour(this, MessageTemplate.MatchPerformative((ACLMessage.CFP))));
        myAgentID++;

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("hospital");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            //registar hospital
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
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

    public int calculateCoordinate(){
        Random r = new Random();
        //int aux = r.nextInt(4) + 1;
        int aux = r.nextInt(8) + 1;
        //int aux = r.nextInt(16) + 1;

        return aux;
    }

    public class HospitalBehaviour extends ContractNetResponder {
        private Agent myAgent;

        public HospitalBehaviour(Agent a, MessageTemplate cfp) {
            super(a, cfp);
            myAgent = a;
        }

        protected ACLMessage handleCfp(ACLMessage cfp) {
            ACLMessage reply = cfp.createReply();
            reply.setPerformative(ACLMessage.PROPOSE);

            int distance = (int )(Math.random() * 75 + 1);

            Random r = new Random();
            int random = r.nextInt(4) + 1;
            String specialty = "";

            int aux = calculateCoordinate();
            setX(aux);

            aux = calculateCoordinate();
            setY(aux);

            /*
            System.out.println("");
            System.out.println("Hospital");
            System.out.println("x = " + getX());
            System.out.println("y = " + getY());
            System.out.println("");
            */

            switch (random) {
                case 1:
                    specialty = "heart-";
                    break;
                case 2:
                    specialty = "brain-";
                    break;
                case 3:
                    specialty = "bones-";
                    break;
                case 4:
                    specialty = "blood-";
                    break;
            }

            String info = specialty + Integer.toString(distance);

            reply.setContent(info);

            return reply;
        }

        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        }

        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            ACLMessage nullMessage = new ACLMessage();

            try {
                System.out.println(myAgent.getLocalName() + " got an accept!");
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