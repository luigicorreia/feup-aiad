import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Random;
import java.util.Vector;

/**
 * This class represents the patient. The patient is the responsible to initialize all the process.
 */
public class PatientAgent extends Agent {
    private Agent myAgent;
    private String patientIllness = "";

    public void setup(){
        myAgent = this;

        addBehaviour(new PatientBehaviour(this, new ACLMessage(ACLMessage.REQUEST)));

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("patient");
        sd.setName(getLocalName());
        dfd.addServices(sd);

        try {
            // registar Paciente
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
        }

        generateIllness();
    }

    protected void generateIllness() {
        Random r = new Random();
        int random = r.nextInt(4) + 1;

        switch(random) {
            case 1:
                patientIllness = "heart"; //Ambulance specialized in heart problems
                break;
            case 2:
                patientIllness = "brain"; //Ambulance specialized in brain problems (like a stroke)
                break;
            case 3:
                patientIllness = "bones"; //Ambulance specialized in dealing with broken bones or other bone health problems
                break;
            case 4:
                patientIllness = "blood"; //Ambulance specialized in dealing with large hemorrhaging problems
                break;
        }
    }


    public class PatientBehaviour extends AchieveREInitiator {
        public PatientBehaviour(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        protected Vector<ACLMessage> prepareRequests(ACLMessage msg) {
            Vector<ACLMessage> v = new Vector<ACLMessage>();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("central");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                for(int i=0; i<result.length; ++i) {
                    msg.addReceiver(result[i].getName());
                }
                System.out.println(myAgent.getLocalName() + ": Pacient asking for help. " + patientIllness + " problem");

            }   catch(FIPAException fe) {
                fe.printStackTrace();
            }

            msg.setContent(patientIllness);

            v.add(msg);

            return v;
        }

        protected void handleAgree(ACLMessage agree) {
            //System.out.println(agree.getContent()+" thanks!!");
        }

        public int onEnd(){
            //System.out.println("*exit" + myAgent.getLocalName() + "*");
            return super.onEnd();
        }

        /**
        protected void handleRefuse(ACLMessage refuse) {
            // ...
        }

        protected void handleInform(ACLMessage inform) {
            // ...
        }

        protected void handleFailure(ACLMessage failure) {
            // ...
        }
         **/
    }
}
