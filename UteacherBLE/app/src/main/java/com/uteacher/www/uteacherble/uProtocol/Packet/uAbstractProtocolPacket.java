package com.uteacher.www.uteacherble.uProtocol.Packet;

import com.uteacher.www.uteacherble.uProtocol.uAbstractProtocolStack;

/**
 * Created by cartman on 15/5/28.
 */
public abstract class uAbstractProtocolPacket implements uProtocolPacketInterface {

    public abstract uAbstractProtocolStack getProtocol();

    private int mTimeTick = 0;

    public int timeTick() {
        mTimeTick += 1;
        return mTimeTick;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@type:" + getType()
                + "@operation:" + getOperation() + "@error:" + getError()
                + "@data:" + getData();
    }
}
