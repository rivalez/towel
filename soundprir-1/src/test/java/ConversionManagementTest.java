import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.stream.Stream;

@Test
public class ConversionManagementTest {

    private ConversionManagement cm;

    @BeforeMethod
    public void setUp() {

    }

    private DataPortionImpl[] data =
        new DataPortionImpl[] {
            new DataPortionImpl(5, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(1, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(2, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(2, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.LEFT_CHANNEL),
            new DataPortionImpl(3, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(1, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.LEFT_CHANNEL),
            new DataPortionImpl(9, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(3, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.LEFT_CHANNEL),
            new DataPortionImpl(4, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.RIGHT_CHANNEL),
            new DataPortionImpl(4, new int[]{1, 2, 3, 4}, ConverterInterface.Channel.LEFT_CHANNEL),
        };

    @Test(invocationCount = 500)
    public void sendDataPortion() throws InterruptedException {
        cm = new ConversionManagement();
        cm.setCores(4);
        Stream.of(data).forEach(one -> cm.addDataPortion(one));
        Thread.sleep(10);
        Assert.assertEquals(cm.getPaired().size(), 4);
        Assert.assertEquals(cm.getStore().size(), 6);
    }

}
