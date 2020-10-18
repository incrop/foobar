package markov;

import org.junit.jupiter.api.Test;

import static markov.Solution.absGcd;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MarkovTest {
    @Test
    void testParse() {
        Solution.AbsMarkovChain chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 1},
                {0, 0}
            });
        assertMat(new int[][][] {{{0, 1}}}, chain.trans);
        assertMat(new int[][][] {{{1, 1}}}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {1, 1},
                {0, 0}
            });
        assertMat(new int[][][] {{{1, 2}}}, chain.trans);
        assertMat(new int[][][] {{{1, 2}}}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 0},
                {1, 1}
            });
        assertMat(new int[][][] {{{1, 2}}}, chain.trans);
        assertMat(new int[][][] {{{1, 2}}}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 0, 0, 0, 0},
                {1, 0, 0, 2, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 3, 0, 4},
                {0, 0, 0, 0, 0}
            });
        assertMat(new int[][][] {
                {{0, 1}, {2, 3}},
                {{0, 1}, {0, 1}}
        }, chain.trans);
        assertMat(new int[][][] {
                {{1, 3}, {0, 1}, {0, 1}},
                {{0, 1}, {3, 7}, {4, 7}}
        }, chain.absorb);
    }

    @Test
    void testIdentity() {
        assertMat(new int[][][] {{{1, 1}}}, Solution.Mat.identity(1));
        assertMat(
            new int[][][] {
                {{1, 1}, {0, 1}},
                {{0, 1}, {1, 1}}
            },
            Solution.Mat.identity(2));
        assertMat(
            new int[][][] {
                {{1, 1}, {0, 1}, {0, 1}},
                {{0, 1}, {1, 1}, {0, 1}},
                {{0, 1}, {0, 1}, {1, 1}}
            },
            Solution.Mat.identity(3));
    }

   @Test
    void testMinus() {
        assertMat(new int[][][] {{{0, 1}}}, Solution.Mat.identity(1).minus(Solution.Mat.identity(1)));
        assertMat(
           new int[][][] {
               {{-1, 1}, {0, 1}},
               {{0, 1}, {-1, 1}}
           },
           Solution.Mat.identity(2).minus(Solution.Mat.identity(2)).minus(Solution.Mat.identity(2)));
   }

    @Test
    void testInvert() {
        assertMat(
            new int[][][] {
                {{1, 1}, {0, 1}},
                {{0, 1}, {1, 1}}
            },
            Solution.Mat.identity(2).invert());
        assertMat(
            new int[][][] {
               {{1, 1}, {-2, 1}},
               {{0, 1}, {1, 1}}
            },
            mat(new int[][][] {
                {{1, 1}, {2, 1}},
                {{0, 1}, {1, 1}}
            }).invert());
        assertMat(
            new int[][][] {
               {{1, 5}, {1, 5}, {0, 1}},
               {{-1, 5}, {3, 10}, {1, 1}},
               {{1, 5}, {-3, 10}, {0, 1}}
            },
            mat(new int[][][] {
                {{3, 1}, {0, 1}, {2, 1}},
                {{2, 1}, {0, 1}, {-2, 1}},
                {{0, 1}, {1, 1}, {1, 1}}
            }).invert());
    }

    @Test
    void testMultiply() {
        assertMat(
                new int[][][] {
                    {{1, 1}, {0, 1}},
                    {{0, 1}, {1, 1}}
                },
            Solution.Mat.identity(2).multiply(Solution.Mat.identity(2)));
        assertMat(
            new int[][][] {
               {{58, 1}, {64, 1}},
               {{139, 1}, {154, 1}}
            },
            mat(new int[][][] {
                {{1, 1}, {2, 1}, {3, 1}},
                {{4, 1}, {5, 1}, {6, 1}}
            }).multiply(mat(new int[][][] {
                {{7, 1}, {8, 1}},
                {{9, 1}, {10, 1}},
                {{11, 1}, {12, 1}}
            })));

        assertMat(
            new int[][][] {
                {{29, 2}, {16, 1}},
                {{139, 4}, {77, 2}}
            },
            mat(new int[][][] {
                {{1, 2}, {1, 1}, {3, 2}},
                {{2, 1}, {5, 2}, {3, 1}}
            }).multiply(mat(new int[][][] {
                {{7, 2}, {4, 1}},
                {{9, 2}, {5, 1}},
                {{11, 2}, {6, 1}}
            })));
    }

    @Test
    void testSolution() {
        assertArrayEquals(
            new int[] {1, 1},
            Solution.solution(new int[][] {
                {0}
            }));
        assertArrayEquals(
            new int[] {1, 1},
            Solution.solution(new int[][] {
                {0, 0},
                {1, 1}
            }));
        assertArrayEquals(
            new int[] {1, 2, 3},
            Solution.solution(new int[][] {
                {1, 2, 3, 0, 0, 0},
                {4, 5, 6, 0, 0, 0},
                {7, 8, 9, 1, 0, 0},
                {0, 0, 0, 0, 1, 2},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {100, 90, 81, 271},
            Solution.solution(new int[][] {
                {0, 9, 0, 1, 0, 0},
                {0, 0, 9, 0, 1, 0},
                {9, 0, 0, 0, 0, 1},
                {0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 1}
            }));
        assertArrayEquals(
            new int[] {7, 6, 8, 21},
            Solution.solution(new int[][] {
                {0, 2, 1, 0, 0},
                {0, 0, 0, 3, 4},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {7, 6, 8, 21},
            Solution.solution(new int[][] {
                {0, 2, 1, 0, 0},
                {0, 0, 0, 3, 4},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {0, 3, 2, 9, 14},
            Solution.solution(new int[][] {
                    {0, 1, 0, 0, 0, 1},
                    {4, 0, 0, 3, 2, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {1, 1},
            Solution.solution(new int[][] {
                    {1, 1, 0, 0},
                    {0, 1, 1, 0},
                    {1, 0, 0, 1},
                    {0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {395, 45, 36, 324, 800},
            Solution.solution(new int[][] {
                    {1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 9, 0, 0, 0},
                    {5, 0, 2, 0, 0, 0, 0, 0, 0, 0},
                    {0, 1, 2, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 5, 0, 0, 0, 9, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 5, 0, 0},
            }));
        assertArrayEquals(
            new int[] {1, 1, 2, 4, 8, 16},
            Solution.solution(new int[][] {
                    {0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
                    {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 1, 0, 0, 0, 1, 0, 0},
                    {0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            }));
        assertArrayEquals(
            new int[] {4, 5, 5, 4, 2, 20},
            Solution.solution(new int[][] {
                    {0, 7, 0, 17, 0, 1, 0, 5, 0, 2},
                    {0, 0, 29, 0, 28, 0, 3, 0, 16, 0},
                    {0, 3, 0, 0, 0, 1, 0, 0, 0, 0},
                    {48, 0, 3, 0, 0, 0, 17, 0, 0, 0},
                    {0, 6, 0, 0, 0, 1, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            }));
    }

    private void assertMat(int[][][] data, Solution.Mat actual) {
        Solution.Mat expected = mat(data);
        assertArrayEquals(expected.nums, actual.nums);
        assertArrayEquals(expected.dens, actual.dens);
    }

    private Solution.Mat mat(int[][][] data) {
        long[][] nums = new long[data.length][data[0].length];
        long[] dens = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            long commonDen = lcd(data[i]);
            for (int j = 0; j < data[0].length; j++) {
                long mul = commonDen / data[i][j][1];
                nums[i][j] = data[i][j][0] * mul;
            }
            dens[i] = commonDen;
        }
        return new Solution.Mat(nums, dens);
    }

    private long lcd(int[][] row) {
        long lcd = row[0][1];

        for (int i = 1; i < row.length; i++) {
            long mul = lcd * row[i][1];
            lcd = Math.max(lcd, mul / absGcd(lcd, mul));
        }
        return lcd;
    }
}
