import jade.Boot;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.io.FileWriter;

public class main {

    private static AgentContainer acHospitals;
    private static AgentContainer acPatients;
    private static AgentContainer acAmbulances;
    private static AgentContainer acCentral;

    private static Profile profileHospitals;
    private static Profile profilePatients;
    private static Profile profileAmbulances;
    private static Profile profileCentral;

    public static void main(String args[]){

        jade_initializer();
        start_world();

    }

    public static void jade_initializer() {
        Runtime rt = Runtime.instance();

        String[] container = {
                "-gui",
                "-host 127.0.0.1",
                "-container"
        };

        Boot.main(container);

        profileHospitals = new ProfileImpl();
        profilePatients = new ProfileImpl();
        profileAmbulances = new ProfileImpl();
        profileCentral = new ProfileImpl();

        acHospitals = rt.createAgentContainer(profileHospitals); // Hospitais Container
        acPatients = rt.createAgentContainer(profilePatients); // Patients Container
        acAmbulances = rt.createAgentContainer(profileAmbulances); // Ambulances Container
        acCentral = rt.createAgentContainer(profileCentral); //Central Container
    }

    public static void start_world() {
        Object[] args = new Object[1];
        PatientControl pc = new PatientControl();

        // Hospital Agents
        Vector<AgentController> hospitalAgents = new Vector<AgentController>();

        //Patient Agents
        Vector<AgentController> patientAgents = new Vector<AgentController>();

        //Ambulance Agents
        Vector<AgentController> ambulanceAgents = new Vector<AgentController>();

        //Central Agent
        AgentController c1, c2;


        try {

            //CREATE HOSPITAL AGENTS
            for (int i = 0; i < 100; i++) {
                int i2 = i+1;
                String agentName = "h" + i2;

                hospitalAgents.add(acHospitals.createNewAgent(agentName, "HospitalAgent", args));

            }

            //CREATE PATIENT AGENTS

            for(int i=0; i < 100; i++) {
                int i2 = i+1;
                String agentName = "p" + i2;

                patientAgents.add(acPatients.createNewAgent(agentName, "PatientAgent", args));
            }

            c1 = acCentral.createNewAgent("c", "CentralAgent", args);

            //CREATE AMBULANCE AGENTS
            for (int i = 0; i < 100; i++) {
                int i2 = i+1;
                String agentName = "a" + i2;

                ambulanceAgents.add(acAmbulances.createNewAgent(agentName, "AmbulanceAgent", args)); //Ambulance Agent

            }


            c1.start();

            //START HOSPITAL AGENTS
            for(int i = 0; i < hospitalAgents.size(); i++) {
                hospitalAgents.get(i).start();
            }

            //START AMBULANCE AGENTS
            for(int i = 0; i < ambulanceAgents.size(); i++) {
                ambulanceAgents.get(i).start();
            }

            //START PATIENT AGENTS
            pc.schedulePatient(patientAgents);



        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}