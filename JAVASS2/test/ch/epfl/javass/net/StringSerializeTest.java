/*
 *	Author:		Ouriel Sebbagh (287796)
 */

package ch.epfl.javass.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.net.StringSerializer;

class StringSerializeTest {

    @Test
    void combineTest() {
         String s =(",I,waaant,a,babyyyy,, ,");
         assertEquals(s,StringSerializer.combineStrings(",", "","I","waaant","a","babyyyy",""," ",""));
    }

    @Test
    void splitTest() {
         String[] s = (StringSerializer.splitStrings(" ",
         "je ne veux pas mettre d'espace !!! (saufALaFin) "));
         String[] result = new String[] { "je", "ne", "veux",
         "pas","mettre", "d'espace", "!!!","(saufALaFin)", ""};
         for (int i = 0; i<s.length;++i) {
        assertEquals(result[i],s[i]);
         }
    }
    @Test
    void serializeLongTest() {
        CardSet hand = CardSet.EMPTY.add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.KING))
                .add(Card.of(Color.HEART, Rank.SIX))
                .add(Card.of(Color.HEART, Rank.EIGHT))
                .add(Card.of(Color.HEART, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.SEVEN))
                .add(Card.of(Color.CLUB, Rank.SIX))
                .add(Card.of(Color.CLUB, Rank.JACK));
//      ♠9,♠K,♡6,♡8,♡9,♡K,♢7,♣6,♣J 
        assertEquals("210002008d0088", StringSerializer.serializeLong(hand.packed()));
    }
    
//    Amélie (QW3DqWxpZQ==), Gaëlle (R2HDq2xsZQ==), Émile (w4ltaWxl) et Nadège (TmFkw6hnZQ==)
    @Test
    void serializeStringTest() {
        assertEquals("QW3DqWxpZQ==",StringSerializer.serializeString("Amélie"));
        assertEquals("R2HDq2xsZQ==",StringSerializer.serializeString("Gaëlle"));
        assertEquals("w4ltaWxl",StringSerializer.serializeString("Émile"));
        assertEquals("TmFkw6hnZQ==",StringSerializer.serializeString("Nadège"));
    }

    @Test
    void deserializeStringTest() {
        assertEquals("Amélie",StringSerializer.deserializeString("QW3DqWxpZQ=="));
        assertEquals("Gaëlle",StringSerializer.deserializeString("R2HDq2xsZQ=="));
        assertEquals("Émile",StringSerializer.deserializeString("w4ltaWxl"));
        assertEquals("Nadège",StringSerializer.deserializeString("TmFkw6hnZQ=="));
    }
    
}
