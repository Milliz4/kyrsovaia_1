package db;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {

    @Test
    void getInstance_returnsSameInstance() {
        DBManager db1 = DBManager.getInstance();
        DBManager db2 = DBManager.getInstance();
        assertSame(db1, db2);
    }

    @Test
    void getInstance_notNull() {
        assertNotNull(DBManager.getInstance());
    }
}