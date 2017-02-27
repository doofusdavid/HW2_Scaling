package cs455.scaling.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility Class to process Hash Codes.
 */
public class ServerHashCode
{
    /**
     * Calculates SHA1 hash code and returns it.
     *
     * @param data Byte array containing a block of data
     * @return String containing the SHA1 hash
     */
    public static String SHA1FromBytes(byte[] data)
    {
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);

        return hashInt.toString(16);

    }
}
