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
        AgentController h1,h2,h3,h4,h5;

        //Patient Agents
        AgentController p1;

        //Ambulance Agents
        AgentController a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;

        //Central Agent
        AgentController c;

        try {
            h1 = acHospitals.createNewAgent("h1", "HospitalAgent", args); // Hospital Agent
            h2 = acHospitals.createNewAgent("h2", "HospitalAgent", args); // Hospital Agent
            h3 = acHospitals.createNewAgent("h3", "HospitalAgent", args); // Hospital Agent
            h4 = acHospitals.createNewAgent("h4", "HospitalAgent", args); // Hospital Agent
            h5 = acHospitals.createNewAgent("h5", "HospitalAgent", args); // Hospital Agent

            p1 = acPatients.createNewAgent("p1", "PatientAgent", args); // Patient Agent

            c = acCentral.createNewAgent("c", "CentralAgent", args);

            a1 = acAmbulances.createNewAgent("a1", "AmbulanceAgent", args); //Ambulance Agent
            a2 = acAmbulances.createNewAgent("a2", "AmbulanceAgent", args); //Ambulance Agent
//            a3 = acAmbulances.createNewAgent("a3", "AmbulanceAgent", args); //Ambulance Agent
//            a4 = acAmbulances.createNewAgent("a4", "AmbulanceAgent", args); //Ambulance Agent
//            a5 = acAmbulances.createNewAgent("a5", "AmbulanceAgent", args); //Ambulance Agent
//            a6 = acAmbulances.createNewAgent("a6", "AmbulanceAgent", args); //Ambulance Agent
//            a7 = acAmbulances.createNewAgent("a7", "AmbulanceAgent", args); //Ambulance Agent
//            a8 = acAmbulances.createNewAgent("a8", "AmbulanceAgent", args); //Ambulance Agent
//            a9 = acAmbulances.createNewAgent("a9", "AmbulanceAgent", args); //Ambulance Agent
//            a10 = acAmbulances.createNewAgent("a10", "AmbulanceAgent", args); //Ambulance Agent
//            a11 = acAmbulances.createNewAgent("a11", "AmbulanceAgent", args); //Ambulance Agent



            p1.start();

            c.start();



            h1.start();
            h2.start();
            h3.start();
            h4.start();
            h5.start();

            a1.start();
            a2.start();
//            a3.start();
//            a4.start();
//            a5.start();
//            a6.start();
//            a7.start();
//            a8.start();
//            a9.start();
//            a10.start();
//            a11.start();




        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }




}
