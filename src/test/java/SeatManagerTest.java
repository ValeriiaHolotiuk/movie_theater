import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

public class SeatManagerTest {

    @Test
    void startsAllOpen() {
        SeatManager m = new SeatManager(3,4);
        for (int r=0;r<3;r++) for (int c=0;c<4;c++) {
            assertTrue(m.isAvailable(r,c));
        }
    }

    @Test
    void reserveAndCancel() {
        SeatManager m = new SeatManager(2,2);
        assertTrue(m.reserve(0,1));
        assertFalse(m.isAvailable(0,1));
        assertFalse(m.reserve(0,1)); // already taken
        assertTrue(m.cancel(0,1));
        assertTrue(m.isAvailable(0,1));
    }

    @Test
    void boundsChecks() {
        SeatManager m = new SeatManager(2,2);
        assertFalse(m.reserve(-1,0));
        assertFalse(m.cancel(0,99));
    }

    @Test
    void suggestionWorks() {
        SeatManager m = new SeatManager(1,2);
        Optional<int[]> s1 = m.suggestFirstAvailable();
        assertTrue(s1.isPresent());
        m.reserve(0,0);
        m.reserve(0,1);
        assertTrue(m.suggestFirstAvailable().isEmpty());
    }
}
