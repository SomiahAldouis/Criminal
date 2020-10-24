package somiah.jad.criminalintent



import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_TIME= "time"

class TimePickerFragment: DialogFragment() {

    interface CallBacks{
        fun onTimeSelected(date: Date)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val time = arguments?.getSerializable(ARG_TIME) as Date

        val calender  = Calendar.getInstance()
        calender.time = time

        val initHour  = calender.get(Calendar.HOUR)
        val initMin   = calender.get(Calendar.MINUTE)

        val timeListener = TimePickerDialog.OnTimeSetListener{
                _: TimePicker, initHour: Int, initMin: Int ->
            calender.set(Calendar.HOUR_OF_DAY , initHour)
            calender.set(Calendar.MINUTE , initMin)
            val resultTime = calender.time
            targetFragment?.let { fragment ->
                (fragment as CallBacks).onTimeSelected(resultTime)
            }
        }

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initHour,
            initMin,
            true
        )
    }


    companion object{
        fun newInstance(time: Date): TimePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_TIME,time)
            }
            return TimePickerFragment().apply {
                arguments =args
            }
        }
    }
}