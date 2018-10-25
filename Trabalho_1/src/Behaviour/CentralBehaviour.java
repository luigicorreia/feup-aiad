package Behaviour;

import Agents.CentralAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class CentralBehaviour extends CyclicBehaviour{
    private Agent myAgent;

    public CentralBehaviour(CentralAgent centralAgent){
        myAgent = centralAgent;
    }

    public void action(){
        ACLMessage msg = myAgent.receive();

        if(msg != null) {
            System.out.println(msg);
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("Got your message!");
            myAgent.send(reply);
        } else {
            block();
        }
    }
}
