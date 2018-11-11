import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * The main class allows initializing the program and
 *  * create/change the Agents Patient, Central, Ambulance and Hospital.
 */
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

    /**
     * This function initialize JADE interface and create the all the containers we need.
     */
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

        // Hospitals Container
        acHospitals = rt.createAgentContainer(profileHospitals);

        // Patients Container
        acPatients = rt.createAgentContainer(profilePatients);

        // Ambulances Container
        acAmbulances = rt.createAgentContainer(profileAmbulances);

        //Central Container
        acCentral = rt.createAgentContainer(profileCentral);
    }

    /**
     * This function create and initialize all the agents
     */
    public static void start_world() {
        Object[] args = new Object[1];

        // Hospital Agents
        AgentController H1, H2, H3, H4, H5;

        //Patient Agents
        AgentController P1;

        //Ambulance Agents
        AgentController A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11;

        //Central Agent
        AgentController C;

        try {
            // Patient Agent
            P1 = acPatients.createNewAgent("P1", "PatientAgent", args);

            // Central Agent
            C = acCentral.createNewAgent("C", "CentralAgent", args);

            // HospitalS Agent
            H1 = acHospitals.createNewAgent("H1", "HospitalAgent", args);
            H2 = acHospitals.createNewAgent("H2", "HospitalAgent", args);
            H3 = acHospitals.createNewAgent("H3", "HospitalAgent", args);
            H4 = acHospitals.createNewAgent("H4", "HospitalAgent", args);
            H5 = acHospitals.createNewAgent("H5", "HospitalAgent", args);

            //Ambulances Agent
            A1 = acAmbulances.createNewAgent("A1", "AmbulanceAgent", args);
            A2 = acAmbulances.createNewAgent("A2", "AmbulanceAgent", args);
            A3 = acAmbulances.createNewAgent("A3", "AmbulanceAgent", args);
            A4 = acAmbulances.createNewAgent("A4", "AmbulanceAgent", args);
//            A5 = acAmbulances.createNewAgent("A5", "AmbulanceAgent", args); //Ambulance Agent
//            A6 = acAmbulances.createNewAgent("A6", "AmbulanceAgent", args); //Ambulance Agent
//            A7 = acAmbulances.createNewAgent("A7", "AmbulanceAgent", args); //Ambulance Agent
//            A8 = acAmbulances.createNewAgent("A8", "AmbulanceAgent", args); //Ambulance Agent
//            A9 = acAmbulances.createNewAgent("A9", "AmbulanceAgent", args); //Ambulance Agent
//            A10 = acAmbulances.createNewAgent("A10", "AmbulanceAgent", args); //Ambulance Agent
//            A11 = acAmbulances.createNewAgent("A11", "AmbulanceAgent", args); //Ambulance Agent

            P1.start();

            C.start();

            H1.start();
            H2.start();
            H3.start();
            H4.start();
            H5.start();

            A1.start();
            A2.start();
            A3.start();
            A4.start();
//            A5.start();
//            A6.start();
//            A7.start();
//            A8.start();
//            A9.start();
//            A10.start();
//            A11.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
