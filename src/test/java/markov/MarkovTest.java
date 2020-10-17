package markov;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkovTest {
    @Test
    void testParse() {
        Solution.AbsMarkovChain chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 1},
                {0, 0}
            });
        assertMat(new int[][] {{0}}, new int[] {1}, chain.trans);
        assertMat(new int[][] {{1}}, new int[] {1}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {1, 1},
                {0, 0}
            });
        assertMat(new int[][] {{1}}, new int[] {2}, chain.trans);
        assertMat(new int[][] {{1}}, new int[] {2}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 0},
                {1, 1}
            });
        assertMat(new int[][] {{1}}, new int[] {2}, chain.trans);
        assertMat(new int[][] {{1}}, new int[] {2}, chain.absorb);

        chain = Solution.AbsMarkovChain.parse(
            new int[][] {
                {0, 0, 0, 0, 0},
                {1, 0, 0, 2, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 3, 0, 4},
                {0, 0, 0, 0, 0}
            });
        assertMat(new int[][] {{0, 2}, {0, 0}}, new int[] {3, 1}, chain.trans);
        assertMat(new int[][] {{0, 1, 0}, {3, 0, 4}}, new int[] {3, 7}, chain.absorb);
    }

    @Test
    void testIdentity() {
        assertMat(new int[][] {{1}}, new int[] {1}, Solution.Mat.identity(1));
        assertMat(
            new int[][] {
                {1, 0},
                {0, 1}
            },
            new int[] {1, 1},
            Solution.Mat.identity(2));
        assertMat(
            new int[][] {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
            },
            new int[] {1, 1, 1},
            Solution.Mat.identity(3));
    }

   @Test
    void testMinus() {
        assertMat(new int[][] {{0}}, new int[] {1}, Solution.Mat.identity(1).minus(Solution.Mat.identity(1)));
        assertMat(
           new int[][] {
                   {-1, 0},
                   {0, -1}
           },
           new int[] {1, 1},
           Solution.Mat.identity(2).minus(Solution.Mat.identity(2)).minus(Solution.Mat.identity(2)));
   }

    @Test
    void testInvert() {
        assertMat(
            Solution.Mat.identity(2).nums,
            Solution.Mat.identity(2).dens,
            Solution.Mat.identity(2).invert());
        assertMat(
            new int[][] {
                   {1, -2},
                   {0, 1}
            },
            new int[] {1, 1},
            new Solution.Mat(new int[][] {
                    {1, 2},
                    {0, 1}
                }, new int[] {1, 1}).invert());
        assertMat(
            new int[][] {
                   {1, 1, 0},
                   {-2, 3, 10},
                   {2, -3, 0}
            },
            new int[] {5, 10, 10},
            new Solution.Mat(new int[][] {
                    {3, 0, 2},
                    {2, 0, -2},
                    {0, 1, 1}
                }, new int[] {1, 1, 1}).invert());
    }

    @Test
    void testMultiply() {
        assertMat(
            Solution.Mat.identity(2).nums,
            Solution.Mat.identity(2).dens,
            Solution.Mat.identity(2).multiply(Solution.Mat.identity(2)));
        assertMat(
            new int[][] {
                   {58, 64},
                   {139, 154}
            },
            new int[] {1, 1},
            new Solution.Mat(new int[][] {
                    {1, 2, 3},
                    {4, 5, 6}
                }, new int[] {1, 1})
                    .multiply(new Solution.Mat(new int[][] {
                            {7, 8},
                            {9, 10},
                            {11, 12}
                    }, new int[] {1, 1, 1})));
        assertMat(
                new int[][] {
                        {29, 32},
                        {139, 154}
                },
                new int[] {2, 4},
                new Solution.Mat(new int[][] {
                        {1, 2, 3},
                        {4, 5, 6}
                }, new int[] {2, 2})
                        .multiply(new Solution.Mat(new int[][] {
                                {7, 8},
                                {9, 10},
                                {11, 12}
                        }, new int[] {2, 2, 2})));
    }

    @Test
    void testSolution() {
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
    }

    private void assertMat(int[][] nums, int[] dens, Solution.Mat mat) {
        assertArrayEquals(nums, mat.nums);
        assertArrayEquals(dens, mat.dens);
    }

}
