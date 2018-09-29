package com.lmucassi.app.fixer.messages;

import com.lmucassi.app.fixer.Hasher;

public class RecieveConnection extends MesFixer {
    private int id;

    public RecieveConnection(String messageType, int marketId, int id)
    {
        super(messageType, marketId);
        this.id = id;
        setChecksum(Hasher.idHash(String.valueOf(id)));
    }

    public RecieveConnection() {}

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setNewChecksum ()
    {
        setChecksum(Hasher.idHash(String.valueOf(id)));
    }

    @Override
    public String toString()
    {
        return "MessageAcceptConnection {" +
                "ID = " + id +
                "|MSG_TYPE = '" + getMessageType() + "'" +
                "|CHECKSUM = '" + getChecksum() + "'" +
                '}';
    }
}
