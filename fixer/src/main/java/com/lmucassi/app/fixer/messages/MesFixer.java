package com.lmucassi.app.fixer.messages;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MesFixer {
    private int     typeLength;
    private String  messageType;
    private int     marketId;
    private int     checksumLength;
    private String  checksum;

    MesFixer(String messageType, int marketId)
    {
        this.messageType = messageType;
        this.typeLength = messageType.length();
        this.marketId = marketId;
    }

    public MesFixer(){}

    public int getMarketId() { return this.marketId; }

    public String getMessageType() { return this.messageType; }

    public void setMarketId(int marketId) { this.marketId = marketId; }

    public String getChecksum() { return this.checksum; }

    private void setMessageType(String messageType) {
        this.messageType = messageType;
        this.typeLength = messageType.length();
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
        this.checksumLength = checksum.length();
    }

    public int getTypeLength() { return typeLength; }

    public int getChecksumLength() { return checksumLength; }
}
