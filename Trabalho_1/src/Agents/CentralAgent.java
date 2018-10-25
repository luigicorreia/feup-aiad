package Agents;

import jade.core.Agent;
import Behaviour.CentralBehaviour;

public class CentralAgent extends Agent {
    public void setup(){
        addBehaviour(new CentralBehaviour(this));
    }
}
