package reflections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionsTest {
    @Test
    void test() {
        assertEquals(7, Solution.solution(new int[] {3, 2}, new int[] {1,1}, new int[] {2, 1}, 4));
        assertEquals(9, Solution.solution(new int[] {300, 275}, new int[] {150, 150}, new int[] {185, 100}, 500));
    }
}
