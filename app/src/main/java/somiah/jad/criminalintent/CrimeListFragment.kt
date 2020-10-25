package somiah.jad.criminalintent

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*
import java.util.zip.DataFormatException

//private const val TAG = "CrimeListFragment"
class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter :CrimeAdapter? = CrimeAdapter(emptyList())
    //private var adapter :CrimeAdapter? = CrimeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView= view.findViewById(R.id.crime_recycler_view)as RecyclerView
        crimeRecyclerView.layoutManager=LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        return view
    }
    private fun updateUI(crimes: List<Crime>){
        adapter = CrimeAdapter(crimes)
       // adapter = CrimeAdapter()
        crimeRecyclerView.adapter = adapter
        //adapter = crimeRecyclerView.adapter as CrimeAdapter
        //adapter?.submitList(crimes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer {crimes ->
                crimes?.let {
                    updateUI(crimes)
                }
            }
        )
    }

    private inner class CrimeHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime : Crime
        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime){
            this.crime=crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.getDateInstance(DateFormat.FULL).format(this.crime.date).toString()
            solvedImageView.visibility = if (crime.isSolved){
                View.VISIBLE
            }else{
                View.GONE
            }
        }
        override fun onClick(v: View) {
            callBacks?.onCrimeSelected(crime.id)
        }
    }
    interface CallBacks{
        fun onCrimeSelected(crimeID: UUID)
    }
    private var callBacks: CallBacks?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callBacks = context as CallBacks?
    }

    override fun onDetach() {
        super.onDetach()
        callBacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_add_crime,menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callBacks?.onCrimeSelected(crime.id)
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }

    }

   // private inner class CrimeAdapter: ListAdapter<Crime,CrimeHolder>(CrimeDiffUtil()){
    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

            val view = layoutInflater.inflate(R.layout.item_view,parent,false)
            return  CrimeHolder(view)

        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
           // val crime = getItem(position)
            holder.bind(crime)
        }

        override fun getItemCount()= crimes.size
        override fun getItemViewType(position: Int): Int {
            return R.layout.item_view

        }
    }
    class CrimeDiffUtil: DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id === newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            var sameId=oldItem.id==newItem.id
            var sameTitle=oldItem.title==newItem.title
            var sameIsSolved=oldItem.isSolved==newItem.isSolved
            var sameDate=oldItem.date==newItem.date
            var sameItem=(sameId && sameTitle && sameIsSolved  && sameDate)
            return sameItem
        }
    }

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    companion object{
        fun newInstance():CrimeListFragment{
            return CrimeListFragment()
        }
    }
}