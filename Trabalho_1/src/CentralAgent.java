import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.*;

/**
 * This class represents the Central of Emergency.
 * From here we can communicate with others agents,
 * like the central receive the help request from
 * Patient and send an ambulance to catch him to
 * take a hospital.
 */
public class CentralAgent extends Agent {
    private Agent myAgent;
    private Vector<String> patientIllnesses = new Vector();
    private String patientIllness="";
    private int patientX = 0;
    private int patientY = 0;



    /**
     * This function prepare the Central Agent.
     */
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
            // registar Central
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        myAgent = this;
    }

    public class CentralBehaviour extends AchieveREResponder {

        /**
         *
         * @param a - represent the central agent
         * @param msg - represent the message from other agent
         */
        public CentralBehaviour(Agent a, MessageTemplate msg){
            super(a, msg);
        }

        /**
         *
         * @param msg - message received
         * @return reply - answer from ambulance
         */
        protected ACLMessage handleRequest(ACLMessage msg) {
            ACLMessage reply = msg.createReply();

            String patientRequest = msg.getContent();

            String[] tokens = patientRequest.split("-");

            patientIllness = tokens[0];
            patientX = Integer.parseInt(tokens[1]);
            patientY = Integer.parseInt(tokens[2]);

            addBehaviour(new CallBehaviour(myAgent, new ACLMessage(ACLMessage.CFP)));

            System.out.println("");
            System.out.println("Central received call. Patient is at [" + patientX + "," + patientY + "]");
            System.out.println("");

            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("ambulance on the way!");

            return reply;
        }

        /**
         *
         * @param request - patient illness
         * @param response - from an ambulance on the way
         * @return
         */
        protected  ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            return response;
        }

        public int onEnd(){
            return super.onEnd();
        }
    }

    public class CallBehaviour extends ContractNetInitiator{

        /**
         *
         * @param a - agent
         * @param cfp - message
         */
        public CallBehaviour(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        /**
         *
         * @param cfp  - patient illness
         * @return v - return a vector with the ambulances most appropriate equipment
         */
        protected Vector prepareCfps(ACLMessage cfp) {
            Vector v = new Vector();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("ambulance");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                for(int i = 0; i < result.length; ++i) {
                    cfp.addReceiver(result[i].getName());
                }
            } catch(FIPAException fe) {
                fe.printStackTrace();
            }

            System.out.println("Sending ambulance request ...");
            System.out.println("Problem: " + patientIllness);
            System.out.println("");

            cfp.setContent(patientIllness + "-" + patientX + "-" + patientY);

            v.add(cfp);

            return v;
        }

        public void writeData(Vector<String[]> alltokens) throws IOException {

            String csvFile = "../data.csv";
            FileWriter writer = new FileWriter(csvFile);
            for(int i = 0; i < alltokens.size(); i++){
                String[] tokens = alltokens.get(i);

                List<String> list = new ArrayList<>();
                list.add(tokens[0]);
                list.add(tokens[1]);
                list.add(tokens[2]);

                CSVUtils.writeLine(writer, list);
            }


            writer.flush();
            writer.close();


        }

        protected void handleAllResponses(Vector responses, Vector acceptances) {
            System.out.println("");
            System.out.println("Central got " + responses.size() + " responses!");

            Vector<String[]> allTokens = new Vector<>();



            try {
                System.out.println("");
                System.out.println("Ambulance data:");
                System.out.println("");
                System.out.println("| name | specialty | position x | position y | distance |");
                System.out.println("|------|-------------|------------|------------|----------|");

                for (int i = 0; i < responses.size(); i++) {
                    ACLMessage msg = ((ACLMessage) responses.get(i)).createReply();

                    String ambulanceResponse = ((ACLMessage) responses.get(i)).getContent();

                    String[] tokens = ambulanceResponse.split("-");

                    allTokens.add(tokens);




                    System.out.println("|  " + ((ACLMessage) responses.get(i)).getSender().getLocalName() +
                            "  |    " + tokens[0] +"    |      " + tokens[1] +
                            "     |      " + tokens[2] + "     |     " +
                            "d" + "    |");


                }
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

            System.out.println("");
            try {
                writeData(allTokens);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int id = analyzeInfo(allTokens);

            if (id == -1){
                System.out.println("Não existem ambulâncias disponíveis");
                return;
            }

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

        protected void handleAllResultNotifications(Vector resultNotifications) {
            if (resultNotifications.size() < 1){
                System.out.println("");
                System.out.println("Central got " + resultNotifications.size() + " result notifications!");
                System.out.println("");
            }else {
                System.out.println("");
                System.out.println("Central got " + resultNotifications.size() + " result notification!");
                System.out.println("");
            }
        }

        /**
         * Select the ambulance
         * @param tokens
         * @return
         */
        protected int analyzeInfo(Vector<String[]> tokens) {
            int min = 100;
            int id = -1;
            int min2op = 100;
            int id2op = 0;
            boolean noAmbulanceAvailable = true;

            for(int i = 0; i < tokens.size(); i++) {
                System.out.println(tokens.get(i)[2]);
                if (tokens.get(i)[2].equals("true")){
                    int num = Integer.parseInt(tokens.get(i)[1]);

                    if(num < min && patientIllness.equals(tokens.get(i)[0])) {
                        min = Integer.parseInt(tokens.get(i)[1]);
                        id = i;
                    }

                    if(num < min2op){
                        min2op = Integer.parseInt(tokens.get(i)[1]);
                        id2op = i;
                    }

                    noAmbulanceAvailable = false;
                }
            }

            if (noAmbulanceAvailable == true){
                return -1;
            } else if(id == -1 && tokens.get(id2op)[2].equals("true")){
                id = id2op;
            }


            return id;
        }

        public int onEnd(){
            //System.out.println("contract net central");
            //System.out.println("*exit " + myAgent.getLocalName() + " *");
            return super.onEnd();
        }
    }
}
