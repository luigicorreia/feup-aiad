import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.*;

public class PatientControl {
    public int i = 0;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void schedulePatient(Vector<AgentController> p) {
        final Runnable beeper = new Runnable() {
            public void run() {
                try {
                    System.out.println("\n\n\n");
                    if (i < p.size()) {
                        p.get(i).start();
                        i++;
                    }

                    else {System.out.println("finished!!");
                    }

                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 5, 2, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() { beeperHandle.cancel(true); }
        }, 20, SECONDS);
    }


}
