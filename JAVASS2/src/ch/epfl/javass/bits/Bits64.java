package ch.epfl.javass.bits; 

import ch.epfl.javass.Preconditions;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining useful methods to act on 64bits vectors (type long) **/
public final class Bits64 {
    private Bits64() {
    }

    /**
     * returns a Long whose index bits ranging from start (inclusive) to start +
     * size (excluded) are 1, the others being 0
     * 
     * @param start
     *            rank from which it starts
     * @param size
     *            of the series of 1L
     * @throws IllegalArgumentException if start and size do not designate a valid bit range
     * @return a Long whose index bits ranging from start (inclusive) to start +
     *         size (excluded) are 1, the others being 0
     */
    public static long mask(int start, int size){
        Preconditions.checkArgument(0 <= start && start <= start + size
                && start + size <= Long.SIZE);

        if (start == 0 && size == Long.SIZE)
            return ~0;

        return ((1L << size) - 1) << start;
    }

    /**
     * returns a value whose size bits of low weight are equal to those of bits
     * ranging from the start index (included) to the index start + size
     * (excluded)
     * 
     * @param bits
     *            the Long we want to extract a certain part
     * @param start
     *            rank from which it starts
     * @param size
     *            number of low bits in the integer to return
     * @throws IllegalArgumentException if start and size do not designate a valid bit range
     * @return a value whose size bits of low weight are equal to those of bits
     *         ranging from the start index (included) to the index start + size
     *         (excluded)
     */
    public static long extract(long bits, int start, int size){
        Preconditions.checkArgument(0 <= start && start <= start + size
                && start + size <= Long.SIZE);

        return (mask(start, size) & bits) >>> start;
    }

    /**
     * returns the packaged v1 and v2 values in a Long, v1 occupying the s1
     * least significant bits, and v2 occupying the following s2 bits, all other
     * bits being 0
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
     * @return the packaged v1 and v2 values in a long
     */
    public static long pack(long v1, int s1, long v2, int s2){
        Preconditions.checkArgument((s1 + s2) <= Long.SIZE);

        long first = checkPackArgument(v1, s1);
        long second = checkPackArgument(v2, s2) << s1;

        return (first | second);
    }

    private static long checkPackArgument(long value, int size){
        Preconditions.checkArgument(size >= 1 && size < Long.SIZE && value <= mask(0, size));

        return value;
    }

}
