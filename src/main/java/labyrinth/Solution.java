package labyrinth;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static int solution(int[][] map) {
        int h = map.length;
        int w = map[0].length;
        int[] visited = new int[h * w];
        visited[0] = 2;
        List<Step> front = new ArrayList<>();
        // First step is entering the maze
        front.add(new Step(0, 0, false));
        int distance = 1;
        while (!front.isEmpty()) {
            List<Step> nextFront = new ArrayList<>();
            for (Step step : front) {
                if (step.i == h - 1 && step.j == w - 1) {
                    // Found the shortest path
                    return distance;
                }

                for (int di = 0, dj = 1; di < shifts.length; di += 2, dj += 2) {
                    int i = step.i + shifts[di];
                    int j = step.j + shifts[dj];
                    if (i < 0 || i >= h || j < 0 || j >= w) {
                        // Do not go outside the borders
                        continue;
                    }
                    boolean wallPassed;
                    if (map[i][j] == 1) {
                        if (step.wallBreak) {
                            // Do not break the wall twice
                            continue;
                        }
                        wallPassed = true;
                    } else {
                        wallPassed = step.wallBreak;
                    }
                    int idx = i * w + j;
                    // 1 - visited after breaking the wall, 2 - visited before breaking the wall
                    int nextVisited = wallPassed ? 1 : 2;
                    if (visited[idx] >= nextVisited) {
                        // Allowed cases:
                        // 1) visited[idx] == 0
                        // this was not visited before
                        // 2) visited[idx] == 1 && nextVisited == 2
                        // this was visited after breaking the wall, need to check if we can make better shortcut later
                        continue;
                    }
                    visited[idx] = nextVisited;
                    nextFront.add(new Step(i, j, wallPassed));
                }
            }
            front = nextFront;
            distance++;
        }
        // No more steps left
        throw new IllegalArgumentException("Impossible to solve");
    }

    // shifts to get neighbors coordinates
    private final static int[] shifts = new int[] {
        -1, 0,
        0, 1,
        1, 0,
        0, -1,
    };

    private static class Step {
        final int i;
        final int j;
        final boolean wallBreak;

        private Step(int i, int j, boolean wallPassed) {
            this.i = i;
            this.j = j;
            this.wallBreak = wallPassed;
        }
    }
}