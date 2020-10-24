package somiah.jad.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.util.*
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat

private const val ARG_CRIME_ID = "crime_id"
private const val CRIME_DIALOG_DATE = "crime_date"
private const val CRIME_REQUEST_DATE = 0
private const val CRIME_DIALOG_TIME = "crime_time"
private const val CRIME_REQUEST_TIME = 1

class CrimeFragment : Fragment(), DatePickerFragment.CallBacks, TimePickerFragment.CallBacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private val crimeViewModel : CrimeViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()

        val crimeID:UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeViewModel.loadCrime(crimeID)
    }

    override fun onCreateView(  inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeViewModel.crimeLiveData.observe(viewLifecycleOwner, Observer {crime->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        })
    }
    private  fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        timeButton.text = crime.date.time.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int) {
                    crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, CRIME_REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), CRIME_DIALOG_DATE)
            }
        }
        timeButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, CRIME_REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(), CRIME_DIALOG_TIME)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        crimeViewModel.saveCrime(crime)
    }

    companion object{
        fun newInstance(crimeID: UUID): CrimeFragment{

            val args= Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeID)
            }

            return CrimeFragment().apply {
                arguments=args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

    override fun onTimeSelected(time: Date) {
        val pattern = "hh:mm a"
        val dateFormat = SimpleDateFormat(pattern)
        val timeShow = dateFormat.format(time)
        timeButton.text=timeShow
    }

}