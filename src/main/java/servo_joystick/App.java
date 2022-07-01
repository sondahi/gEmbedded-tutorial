package servo_joystick;

import com.comert.gEmbedded.api.device.Device;

import com.comert.gEmbedded.api.device.DeviceFactory;
import com.comert.gEmbedded.api.device.exception.IOException;
import com.comert.gEmbedded.api.device.gpio.GPIOFactory;
import com.comert.gEmbedded.api.device.gpio.pin.PWMPin;
import com.comert.gEmbedded.api.device.gpio.pin.Pin;
import com.comert.gEmbedded.api.device.gpio.pin.PinFunction;
import com.comert.gEmbedded.api.device.gpio.pin.configurator.PWMPinConfigurator;
import com.comert.gEmbedded.api.device.i2c.I2CMasterFactory;
import com.comert.gEmbedded.api.device.i2c.master.I2CBus;
import com.comert.gEmbedded.api.device.i2c.master.I2CMaster;
import com.comert.gEmbedded.api.device.i2c.master.configurator.I2CMasterConfigurator;

public class App {

    public static void main(String[] args) {
        Device device = DeviceFactory.getDeviceInstance();
        try {
            device.setUpDevice();

            final var sdaPin = Pin.PIN_2;
            final var sclPin = Pin.PIN_3;
            final byte slaveAddress = 0x4b;
            final byte channel7 = (byte) 0b1_111_00_00;
            I2CMasterFactory i2CMasterFactory = device.getI2CMasterFactoryInstance();
            I2CMaster master = i2CMasterFactory.createI2CMaster(
                    I2CMasterConfigurator
                            .getBuilder()
                            .bus(I2CBus.BUS_1)
                            .sdaPin(sdaPin, PinFunction.ALT0)
                            .sclPin(sclPin, PinFunction.ALT0)
                            .busClockInHertz(3400000)
                            .busClockStretchTimeout(0x0040)
                            .build()
            );

            final var pwmPin = Pin.PIN_21;
            GPIOFactory gpioFactory = device.getGPIOFactoryInstance();
            PWMPin servo = gpioFactory.createPWMPin(
                    PWMPinConfigurator
                            .getBuilder()
                            .softwarePWM()
                            .pin(pwmPin)
                            .build()
            );

            final var playTime = 1000;
            for (int i = 0; i < playTime; i++) {
                try {
                    final int degree = master.readRegister(slaveAddress, channel7);
                    servo.pulse(20000, (int) (500 + (degree * 7.84)));
                } catch (IOException ioException) {
                    System.out.println(ioException.getMessage());
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            device.shutDownDevice();
        }
    }

}
