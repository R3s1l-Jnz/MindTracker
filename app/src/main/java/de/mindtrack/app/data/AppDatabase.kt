package de.mindtrack.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TrackingEventEntity::class,
        SkillEntity::class,
        SkillUsageEntity::class,
        DiscoveryEntity::class,
        ContextTagEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingEventDao(): TrackingEventDao
    abstract fun skillDao(): SkillDao
    abstract fun skillUsageDao(): SkillUsageDao
    abstract fun discoveryDao(): DiscoveryDao
    abstract fun contextTagDao(): ContextTagDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `skills` (
                        `id` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `evidenceLabel` TEXT NOT NULL,
                        `recommended` INTEGER NOT NULL,
                        `tried` INTEGER NOT NULL,
                        `helpful` INTEGER NOT NULL,
                        `tensionLow` INTEGER NOT NULL,
                        `tensionMedium` INTEGER NOT NULL,
                        `tensionHigh` INTEGER NOT NULL,
                        `tensionBreakdown` INTEGER NOT NULL,
                        `inToolbox` INTEGER NOT NULL,
                        `sortOrder` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `skill_usage` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `skillId` TEXT NOT NULL,
                        `skillName` TEXT NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `discoveries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `text` TEXT NOT NULL,
                        `affectsEnergy` INTEGER NOT NULL,
                        `affectsTension` INTEGER NOT NULL,
                        `energyDirection` INTEGER,
                        `tensionDirection` INTEGER,
                        `contextTag` TEXT
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `context_tags` (
                        `name` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `favorite` INTEGER NOT NULL,
                        PRIMARY KEY(`name`)
                    )
                """.trimIndent())
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "mindtrack.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
