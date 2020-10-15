/*
 *	Author:		Ouriel Sebbagh (287796)
 */

package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

class MeldSetTest {
    private static final long SEED = 0;
    private static final int ITERATIONS = 10_000;
    private MctsPlayer p = new MctsPlayer(PlayerId.PLAYER_2, SEED, ITERATIONS);


    @Test
    void ComparationHigherTest() {
        CardSet hand = CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.EIGHT))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.SIX))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.EIGHT))
                .add(Card.of(Color.HEART, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.JACK));
        System.out.println(p.selectMeldSet(hand));
        assertEquals(120, p.selectMeldSet(hand).points());
    }

}

