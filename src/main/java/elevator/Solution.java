package elevator;

import java.util.Arrays;

public class Solution {
    public static String[] solution(String[] l) {
        return Arrays.stream(l)
                .map(Version::new)
                .sorted()
                .map(v -> v.orig)
                .toArray(String[]::new);
    }

    private static class Version implements Comparable<Version> {
        private final String orig;
        private final int[] parsed;
        Version(String orig) {
            this.orig = orig;
            this.parsed = Arrays.stream(orig.split( "\\." ))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }

        @Override
        public int compareTo(Version that) {
            int minLen = Math.min(this.parsed.length, that.parsed.length);
            for (int i = 0; i < minLen; i++) {
                int diff = this.parsed[i] - that.parsed[i];
                if (diff != 0) {
                    return diff;
                }
            }
            return this.parsed.length - that.parsed.length;
        }
    }
}