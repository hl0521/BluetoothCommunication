package com.uteacher.www.uteacherble.TestUtil;

import com.uteacher.www.uteacherble.uProtocol.CRC.CRCFactory;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cartman on 15/5/30.
 */
public class TestDataFactory {

    //FIXME: testdata should be retrieved from files later.

    private static ArrayList<byte[]> testRdmData = new ArrayList<>();
    private static ArrayList<byte[]> expectRdmData = new ArrayList<>();

    static {
        for (int i = 0; i < 100; i++) {
            byte[] b = new byte[12];
            new Random().nextBytes(b);
            //FIXME:
            for (int j = 0; j < 12; j++) {
                if (b[j] == (byte) 0xfb || b[j] == (byte) 0xff) {
                    b[j] = (byte) 0xaa;
                }
            }
            b[0] = (byte) 0xfb;
            b[11] = (byte) 0xff;
            testRdmData.add(b);
            expectRdmData.add(b);
        }
    }

    public static byte[] getRandomTestData(int index) {
        if (index < testRdmData.size()) {
            return testRdmData.get(index);
        }
        return null;
    }

    public static byte[] getRandomExpectData(int index) {
        if (index < expectRdmData.size()) {
            return expectRdmData.get(index);
        }
        return null;
    }

    private static ArrayList<byte[]> testProtoData = new ArrayList<>();
    private static ArrayList<byte[]> expectProtoData = new ArrayList<>();

    static {

        testProtoData.add(new byte[]{(byte) 0xc0, 0}); //connect
        expectProtoData.add(new byte[]{(byte) 0xc0, 0}); //connect ack
        testProtoData.add(new byte[]{(byte) 0xc2, 0, 1}); //keepalive
        expectProtoData.add(new byte[]{(byte) 0xc2, 0, 1}); //keepalive ack
        testProtoData.add(new byte[]{(byte) 0xe2, 1}); //get device info
        expectProtoData.add(new byte[]{(byte) 0xe2, 0}); //device info
        for (int i = 0; i < 4; i++) {
            testProtoData.add(new byte[]{(byte) 0xe3, 1, (byte) i}); //get error info
            expectProtoData.add(new byte[]{(byte) 0xe3, 0, (byte) i}); //error info
        }
        testProtoData.add(new byte[]{(byte) 0xe4, 1}); //get battery
        expectProtoData.add(new byte[]{(byte) 0xe4, 0, 100}); //battery
        for (int i = 0; i < 4; i++) {
            testProtoData.add(new byte[]{(byte) 0xd0, 2, (byte) i, (byte) 100}); //set param
            expectProtoData.add(new byte[]{(byte) 0xd0, 0});//ack

            testProtoData.add(new byte[]{(byte) 0xd0, 1, (byte) i}); //get param
            expectProtoData.add(new byte[]{(byte) 0xd0, 0, (byte) i, (byte) 100}); //param
        }

        testProtoData.add(new byte[]{(byte) 0xc1, 0}); //disconnect
        expectProtoData.add(new byte[]{(byte) 0xc1, 0}); //disconnect ack

    }

    private static byte[] wrapProtoData(byte[] originData, int index, byte control) {
        byte[] data = new byte[originData.length + 4];
        data[0] = (byte) (originData.length + 2);
        data[1] = (byte) index;
        data[2] = control;
        for (int i = 3; i < originData.length + 3; i++) {
            data[i] = originData[i - 3];
        }
        data[data.length - 1] = CRCFactory.calculateCRC8(data, 0, data.length - 1);
        return data;
    }

    public static byte[] getProtoTestData(int index) {
        if (index < testProtoData.size()) {
            return wrapProtoData(testProtoData.get(index), index, (byte) 0x40);
        }
        return null;
    }

    public static byte[] getProtoExpectData(int index) {
        if (index < expectProtoData.size()) {
            return wrapProtoData(expectProtoData.get(index), index, (byte) 0x50);

        }
        return null;
    }

}
