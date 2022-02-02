package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController

class ExerciseFragment : Fragment(),OnMapReadyCallback {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var myContext: IServiceController
    lateinit var supportMapFragment:SupportMapFragment
    lateinit var workoutViewModel:WorkoutsViewModel
    val current = LatLng(37.129665, -8.669586)
    var marker = MarkerOptions().position(LatLng(37.129665, -8.669586))
    var listPoints = ArrayList<LatLng>()
    lateinit var polyOptions : PolylineOptions
    var googlemap:GoogleMap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workoutViewModel = ViewModelProvider(requireActivity()).get(WorkoutsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnStart.setOnClickListener {
            myContext.startAutomaticExercise()
        }

        binding.btnStop.setOnClickListener {
            myContext.stopAutomaticExercise()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        polyOptions = PolylineOptions()
        workoutViewModel.getCurrentPosition().observe(viewLifecycleOwner,{
            googlemap?.apply {
                animateCamera(CameraUpdateFactory.newLatLngZoom(it,16f))
                clear()
                addMarker(
                    MarkerOptions().position(it)
                )
                polyOptions.add(it)
                addPolyline(polyOptions)
            }
        })

        workoutViewModel.getDistance().observe(viewLifecycleOwner,{
            binding.totalDistance.setText(it.toString())
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googlemap = googleMap
            googleMap?.apply {
                addMarker(
                   marker
                )
                addPolyline(polyOptions)
                animateCamera( CameraUpdateFactory.newLatLngZoom(current,16f));
            }
    }
}