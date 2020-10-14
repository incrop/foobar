package fuel;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static fuel.Solution.solution;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelTest {
    @Test
    void tests() {
        assertEquals(0, solution("1"));
        assertEquals(1, solution("2"));
        assertEquals(2, solution("3"));
        assertEquals(2, solution("4"));
        assertEquals(3, solution("5"));
        assertEquals(5, solution("15"));
        assertEquals(11, solution("381"));
        assertEquals(66, solution("389837429083471"));
        assertEquals(294, solution("38983742904872634012934602198470129834712098347210983471209843783471"));
        assertEquals(1382, solution(bigRandom()));
    }

    private String bigRandom() {
        int digits = 309;
        Random rnd = new Random(digits);
        StringBuilder sb = new StringBuilder(digits);
        while (digits > 0) {
            sb.append((char) ('0' + rnd.nextInt(10)));
            digits--;
        }
        return sb.toString();
    }
}
