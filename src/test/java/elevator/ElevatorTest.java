package elevator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static elevator.Solution.solution;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ElevatorTest {
    @Test
    void tests() {
        assertArrayEquals(arr(), solution(arr()));
        assertArrayEquals(arr("1"), solution(arr("1")));
        assertArrayEquals(arr("1", "2"), solution(arr("2", "1")));
        assertArrayEquals(
                arr("1", "1.1", "2", "2.1"),
                solution(arr("2.1", "1", "1.1", "2")));
        assertArrayEquals(
                arr("0.1", "1.1.1", "1.2", "1.2.1", "1.11", "2", "2.0", "2.0.0"),
                solution(arr("1.11", "2.0.0", "1.2", "2", "0.1", "1.2.1", "1.1.1", "2.0")));
        assertArrayEquals(
                arr("1.0", "1.0.2", "1.0.12", "1.1.2", "1.3.3"),
                solution(arr("1.1.2", "1.0", "1.3.3", "1.0.12", "1.0.2")));
    }

    String[] arr(String ... s) {
        return s;
    }
}
