package somiah.jad.criminalintent

import DataBase.CrimeDataBase
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"
class CrimeRepository private constructor(context: Context){

    private val database : CrimeDataBase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDataBase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCrime (id : UUID): LiveData< Crime? >      = crimeDao.getCrime(id)
    fun getCrimes()         : LiveData< List<Crime> > = crimeDao.getCrimes()
    fun updateCrime(crime: Crime){
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    fun addCrime(crime: Crime){
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    companion object{
        private var INSTANCE: CrimeRepository?= null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get():CrimeRepository{
            return INSTANCE?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}