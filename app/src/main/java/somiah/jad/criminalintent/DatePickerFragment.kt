package somiah.jad.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE= "date"

class DatePickerFragment: DialogFragment() {

    interface CallBacks{
        fun onDateSelected(date: Date)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(ARG_DATE) as Date

        val calender  = Calendar.getInstance()
        calender.time = date

        val initYear  = calender.get(Calendar.YEAR)
        val initMonth = calender.get(Calendar.MONTH)
        val initDay   = calender.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener {
                _:DatePicker, year:Int, month:Int, day:Int ->
            val resultDate: Date = GregorianCalendar(year,month,day).time
            targetFragment?.let {fragment ->
                (fragment as CallBacks).onDateSelected(resultDate)
            }
        }

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initYear,
            initMonth,
            initDay
        )
    }


    companion object{
        fun newInstance(date: Date): DatePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_DATE,date)
            }
            return DatePickerFragment().apply {
                arguments =args
            }
        }
    }
}