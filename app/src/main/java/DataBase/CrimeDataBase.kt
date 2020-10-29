package DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import somiah.jad.criminalintent.Crime

@Database(entities = [Crime::class],version = 3)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDataBase: RoomDatabase() {

    abstract fun crimeDao(): CrimeDao
}

val migration_1to_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }

}

val migration_2to_3 = object : Migration(2,3){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect_phone INTEGER NOT NULL DEFAULT ''"
        )
    }

}
