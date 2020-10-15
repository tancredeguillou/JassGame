/**
 *	@author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** public class, final which cannot be instantiated **/
public final class StringSerializer {
    private StringSerializer() {
    }

    /**
     * used to serialize as a character string (in the form of their textual
     * representation in base 16) the value of the type Integer
     * 
     * @param i
     *            the parameter we want to serialize
     * @return the serialized form of their textual representation in base 16
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, 16);
    }

    /**
     * used to undo the serialization of the value of type Long into a string
     * (representation in base 16)
     * 
     * @param i
     *            the parameter we want to undo the serialization
     * @return the (undo serialized) form of their textual representation in
     *         base 16
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    /**
     * used to serialize as a character string (in the form of their textual
     * representation in base 16) the value of the type Long
     * 
     * @param i
     *            the parameter we want to serialize
     * @return the serialized form of their textual representation in base 16
     */
    public static String serializeLong(long l) {
        return Long.toUnsignedString(l, 16);
    }

    /**
     * used to undo the serialization of the value of type Long into a string
     * (representation in base 16)
     * 
     * @param i
     *            the parameter we want to undo the serialization
     * @return the (undo serialized) form of their textual representation in
     *         base 16
     */
    public static long deserializeLong(String s) {
        return Long.parseLong(s, 16);
    }

    /**
     * used to serialize as a character string (by base64 encoding of bytes
     * constituting their encoding in UTF-8) the value of the type String
     * 
     * @param s
     *            the String we want to serialize
     * @return the serialized version in base64 of the String
     */
    public static String serializeString(String s) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] originBytes = s.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(originBytes);
    }

    /**
     * used to undo the serialization as a character string (by UTF-8 encoding
     * of bytes constituting their encoding in Base64) of the value of the type
     * String
     * 
     * @param serialized
     *            the String we want to serialize
     * @return the original version in UTF-8 of the String
     */
    public static String deserializeString(String serialized) {
        Base64.Decoder decode = Base64.getDecoder();
        byte[] originBytes = decode.decode(serialized);
        return new String(originBytes, StandardCharsets.UTF_8);
    }

    /**
     * returns the string consisting of strings separated by the separator
     * 
     * @param delimiter
     *            the separation character
     * @param strings
     *            a variable number of strings
     * @return the string consisting of strings separated by the separator
     */
    public static String combineStrings(CharSequence delimiter,
            String... strings) {
        return String.join(delimiter, strings);
    }
    
    /**
     * returns a table containing the individual strings
     * 
     * @param delimiter
     *            the separation character
     * @param strings
     *            the unique string which contains subString separated by the
     *            delimiter
     * @return a table containing the individual strings
     */
    public static String[] splitStrings(String delimiter, String strings) {
        return strings.split(delimiter);
    }
}
