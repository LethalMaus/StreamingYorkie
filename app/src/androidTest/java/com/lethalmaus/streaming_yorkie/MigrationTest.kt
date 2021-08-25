package com.lethalmaus.streaming_yorkie

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lethalmaus.streaming_yorkie.database.StreamingYorkieDB
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

const val TEST_DB = "migration-test"

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        StreamingYorkieDB::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        val db = helper.createDatabase(TEST_DB, 2)
        db.close()
        val appDb = Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            StreamingYorkieDB::class.java,
            TEST_DB
        ).addMigrations(*migrations).build()
        appDb.openHelper.writableDatabase
        appDb.close()
    }

    private val migrations = arrayOf(
        StreamingYorkieDB.MIGRATION_1_3,
        StreamingYorkieDB.MIGRATION_3_4
    )
}