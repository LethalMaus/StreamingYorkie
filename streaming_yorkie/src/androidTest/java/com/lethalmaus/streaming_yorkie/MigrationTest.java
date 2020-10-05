package com.lethalmaus.streaming_yorkie;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import static com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB.MIGRATION_1_2;
import static com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB.MIGRATION_2_3;
import static com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB.MIGRATION_3_4;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public MigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                Objects.requireNonNull(StreamingYorkieDB.class.getCanonicalName()),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrateAll() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);
        db.close();

        StreamingYorkieDB appDb = Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                StreamingYorkieDB.class,
                TEST_DB)
                .addMigrations(ALL_MIGRATIONS).build();
        appDb.getOpenHelper().getWritableDatabase();
        appDb.close();
    }

    private static final Migration[] ALL_MIGRATIONS = new Migration[]{
            MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4};
}
