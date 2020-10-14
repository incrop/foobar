package fuel;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static int solution(String x) {
        Set<BigInteger> prev = new HashSet<>();
        Set<BigInteger> curr = new HashSet<>();
        Set<BigInteger> next = new HashSet<>();
        curr.add(new BigInteger(x));
        int iter = 0;
        // Breadth-first search.
        // If num is even - divide by two
        // If num is odd - try both +1 and -1
        // Filter out numbers encountered on this and previous iteration
        while (!curr.contains(BigInteger.ONE)) {
            for (BigInteger num : curr) {
                if (num.testBit(0)) {
                    // num is odd - add and remove 1
                    BigInteger incr = num.add(BigInteger.ONE);
                    if (!prev.contains(incr) && !curr.contains(incr)) {
                        next.add(incr);
                    }
                    BigInteger decr = num.subtract(BigInteger.ONE);
                    if (!prev.contains(decr) && !curr.contains(decr)) {
                        next.add(decr);
                    }
                } else {
                    // num is even - divide by 2
                    BigInteger half = num.shiftRight(1);
                    if (!prev.contains(half) && !curr.contains(half)) {
                        next.add(half);
                    }
                }
            }
            prev = curr;
            curr = next;
            next = new HashSet<>();
            iter++;
        }
        return iter;
    }
}