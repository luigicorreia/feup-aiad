import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class CentralAgent extends Agent {

    public void setup(){
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
    }

    public class CentralBehaviour extends AchieveREResponder {

        private Agent myAgent;

        public CentralBehaviour(Agent a, MessageTemplate msg){
            super(a, msg);
        }

        protected ACLMessage handleRequest(ACLMessage msg) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            reply.setContent("ambulance on the way!");

            System.out.println("ambulance on the way");

            return reply;
        }
    }
}
