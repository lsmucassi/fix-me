package com.lmucassi.app.fixer;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    public static String idHash(String id)
    {
        String Hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id.getBytes());
            byte[] digest = md.digest();
            Hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Hash;
    }
}
