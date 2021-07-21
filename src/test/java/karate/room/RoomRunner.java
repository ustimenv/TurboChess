package karate.room;

import com.intuit.karate.junit5.Karate;

public class RoomRunner{
    @Karate.Test
    Karate testAll() {
        return Karate.run().relativeTo(getClass());
    }
}
