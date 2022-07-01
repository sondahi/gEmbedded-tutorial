package output_pin;

import com.comert.gEmbedded.api.device.Device;
import com.comert.gEmbedded.api.device.DeviceFactory;
import com.comert.gEmbedded.api.device.gpio.GPIOFactory;
import com.comert.gEmbedded.api.device.gpio.pin.OutPutPin;
import com.comert.gEmbedded.api.device.gpio.pin.Pin;
import com.comert.gEmbedded.api.device.gpio.pin.PinState;
import com.comert.gEmbedded.api.device.gpio.pin.configurator.OutPutPinConfigurator;

public class App {
    public static void main(String[] args) throws InterruptedException {

        Device device = DeviceFactory.getDeviceInstance();
        try {
            device.setUpDevice();

            final var outputPin = Pin.PIN_21;
            GPIOFactory gpioFactory = device.getGPIOFactoryInstance();
            OutPutPin writer = gpioFactory.createOutPutPin(
                    OutPutPinConfigurator
                            .getBuilder()
                            .pin(outputPin)
                            .initialState(PinState.LOW)
                            .build()
            );

            writer.write();
            Thread.sleep(5000);
            writer.clear();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            device.shutDownDevice();
        }
    }
}
