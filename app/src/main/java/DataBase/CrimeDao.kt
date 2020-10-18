package DataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import somiah.jad.criminalintent.Crime
import java.util.*

@Dao interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

}