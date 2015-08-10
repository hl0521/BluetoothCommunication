package com.uteacher.www.uteacherble.uDeviceAdapter;

import android.os.Handler;

import java.util.HashMap;

/**
 * Created by cartman on 15/5/22.
 */
public class uBleDeviceAttributeFactory {
    private static HashMap<SERVICE, String> serviceHashMap = new HashMap<>();
    private static HashMap<CHARACTER, String> characterHashMap = new HashMap<>();
    private static HashMap<ATTRIBUTE, String> attributeHashMap = new HashMap<>();

    public enum ATTRIBUTE {
        DEVICE_NAME_PATTERN,
        DEVICE_ADDRESS_PATTERN,
        CANCEL_PASSWORD,
    }

    public enum SERVICE {
        DATA_WRITE_SERVICE,
        DATA_NOTIFY_SERVICE,
        NAME_SERVICE,
        TRANSMIT_DELTA_SERVICE,
        UART_RATE_SERVICE,
        RESET_SERVICE,
        BROADCAST_FREQUENCY_SERVICE,
        TRANSMIT_POWER_SERVICE,
        RSSI_SERVICE,
        BATTERY_SERVICE,
        PASSWORD_SERVICE,
    }

    public enum CHARACTER {
        DATA_WRITE_CHARACTER,
        DATA_NOTIFY_CHARACTER,
        PASSWORD_WRITE_CHARACTER,
        PASSWORD_NOTIFY_CHARACTER,
        BATTERY_READ_CHARACTER,
        RSSI_READ_CHARACTER,
        NAME_CHARACTER,
        TRANSMIT_DELTA_CHARACTER,
        UART_RATE_CHARACTER,
        RESET_CHARACTER,
        BROADCAST_FREQUENCY_CHARACTER,
        TRANSMIT_POWER_CHARACTER,
    }

