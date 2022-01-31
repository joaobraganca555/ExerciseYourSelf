package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentExerciseBinding
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController

class ExerciseFragment : Fragment(),OnMapReadyCallback {
    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!
    private lateinit var myContext: IServiceController
    lateinit var supportMapFragment:SupportMapFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as IServiceController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnStart.setOnClickListener {
            myContext.startManualExercise()
        }

        binding.btnStop.setOnClickListener {
            myContext.stopManualExercise()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val vizela = LatLng(41.39096, -8.26389)
            googleMap?.apply {
                addMarker(
                    MarkerOptions()
                        .position(vizela)
                        .title("Eu")
                )
                animateCamera( CameraUpdateFactory.newLatLngZoom(vizela,10f));
            }
    }
}