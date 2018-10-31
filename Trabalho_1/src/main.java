import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class main {

    private static AgentContainer acHospitals;
    private static AgentContainer acPatients;
    private static Profile profileHospitals;

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
        Profile profilePatients = new ProfileImpl();

        acHospitals = rt.createAgentContainer(profileHospitals);
        acPatients = rt.createAgentContainer(profilePatients);
    }

    public static void start_world() {
        Object[] args = new Object[1];

        Runtime rt = Runtime.instance();

        AgentController hospitalController;
        AgentController patientController;

        try {
            hospitalController = acHospitals.createNewAgent("central", "CentralAgent", args);
            patientController = acPatients.createNewAgent("patient1", "PatientAgent", args);
            patientController.start();
            hospitalController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }




    }


}