    static {
        serviceHashMap.put(SERVICE.DATA_WRITE_SERVICE, "0000ffe5-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.DATA_NOTIFY_SERVICE, "0000ffe0-0000-1000-8000-00805f9b34fb");

        serviceHashMap.put(SERVICE.NAME_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.TRANSMIT_DELTA_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.UART_RATE_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.RESET_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.BROADCAST_FREQUENCY_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.TRANSMIT_POWER_SERVICE, "0000ff90-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.RSSI_SERVICE, "0000ffa0-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.BATTERY_SERVICE, "0000180f-0000-1000-8000-00805f9b34fb");
        serviceHashMap.put(SERVICE.PASSWORD_SERVICE, "0000ffc0-0000-1000-8000-00805f9b34fb");

        characterHashMap.put(CHARACTER.DATA_WRITE_CHARACTER,"0000ffe9-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.DATA_NOTIFY_CHARACTER, "0000ffe4-0000-1000-8000-00805f9b34fb");

        characterHashMap.put(CHARACTER.PASSWORD_WRITE_CHARACTER, "0000ffc1-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.PASSWORD_NOTIFY_CHARACTER, "0000ffc2-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.BATTERY_READ_CHARACTER, "00002a19-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.RSSI_READ_CHARACTER, "0000ffa1-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.NAME_CHARACTER, "0000ff91-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.TRANSMIT_DELTA_CHARACTER, "0000ff92-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.UART_RATE_CHARACTER, "0000ff93-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.RESET_CHARACTER, "0000ff94-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.BROADCAST_FREQUENCY_CHARACTER, "0000ff95-0000-1000-8000-00805f9b34fb");
        characterHashMap.put(CHARACTER.TRANSMIT_POWER_CHARACTER, "0000ff97-0000-1000-8000-00805f9b34fb");

        attributeHashMap.put(ATTRIBUTE.DEVICE_NAME_PATTERN, "Tv221u-.*");
        //FIXME: address pattern
        attributeHashMap.put(ATTRIBUTE.DEVICE_ADDRESS_PATTERN, "B4:99:4C:65:A7:B2");
        attributeHashMap.put(ATTRIBUTE.CANCEL_PASSWORD, "000000");

    }


    private static HashMap<uBleDeviceInterface.DELTA, Byte> deltaByteHashMap = new HashMap<>();
    private static HashMap<uBleDeviceInterface.RATE, Byte> rateByteHashMap = new HashMap<>();
    private static HashMap<uBleDeviceInterface.RESET, Byte> resetByteHashMap = new HashMap<>();
    private static HashMap<uBleDeviceInterface.FREQUENCY, Byte> frequencyByteHashMap = new HashMap<>();
    private static HashMap<uBleDeviceInterface.POWER, Byte> powerByteHashMap = new HashMap<>();

    static {
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_20, (byte)0);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_50, (byte)1);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_100, (byte)2);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_200, (byte)3);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_300, (byte)4);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_400, (byte)5);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_500, (byte)6);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_1000, (byte)7);
        deltaByteHashMap.put(uBleDeviceInterface.DELTA.MS_2000, (byte)8);

        resetByteHashMap.put(uBleDeviceInterface.RESET.GENERAL, (byte)0x55);
        resetByteHashMap.put(uBleDeviceInterface.RESET.SHALLOW, (byte)0x35);
        resetByteHashMap.put(uBleDeviceInterface.RESET.DEEP, (byte)0x35);

        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_4800, (byte)0);
        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_9600, (byte)1);
        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_19200, (byte)2);
        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_38400, (byte) 3);
        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_57600, (byte)4);
        rateByteHashMap.put(uBleDeviceInterface.RATE.BPS_115200, (byte)5);

        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_200, (byte)0);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_500, (byte)1);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_1000, (byte)2);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_1500, (byte)3);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_2000, (byte)4);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_2500, (byte)5);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_3000, (byte)6);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_4000, (byte)7);
        frequencyByteHashMap.put(uBleDeviceInterface.FREQUENCY.MS_5000, (byte)8);

        powerByteHashMap.put(uBleDeviceInterface.POWER.DB_PLUS_4, (byte)0);
        powerByteHashMap.put(uBleDeviceInterface.POWER.DB_0, (byte)1);
        powerByteHashMap.put(uBleDeviceInterface.POWER.DB_MINUS_6, (byte)2);
        powerByteHashMap.put(uBleDeviceInterface.POWER.DB_MINUS_23, (byte)3);


    }

    private static HashMap<Byte, uBleDeviceInterface.DELTA> byteDELTAHashMap = new HashMap<>();
    private static HashMap<Byte, uBleDeviceInterface.RESET> byteRESETHashMap = new HashMap<>();
    private static HashMap<Byte,uBleDeviceInterface.RATE> byteRATEHashMap = new HashMap<>();
    private static HashMap<Byte, uBleDeviceInterface.FREQUENCY> byteFREQUENCYHashMap = new HashMap<>();
    private static HashMap<Byte, uBleDeviceInterface.POWER> bytePOWERHashMap = new HashMap<>();

    static {
        for (uBleDeviceInterface.DELTA delta:deltaByteHashMap.keySet()) {
            byteDELTAHashMap.put(deltaByteHashMap.get(delta) , delta);
        }
        for (uBleDeviceInterface.RESET reset:resetByteHashMap.keySet()) {
            byteRESETHashMap.put(resetByteHashMap.get(reset), reset);
        }
        for (uBleDeviceInterface.RATE rate:rateByteHashMap.keySet()) {
            byteRATEHashMap.put(rateByteHashMap.get(rate), rate);
        }
        for (uBleDeviceInterface.FREQUENCY frequency:frequencyByteHashMap.keySet()) {
            byteFREQUENCYHashMap.put(frequencyByteHashMap.get(frequency), frequency);
        }
        for (uBleDeviceInterface.POWER power:powerByteHashMap.keySet()) {
            bytePOWERHashMap.put(powerByteHashMap.get(power), power);
        }
    }

    public static uBleDeviceInterface.DELTA parseDeltaFromByte(Byte b) {
        return byteDELTAHashMap.get(b);
    }

    public static uBleDeviceInterface.RESET parseResetFromByte(Byte b) {
        return byteRESETHashMap.get(b);
    }

    public static uBleDeviceInterface.RATE parseRateFromByte(Byte b) {
        return byteRATEHashMap.get(b);
    }

    public static uBleDeviceInterface.FREQUENCY parseFrequencyFromByte(Byte b) {
        return byteFREQUENCYHashMap.get(b);
    }

    public static uBleDeviceInterface.POWER parsePowerFromByte(Byte b) {
        return bytePOWERHashMap.get(b);
    }



    public static byte getDeltaByte(uBleDeviceInterface.DELTA delta) {
        return deltaByteHashMap.get(delta);
    }

    public static byte getRateByte(uBleDeviceInterface.RATE rate) {
        return rateByteHashMap.get(rate);
    }

    public static byte getResetByte(uBleDeviceInterface.RESET reset) {
        return resetByteHashMap.get(reset);
    }

    public static byte getFrequencyByte(uBleDeviceInterface.FREQUENCY frequency) {
        return frequencyByteHashMap.get(frequency);
    }

    public static byte getPowerByte(uBleDeviceInterface.POWER power) {
        return powerByteHashMap.get(power);
    }

    public static String getServiceUUID(SERVICE service) {
        return serviceHashMap.get(service);
    }

    public static String getCharacterUUID(CHARACTER character) {
        return characterHashMap.get(character);
    }

    public static String getAttribute(ATTRIBUTE attribute) {
        return attributeHashMap.get(attribute);
    }
}
