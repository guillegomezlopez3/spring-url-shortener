package dev.guillermo.shortener;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62Test {
    @Test
    void roundTrip() {
        for (long v : new long[]{0, 1, 61, 62, 12345, Integer.MAX_VALUE, Long.MAX_VALUE}) {
            assertEquals(v, Base62.decode(Base62.encode(v)));
        }
    }

    @Test
    void knownValues() {
        assertEquals("0", Base62.encode(0));
        assertEquals("z", Base62.encode(35));
        assertEquals("10", Base62.encode(62));
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Base62.encode(-1));
        assertThrows(IllegalArgumentException.class, () -> Base62.decode("ab-c"));
    }
}
