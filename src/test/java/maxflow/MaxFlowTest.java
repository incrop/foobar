package maxflow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxFlowTest {
    @Test
    void test() {
        assertEquals(6, Solution.solution(new int[] {0}, new int[] {3}, new int[][] {
            {0, 7, 0, 0},
            {0, 0, 6, 0},
            {0, 0, 0, 8},
            {9, 0, 0, 0}
        }));
        assertEquals(5, Solution.solution(new int[] {0}, new int[] {3}, new int[][] {
            {0, 3, 2, 0},
            {0, 0, 5, 2},
            {0, 0, 0, 3},
            {0, 0, 0, 0}
        }));
        assertEquals(88, Solution.solution(new int[] {0, 1, 2, 3}, new int[] {4, 5, 6, 7}, new int[][] {
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7},
            {0, 1, 2, 3, 4, 5, 6, 7}
        }));
        assertEquals(5, Solution.solution(new int[] {0}, new int[] {9}, new int[][] {
                {0, 5, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 5, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 5, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        }));

    }
}
