package burnside;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    final static Map<ConjClass, BigInteger> cache = new HashMap<>();

    // I'm not really on that level of math, and unfortunately don't have enough free time to dig into this on my own,
    // so all credits go to Chris Locke and his article: https://project-eutopia.github.io/Google_foobar_challenge/
    // Also used test cases from here: https://math.stackexchange.com/a/2057828/498993
    public static String solution(int w, int h, int s) {
        BigInteger S = BigInteger.valueOf(s);
        BigInteger sum = BigInteger.ZERO;
        // We are looking for number of equivalency classes of X under action by G:
        // N(w, h, s) = (∑g∈G |X^g|) / |G|
        // |G| = w! * h! - number of all row/column permutations
        // ∑g∈G |X^g| - for any two conjugacy classes of row/column permutations: cc1/cc2
        //      we multiply number of permutations in that classes: numberOfConjugates(cc1) * numberOfConjugates(cc2)
        //      by number of fixed points of that permutation: s ^ numberOfOrbits(cc1, cc2)
        for (ConjClass cc1 : conjClassesOfSize(w)) {
            for (ConjClass cc2 : conjClassesOfSize(h)) {
                sum = sum.add(S.pow(numberOfOrbits(cc1, cc2))
                    .multiply(cache.computeIfAbsent(cc1, Solution::numberOfConjugates))
                    .multiply(cache.computeIfAbsent(cc2, Solution::numberOfConjugates)));
            }
        }
        return sum.divide(factorial(w)).divide(factorial(h)).toString();
    }

    private static List<ConjClass> conjClassesOfSize(int size) {
        List<ConjClass> res = new ArrayList<>();
        conjClassOfSizeAndMaxCycle(res, new int[size], 0, size, size);
        return res;
    }

    private static void conjClassOfSizeAndMaxCycle(List<ConjClass> res, int[] rows, int curr, int size, int width) {
        if (size == 0) {
            res.add(new ConjClass(Arrays.copyOfRange(rows, 0, curr)));
            return;
        }
        for (int row = Math.min(width, size); row > 0; row--) {
            rows[curr] = row;
            conjClassOfSizeAndMaxCycle(res, rows, curr + 1, size - row, row);
        }
    }

    // Find number of permutations in some conjugacy class
    // Use formula for number of conjugates from here: https://en.wikipedia.org/wiki/Conjugacy_class#Properties
    // n! / ((k1! * m1^k1) * (k2! * m2^k2) * ... * (ks! * ms^ks))
    // For efficiency compute based on conjugacy class of smaller size and cache result.
    private static BigInteger numberOfConjugates(ConjClass cc) {
        if (cc.cycleLen[0] == 1) {
            // Identity (e.g (1) (2) (3) (4))
            return BigInteger.ONE;
        }
        if (cc.cycleLen.length == 1) {
            // Cyclic permutations of all elements = (n - 1)!
            return factorial(cc.cycleLen[0] - 1);
        }
        // Recursively calculate number of permutations without first (largest) cycle
        BigInteger res = cache.computeIfAbsent(
                new ConjClass(Arrays.copyOfRange(cc.cycleLen, 1, cc.cycleLen.length)),
                Solution::numberOfConjugates);
        // Adjust the result to take first cycle into account
        int sum = Arrays.stream(cc.cycleLen).sum();
        for (int i = sum - cc.cycleLen[0] + 1; i <= sum; i++) {
            res = res.multiply(BigInteger.valueOf(i));
        }
        if (cc.cycleLen[1] == cc.cycleLen[0]) {
            int i = 2;
            while (i < cc.cycleLen.length && cc.cycleLen[i] == cc.cycleLen[0]) {
                i++;
            }
            res = res.divide(BigInteger.valueOf(i));
        }
        return res.divide(BigInteger.valueOf(cc.cycleLen[0]));
    }

    private static int numberOfOrbits(ConjClass cc1, ConjClass cc2) {
        int orbits = 0;
        for (int row1 : cc1.cycleLen) {
            for (int row2 : cc2.cycleLen) {
                orbits += gcd(row1, row2);
            }
        }
        return orbits;
    }

    static int gcd(int a, int b) {
        while (a != 0) {
            int aTmp = a;
            a = b % a;
            b = aTmp;
        }
        return b;
    }

    private static BigInteger factorial(int n) {
        BigInteger fac = BigInteger.ONE;
        for (int i = 2; i <= n ; i++) {
            fac = fac.multiply(BigInteger.valueOf(i));
        }
        return fac;
    }

    // Some conjugacy class of permutations: https://en.wikipedia.org/wiki/Conjugacy_class
    // Encoded as lengths of each permutation cycle in Young tableau: https://en.wikipedia.org/wiki/Young_tableau
    // E.g cells = [7, 2, 2, 1] encodes following tableau:
    // x x x x x x x
    // x x
    // x x
    // x
    private static class ConjClass {
        final int[] cycleLen;
        private ConjClass(int[] cycleLen) {
            this.cycleLen = cycleLen;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConjClass conjClass = (ConjClass) o;
            return Arrays.equals(cycleLen, conjClass.cycleLen);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(cycleLen);
        }

        @Override
        public String toString() {
            return "ConjClass" + Arrays.toString(cycleLen);
        }
    }
}
