import jade.Boot;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

import java.io.*;

public class main {
    public static void main(String args[]){
        Runtime rt = Runtime.instance();

        String[] container = {
          "-gui",
          "-host 127.0.0.1",
          "-container"
        };

        Boot.main(container);

        Profile profileHospitals = new ProfileImpl();
        Profile profilePatients = new ProfileImpl();

        AgentContainer ccHospitals = rt.createAgentContainer(profileHospitals);
        AgentContainer ccPatients = rt.createAgentContainer(profilePatients);
    }
}
