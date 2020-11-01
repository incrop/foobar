package burnside;

import org.junit.jupiter.api.Test;

import static burnside.Solution.solution;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BurnsideTest {
    @Test
    void test() {
        assertEquals("7", solution(2, 2, 2));
        assertEquals("430", solution(2, 3, 4));
        
        assertEquals("1", solution(3, 3, 1));
        assertEquals("36", solution(3, 3, 2));
        assertEquals("738", solution(3, 3, 3));
        assertEquals("8240", solution(3, 3, 4));
        assertEquals("57675", solution(3, 3, 5));
        assertEquals("289716", solution(3, 3, 6));
        assertEquals("1144836", solution(3, 3, 7));
        assertEquals("3780288", solution(3, 3, 8));
        
        assertEquals("1", solution(4, 4, 1));
        assertEquals("317", solution(4, 4, 2));
        assertEquals("90492", solution(4, 4, 3));
        assertEquals("7880456", solution(4, 4, 4));
        assertEquals("270656150", solution(4, 4, 5));
        assertEquals("4947097821", solution(4, 4, 6));
        assertEquals("58002778967", solution(4, 4, 7));
        assertEquals("490172624992", solution(4, 4, 8));
        
        assertEquals("1", solution(5, 5, 1));
        assertEquals("5624", solution(5, 5, 2));
        assertEquals("64796982", solution(5, 5, 3));
        assertEquals("79846389608", solution(5, 5, 4));
        assertEquals("20834113243925", solution(5, 5, 5));
        assertEquals("1979525296377132", solution(5, 5, 6));
        assertEquals("93242242505023122", solution(5, 5, 7));
        assertEquals("2625154125717590496", solution(5, 5, 8));

        assertEquals("" +
                "97195340925396730736950973830781340249131679073592360856141700148734207997877978" +
                "00541973582287876882108834397796920913972168217148795996701228647462897847048719" +
                "3051591840", solution(12, 12, 20));
    }
}