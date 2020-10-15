package ch.epfl.javass; 

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining useful preconditions methods **/
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * check if the expression in argument is true
     * 
     * @param b
     *            the expression to check
     * @throws IllegalArgumentException
     *             if the argument is false
     */
    public static void checkArgument(boolean b) throws IllegalArgumentException {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * check if the index is valid
     * 
     * @param index
     *            the index we want to check
     * @param size
     *            the size of our tab
     * @return index in argument if it is non negative and greater than the size
     * @throws IndexOutOfBoundsException
     *             otherwise
     * 
     */
    public static int checkIndex(int index, int size) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }
}
