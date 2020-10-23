package somiah.jad.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    private val crimeIDLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIDLiveData){ crimeID ->
            crimeRepository.getCrime(crimeID)
        }

    fun loadCrime(crimeId: UUID){
        crimeIDLiveData.value= crimeId
    }

}