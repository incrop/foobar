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
            new int[] {1, 0, 0, 1},
            Solution.solution(new int[][] {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
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
            new int[] {1, 1, 1, 1, 1, 5},
            Solution.solution(new int[][] {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {2, 1, 1, 1, 1, 6},
            Solution.solution(new int[][] {
                    {1, 1, 1, 0, 1, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 1, 1, 0, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 0, 1, 1, 1, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {6, 44, 4, 11, 22, 13, 100},
            Solution.solution(new int[][] {
                    {0, 86, 61, 189, 0, 18, 12, 33, 66, 39},
                    {0, 0, 2, 0, 0, 1, 0, 0, 0, 0},
                    {15, 187, 0, 0, 18, 23, 0, 0, 0, 0},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            }));
        assertArrayEquals(
            new int[] {1, 1, 1, 2, 5},
            Solution.solution(new int[][] {
                    {0, 0, 0, 0, 3, 5, 0, 0, 0, 2},
                    {0, 0, 4, 0, 0, 0, 1, 0, 0, 0},
                    {0, 0, 0, 4, 4, 0, 0, 0, 1, 1},
                    {13, 0, 0, 0, 0, 0, 2, 0, 0, 0},
                    {0, 1, 8, 7, 0, 0, 0, 1, 3, 0},
                    {1, 7, 0, 0, 0, 0, 0, 2, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
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
        Solution.Mat mat = new Solution.Mat(data.length, data[0].length);
        for (int i = 0; i < mat.height(); i++) {
            for (int j = 0; j < mat.width(); j++) {
                mat.addValue(i, j, data[i][j][0], data[i][j][1]);
            }
        }
        return mat;
    }
}
