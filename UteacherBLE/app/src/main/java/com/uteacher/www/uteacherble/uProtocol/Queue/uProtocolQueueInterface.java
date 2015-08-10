package com.uteacher.www.uteacherble.uProtocol.Queue;

import com.uteacher.www.uteacherble.uProtocol.Packet.uAbstractProtocolPacket;

/**
 * Created by cartman on 15/5/26.
 */
public interface uProtocolQueueInterface {

    enum FAILURE {
        INVALID_LENGTH,
        CRC_FAILURE,
        UNEXPECTED_SEQUENCE,
        INVALID_PACKET,
        QUEUE_FAILURE,
    }

    public boolean send(uAbstractProtocolPacket packet);

    public boolean receive(byte[] data);

    public int getFailureStatistic(FAILURE failure);
}
