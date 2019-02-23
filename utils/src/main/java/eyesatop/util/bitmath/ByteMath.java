package eyesatop.util.bitmath;

/**
 * Created by Idan on 17/09/2017.
 */

public class ByteMath {

    public static int fromByteArrayToInt(byte[] bytes,int startIndex){
        return ((bytes[startIndex+1] & 0xff) << 8) | (bytes[startIndex] & 0xff);
    }

    public static byte[] intToByte(int number){
        byte[] value = new byte[2];

        value[0] = (byte)(number & 0xFF);
        value[1] = (byte)((number >> 8) & 0xFF);

        return value;
    }
    public static byte[] merge(byte[] a,int aLen, byte[] b,int bLen) {

        byte[] c= new byte[aLen+bLen];

        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static int fromByteArrayToInt(byte[] bytes){
        return ((bytes[1] & 0xff) << 8) | (bytes[0] & 0xff);
    }
}
