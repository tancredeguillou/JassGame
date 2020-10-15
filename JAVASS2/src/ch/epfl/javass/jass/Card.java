package ch.epfl.javass.jass; 
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ch.epfl.javass.Preconditions;

/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

/** Class defining a card in the game **/
public final class Card {

    private final int packedRepresentation;
    private Card(int packedRepresentation) {
        this.packedRepresentation = packedRepresentation;
    }

    /**
     * @param c : color of the card
     * 
     * @param r : rank of the card
     * 
     * @return the card of given color and rank
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * @param packed : compact representation of a card, bit representation
     * @throws IllegalArgumentException if packed doesn't represent a valid packed card
     * @return the card of given compact representation
     */
    public static Card ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedCard.isValid(packed));

        return new Card(packed);
    }

    /**
     * @return the compact representation of the card
     */
    public int packed() {
        return packedRepresentation;
    }

    /**
     * @return the color of the card given in compact representation
     */
    public Color color() {
        return PackedCard.color(packedRepresentation);
    }

    /**
     * @return the rank of the card given in compact representation
     */
    public Rank rank() {
        return PackedCard.rank(packedRepresentation);
    }

    /**
     * @param trump : the current trump color when the method is used
     * @param that : the card that will be compared
     * @return true if the card on which the method is used is better than the card given
     *          as a parameter, knowing the actual trump
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, packedRepresentation, that.packed());
    }

    /**
     * @param trump : the current trump color when the method is used
     * @return the card's value, given in "points", knowing the current trump color
     */
    public int points(Color trump) {
        return PackedCard.points(trump, packedRepresentation);
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof Card)
            return packedRepresentation == ((Card) thatO).packed();
        return false;
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return packed();
    }

    /*/
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCard.toString(packedRepresentation);
    }


    /** type Color represents the color of a given card **/
    public enum Color { 
        SPADE("pique"), 
        HEART("coeur"), 
        DIAMOND("carreaux"), 
        CLUB("trèfle"); 

        private final String frenchName;
        private Color(String frenchName) {
            this.frenchName = frenchName;
        }

        /** list containing all the values in Color, in their declaration order **/
        public final static List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        /** number of possible values in Color **/
        public final static int COUNT = ALL.size();

        /*/
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() throws Error {
            switch (frenchName) {
            case "pique"    : return "\u2660";
            case "coeur"    : return "\u2661";
            case "carreaux" : return "\u2662";
            case "trèfle"   : return "\u2663";
            default : throw new Error("Could not find a valid color representation");
            }
        }
    }

    /** type Rank represents the rank of a given card **/
    public enum Rank {
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("valet"),
        QUEEN("dame"),
        KING("roi"),
        ACE("as");

        private final String representation;
        private Rank(String representation) {
            this.representation = representation;
        }

        /** list containing all the values in Color, in their declaration order **/
        public final static List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        /** number of possible values in Color **/
        public final static int COUNT = ALL.size();

        /**
         * @return the ranking of a chosen trump card
         * @throws Error as a default outcome
         */
        public int trumpOrdinal() throws Error {
            switch (representation) {
            case "6"    : return 0;
            case "7"    : return 1;
            case "8"    : return 2;
            case "10"   : return 3;
            case "dame" : return 4;
            case "roi"  : return 5;
            case "as"   : return 6;
            case "9"    : return 7;
            case "valet": return 8;
            default : throw new Error("representation does not represent a valid rank");
            } 
        }

        /*/
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() throws Error {
            switch (representation) {
            case "6"    : 
            case "7"    : 
            case "8"    : 
            case "9"    : 
            case "10"   : return representation;
            case "valet": return "J";
            case "dame" : return "Q";
            case "roi"  : return "K";
            case "as"  : return "A";
            default : throw new Error("representation does not represent a valid rank");
            }
        }
    }
}
