package pt.ipp.estg.cmu_exerciseyourself.ui.comunity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentComunityBinding
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentHealthBinding
import pt.ipp.estg.cmu_exerciseyourself.utils.ChatAdapter
import pt.ipp.estg.cmu_exerciseyourself.utils.WorkoutChat

class ComunityFragment : Fragment() {
    var db = FirebaseFirestore.getInstance()
    private var _binding: FragmentComunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter:ChatAdapter
    private lateinit var workoutChatList: ArrayList<WorkoutChat>
    private lateinit var myContext:Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComunityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        workoutChatList = ArrayList()
        myAdapter = ChatAdapter(workoutChatList)

        val recView = binding.recViewChat
        recView.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        }

        updateMessages()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateMessages(){
        val docRef = db.collection("comunity").document("workouts")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("asd", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                var updatedList = ArrayList<WorkoutChat>()
                var data:ArrayList<String> = snapshot.data?.get("published") as ArrayList<String>

                for(item in data){
                    var fieldsList = item.split(" ")
                    var date:String = fieldsList.get(0)
                    var sport:String = fieldsList.get(1)
                    val distance = fieldsList.get(2)
                    var savedUser:String = fieldsList.get(3)
                    if(date!= null && sport!= null){
                        updatedList.add(WorkoutChat(date!!,sport!!,distance,savedUser))
                    }
                }
                myAdapter.updateList(updatedList.reversed())
            } else {
                Log.d("asd", "Current data: null")
            }
        }
    }

}