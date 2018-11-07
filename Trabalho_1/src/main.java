import jade.Boot;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

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

        // Hospital Agents
        AgentController h1;

        //Patient Agents
        AgentController p1;

        //Ambulance Agents
        AgentController a1, a2;

        //Central Agent
        AgentController c;

        try {
            h1 = acHospitals.createNewAgent("h1", "HospitalAgent", args); // Hospital Agent

            p1 = acPatients.createNewAgent("p1", "PatientAgent", args); // Patient Agent

            c = acCentral.createNewAgent("c", "CentralAgent", args);
            a1 = acAmbulances.createNewAgent("a1", "AmbulanceAgent", args); //Ambulance Agent

            a2 = acAmbulances.createNewAgent("a2", "AmbulanceAgent", args); //Ambulance Agent

            h1.start();
            c.start();

            p1.start();



            a1.start();
            a2.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }




}
