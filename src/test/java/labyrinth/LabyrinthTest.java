package labyrinth;

import org.junit.jupiter.api.Test;

import static labyrinth.Solution.solution;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LabyrinthTest {
    @Test
    void tests() {
        check(3, "",
                "--",
                "*-");
        check(3, "",
                "--",
                "-*");
        check(3, "",
                "*-",
                "--");
        check(7, "",
                "-**-",
                "---*",
                "**--",
                "-**-");
        check(39, "",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------",
                "--------------------");
    }

    void check(int expectedDistance, String ... lab) {
        int[][] input = new int[lab.length - 1][];
        for (int i = 1; i < lab.length; i++) {
            String line = lab[i];
            int[] inLine = new int[line.length()];
            for (int j = 0; j < line.length(); j++) {
                inLine[j] = line.charAt(j) == '*' ? 1 : 0;
            }
            input[i - 1] = inLine;
        }
        assertEquals(expectedDistance, solution(input));
    }
}
