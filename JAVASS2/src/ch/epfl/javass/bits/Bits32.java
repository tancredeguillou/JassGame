package ch.epfl.javass.bits; 

import ch.epfl.javass.Preconditions;

/**
 *  @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining useful methods to act on 32bits vectors (type integer) **/
public final class Bits32 {
    private Bits32() {
    }

    /**
     * returns an integer whose index bits ranging from start (inclusive) to
     * start + size (excluded) are 1, the others being 0
     * 
     * @param start
     *            rank from which it starts
     * @param size
     *            of the series of 1
     * @throws IllegalArgumentException if start and size do not designate a valid bit range
     * @return an integer whose index bits ranging from start (inclusive) to
     *         start + size (excluded) are 1, the others being 0
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(0 <= start && start <= start + size
                && start + size <= Integer.SIZE);

        if (start == 0 && size == Integer.SIZE) {
            return ~0;
        } else {
            return ((1 << size) - 1) << start;
        }
    }

    /**
     * returns a value whose size bits of low weight are equal to those of bits
     * ranging from the start index (included) to the index start + size
     * (excluded)
     * 
     * @param bits
     *            the integer we want to extract a certain part
     * @param start
     *            rank from which it starts
     * @param size
     *            number of low bits in the integer to return
     * @throws IllegalArgumentException if start and size do not designate a valid bit range
     * @return a value whose size bits of low weight are equal to those of bits
     *         ranging from the start index (included) to the index start + size
     *         (excluded)
     */
    public static int extract(int bits, int start, int size){
        Preconditions.checkArgument(0 <= start && start <= start + size
                && start + size <= Integer.SIZE);

        int mask = mask(start, size);
        return (mask & bits) >>> start;
    }

    /**
     * returns the packaged v1 and v2 values in an integer of type int, v1
     * occupying the s1 least significant bits, and v2 occupying the following
     * s2 bits, all other bits being 0
     * 
     * @param v1
     *            first value to packed
     * @param s1
     *            number of bits v1 will occupy
     * @param v2
     *            second value to packed
     * @param s2
     *            number of bits v2 will occupy
     * @throws IllegalArgumentException if one of the sizes is not between 1 (inclusive) 
     *              and 31 (inclusive), if one of the values takes up more bits than its size, 
     *              or if the sum of the sizes is greater than 32
     * @return the packaged v1 and v2 values in an integer of type int
     */
    public static int pack(int v1, int s1, int v2, int s2){
        Preconditions.checkArgument((s1 + s2) <= Integer.SIZE);

        int first = checkPackArgument(v1, s1);
        int second = checkPackArgument(v2, s2) << s1;

        return (first | second);
    }

    private static int checkPackArgument(int value, int size){
        Preconditions.checkArgument(size >= 1 && size < Integer.SIZE && value <= mask(0, size));

        return value;
    }

    /**
     * returns the packaged v1 and v2 values in an integer of type int, v1
     * occupying the s1 least significant bits, and v2 occupying the following
     * s2 bits, all other bits being 0
     * 
     * @param v1
     *            first value to packed
     * @param s1
     *            number of bits v1 will occupy
     * @param v2
     *            second value to packed
     * @param s2
     *            number of bits v2 will occupy
     * @param v3
     *            third value to packed
     * @param s3
     *            number of bits v3 will occupy
     * @throws IllegalArgumentException if one of the sizes is not between 1 (inclusive) 
     *              and 31 (inclusive), if one of the values takes up more bits than its size, 
     *              or if the sum of the sizes is greater than 32
     * @return the packaged v1 v2 and v3 values in an integer of type int
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3){
        Preconditions.checkArgument((s1 + s2 + s3) <= Integer.SIZE);

        int first = checkPackArgument(v1, s1);
        int second = checkPackArgument(v2, s2) << s1;
        int third = checkPackArgument(v3, s3) << (s1 + s2);

        return (first | second | third);
    }

    /**
     * returns the packaged v1 and v2 values in an integer of type int, v1
     * occupying the s1 least significant bits, and v2 occupying the following
     * s2 bits, all other bits being 0
     * 
     * @param v1
     *            first value to packed
     * @param s1
     *            number of bits v1 will occupy
     * @param v2
     *            second value to packed
     * @param s2
     *            number of bits v2 will occupy
     * @param v3
     *            third value to packed
     * @param s3
     *            number of bits v3 will occupy
     * @param v4
     *            fourth value to packed
     * @param s4
     *            number of bits v4 will occupy
     * @param v5
     *            fifth value to packed
     * @param s5
     *            number of bits v5 will occupy
     * @param v6
     *            sixth value to packed
     * @param s6
     *            number of bits v6 will occupy
     * @param v7
     *            seventh value to packed
     * @param s7
     *            number of bits v7 will occupy
     * @throws IllegalArgumentException if one of the sizes is not between 1 (inclusive) 
     *              and 31 (inclusive), if one of the values takes up more bits than its size, 
     *              or if the sum of the sizes is greater than 32
     * @return the packaged v1 and v2 values in an integer of type int
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7){
        Preconditions.checkArgument((s1 + s2 + s3 + s4 + s5 + s6 + s7) <= Integer.SIZE);

        int first = checkPackArgument(v1, s1);
        int second = checkPackArgument(v2, s2) << s1;
        int third = checkPackArgument(v3, s3) << (s1 + s2);
        int fourth = checkPackArgument(v4, s4) << (s1 + s2 + s3);
        int fifth = checkPackArgument(v5, s5) << (s1 + s2 + s3 + s4);
        int sixth = checkPackArgument(v6, s6) << (s1 + s2 + s3 + s4 + s5);
        int seventh = checkPackArgument(v7,
                s7) << (s1 + s2 + s3 + s4 + s5 + s6);

        return (first | second | third | fourth | fifth | sixth | seventh);
    }
}
