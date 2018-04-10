package priority.queue;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class TransportManagementTest {

    @Test
    public void sendOnePackage_OneCourierAvailable() throws InterruptedException {
        //given
        TransportManagement transport = new TransportManagementImpl();
        transport.setNumberOfCouriers(1);
        transport.setExistingClients(Collections.singletonList(new Client("Marcin", "Skrzyszów")));
        transport.sendPackage(new Package(1, new Client("Marcin", "Skrzyszów")));

        Thread.sleep(3000);
    }

    @Test
    public void send_10_Packages_OneCourierAvailable() throws InterruptedException {
        //given
        TransportManagement transport = new TransportManagementImpl();
        transport.setNumberOfCouriers(1);
        transport.setExistingClients(Collections.singletonList(new Client("Marcin", "Skrzyszów")));
        IntStream.range(0, 10).forEach((id) -> transport.sendPackage(new Package(id, new Client("Marcin", "Skrzyszów"))));

        Thread.sleep(3000);
    }
}