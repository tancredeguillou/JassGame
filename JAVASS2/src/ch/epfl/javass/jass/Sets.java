/**
 * @author tancrede guillou (287334)
 * @author ouriel sebbagh (287796)
 */

package ch.epfl.javass.jass; 

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class Sets {
    private Sets() {}

    public static <T> Collection<Set<T>> powerSet(Collection<T> set) {
        if (set.isEmpty())
            return singleton(emptySet());

        T firstElement = set.iterator().next();
        Set<T> subset = new HashSet<>(set);
        subset.remove(firstElement);
        Collection<Set<T>> subPowerSet = powerSet(subset);
        Set<Set<T>> powerSet = new HashSet<>();
        for (Set<T> s: subPowerSet) {
            Set<T> s1 = new HashSet<>(s);
            s1.add(firstElement);
            powerSet.add(s);
            powerSet.add(s1);
        }
        return powerSet;
    }

    public static <T> boolean mutuallyDisjoint(Collection<Set<T>> sets) {
        Set<T> union = new HashSet<>();
        int totalSize = 0;
        for (Set<T> s: sets) {
            union.addAll(s);
            totalSize += s.size();
            if (union.size() < totalSize)
                return false;
        }
        return true;
    }
}
