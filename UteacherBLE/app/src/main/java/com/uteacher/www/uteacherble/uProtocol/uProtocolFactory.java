package com.uteacher.www.uteacherble.uProtocol;

import java.util.HashMap;

/**
 * Created by cartman on 15/5/29.
 */
public class uProtocolFactory {

    public final static String PROTO_STACK_V1 = uProtocolStackV1.class.getName();

    private static HashMap<String, uAbstractProtocolStack> protoMap = new HashMap<>();

    public static uAbstractProtocolStack getProtocolInstance(String proto) {
        if (protoMap.containsKey(proto)) {
            return protoMap.get(proto);
        }

        //FIXME: change to reflection later.
        uAbstractProtocolStack protocol = null;
        if (proto.equals(PROTO_STACK_V1)) {
            protocol = new uProtocolStackV1();
            protoMap.put(proto, protocol);
        }

        return protocol;
    }
}
