package somiah.jad.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
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
private const val DATE_FORMAT = "EEE, MMM, dd"
private const val REQUEST_CONTACT = 1
private const val REQUEST_CONTACT_PHONE = 1

class CrimeFragment : Fragment(), DatePickerFragment.CallBacks, TimePickerFragment.CallBacks {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var timeButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var suspectPhoneButton: Button

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
        titleField          = view.findViewById(R.id.crime_title) as EditText
        dateButton          = view.findViewById(R.id.crime_date) as Button
        timeButton          = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox      = view.findViewById(R.id.crime_solved) as CheckBox
        reportButton        = view.findViewById(R.id.crime_report) as Button
        suspectButton       = view.findViewById(R.id.crime_suspect) as Button
        suspectPhoneButton  = view.findViewById(R.id.crime_suspect_phone) as Button

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI)

            setOnClickListener { startActivityForResult(pickContactIntent, REQUEST_CONTACT) }

           // pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
       suspectPhoneButton.apply {
           val pickContactIntent = Intent(Intent.ACTION_DIAL)
           pickContactIntent.data = Uri.parse("tel:${crime.suspectPhone}")
           setOnClickListener {

               startActivityForResult(pickContactIntent, REQUEST_CONTACT_PHONE)
           }
           val packageManager: PackageManager = requireActivity().packageManager
           val resolvedActivity: ResolveInfo? =
               packageManager.resolveActivity(pickContactIntent,
                   PackageManager.MATCH_DEFAULT_ONLY)
           if (resolvedActivity == null) {
               isEnabled = false
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
        if(crime.suspect.isNotEmpty()){
            suspectButton.text = crime.suspect
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields= arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER)

                val cursor = requireActivity().contentResolver
                    .query(contactUri!!, queryFields, null, null, null)
                cursor?.use {
                    if (it.count == 0) {
                        return }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    val suspectPhone= it.getString(0)
                    crime.suspect = suspect
                    crime.suspectPhone = suspectPhone.toInt()
                    crimeViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                    suspectPhoneButton.text = suspectPhone
                }
            }
        }
    }

    private fun getCrimeReport():String{
        val dateString = DateFormat.format(DATE_FORMAT,crime.date).toString()
        val solvedString = if(crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }
        var suspect = if (crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect, crime.suspect)
            getString(R.string.crime_report_suspect_phone, crime.suspectPhone.toString())
        }

        return getString(R.string.crime_report_text,
                        crime.title,
                        dateString,
                        solvedString,
                        suspect,)
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