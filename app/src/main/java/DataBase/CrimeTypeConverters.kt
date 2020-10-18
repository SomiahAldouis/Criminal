package DataBase

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

class CrimeTypeConverters {
    @TypeConverter
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }
    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long?{
        return date?.time
    }
    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date?{
        return millisSinceEpoch?.let {
            Date(it)
        }
    }
}