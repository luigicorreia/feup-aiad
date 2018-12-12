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
        AgentController h1,h2,h3,h4,h5, h6, h7, h8, h9, h10;

        //Patient Agents
        AgentController p1, p2, p3, p4, p5, p6, p7, p8, p9, p10;



        //Ambulance Agents
        AgentController a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;

        //Central Agent
        AgentController c1, c2;


        try {
            h1 = acHospitals.createNewAgent("h1", "HospitalAgent", args); // Hospital Agent
            h2 = acHospitals.createNewAgent("h2", "HospitalAgent", args); // Hospital Agent
            h3 = acHospitals.createNewAgent("h3", "HospitalAgent", args); // Hospital Agent
            h4 = acHospitals.createNewAgent("h4", "HospitalAgent", args); // Hospital Agent
            h5 = acHospitals.createNewAgent("h5", "HospitalAgent", args); // Hospital Agent
            h6 = acHospitals.createNewAgent("h6", "HospitalAgent", args); // Hospital Agent
            h7 = acHospitals.createNewAgent("h7", "HospitalAgent", args); // Hospital Agent
            h8 = acHospitals.createNewAgent("h8", "HospitalAgent", args); // Hospital Agent
            h9 = acHospitals.createNewAgent("h9", "HospitalAgent", args); // Hospital Agent
            h10 = acHospitals.createNewAgent("h10", "HospitalAgent", args); // Hospital Agent



            p1 = acPatients.createNewAgent("p1", "PatientAgent", args); // Patient Agent
            p2 = acPatients.createNewAgent("p2", "PatientAgent", args); // Patient Agent
            p3 = acPatients.createNewAgent("p3", "PatientAgent", args); // Patient Agent
            p4 = acPatients.createNewAgent("p4", "PatientAgent", args); // Patient Agent
            p5 = acPatients.createNewAgent("p5", "PatientAgent", args); // Patient Agent


            c1 = acCentral.createNewAgent("c", "CentralAgent", args);

            a1 = acAmbulances.createNewAgent("a1", "AmbulanceAgent", args); //Ambulance Agent
            a2 = acAmbulances.createNewAgent("a2", "AmbulanceAgent", args); //Ambulance Agent
            a3 = acAmbulances.createNewAgent("a3", "AmbulanceAgent", args); //Ambulance Agent
            a4 = acAmbulances.createNewAgent("a4", "AmbulanceAgent", args); //Ambulance Agent
            a5 = acAmbulances.createNewAgent("a5", "AmbulanceAgent", args); //Ambulance Agent
            a6 = acAmbulances.createNewAgent("a6", "AmbulanceAgent", args); //Ambulance Agent
            a7 = acAmbulances.createNewAgent("a7", "AmbulanceAgent", args); //Ambulance Agent
            a8 = acAmbulances.createNewAgent("a8", "AmbulanceAgent", args); //Ambulance Agent
            a9 = acAmbulances.createNewAgent("a9", "AmbulanceAgent", args); //Ambulance Agent
            a10 = acAmbulances.createNewAgent("a10", "AmbulanceAgent", args); //Ambulance Agent
            a11 = acAmbulances.createNewAgent("a11", "AmbulanceAgent", args); //Ambulance Agent


            c1.start();



            h1.start();
            h2.start();
            h3.start();
            h4.start();
            h5.start();

            a1.start();
            a2.start();
            a3.start();
            a4.start();
            a5.start();


            Vector<AgentController> patients = new Vector();

            patients.add(p1);
            patients.add(p2);
            patients.add(p3);
//            patients.add(p4);
//            patients.add(p5);

            pc.schedulePatient(patients);
//            p2.start();
//            p3.start();
//            p4.start();



        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}