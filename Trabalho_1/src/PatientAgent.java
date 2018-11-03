import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Vector;

public class PatientAgent extends Agent {
    private Agent myAgent;

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
            DFService.register(this, dfd);
        } catch(FIPAException fe) {
            fe.printStackTrace();
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
                msg.addReceiver(result[0].getName());
                System.out.println(result[0].getName().toString());

            }   catch(FIPAException fe) {
                fe.printStackTrace();
            }

            msg.setContent("need ambulance!");

            System.out.println("need ambulance! I'm having a heart attack!");

            v.add(msg);

            return v;
        }

        protected void handleAgree(ACLMessage agree) {
            System.out.println(agree.getContent()+": thanks!!");
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
    }
 **/
    }
}
