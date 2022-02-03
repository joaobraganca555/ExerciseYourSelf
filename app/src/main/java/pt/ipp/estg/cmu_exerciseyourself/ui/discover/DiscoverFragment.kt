package pt.ipp.estg.cmu_exerciseyourself.ui.discover

import android.content.Context
import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.dynamic.IObjectWrapper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.GeopifyResponseObject
import pt.ipp.estg.trashtalkerapp.retrofitService.IGeopify
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Executors

class DiscoverFragment : Fragment() {
    private lateinit var myContext : Context
    private lateinit var findMySelfButton : ImageButton
    private lateinit var findButton : ImageButton
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var radiusText : TextInputEditText
    private lateinit var geopifyGeopifyResponseObject: GeopifyResponseObject
    private var googleMap: GoogleMap? = null
    private val felgueiras = LatLng(41.36735, -8.20094)

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.addMarker(
            MarkerOptions()
                .position(felgueiras)
                .title("Eu")
        ).showInfoWindow()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(felgueiras,10f))
        this.googleMap = googleMap
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_discover, container, false)

        findMySelfButton = view.findViewById(R.id.findMySelfButton)
        findButton = view.findViewById(R.id.findButton)
        radiusText = view.findViewById(R.id.radiusText)

        findMySelfButton.setOnClickListener {
            this.googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(felgueiras,10f))
        }


        findButton.setOnClickListener {
            val radius = radiusText.text.toString()
            if (radius.isNullOrEmpty()) {
                Toast.makeText(myContext,"Por favor preencha o raio!", Toast.LENGTH_LONG).show()
            } else if (radius.toInt() in 1..20000) {
                googleMap!!.clear()
                var filter = "circle:-8.20094,41.36735,"
                val bias = "proximity:-8.20094,41.36735"
                val retrofitClient = IGeopify.getApi()
                filter += radius
                val responseCallback = retrofitClient.findPlaces(filter, bias)
                responseCallback.enqueue(object: retrofit2.Callback<GeopifyResponseObject> {
                    override fun onResponse(call: Call<GeopifyResponseObject>, geopifyResponse: Response<GeopifyResponseObject>) {
                        if(geopifyResponse.code() === 200){
                            Log.d("asd", geopifyResponse.body().toString())
                            geopifyGeopifyResponseObject = geopifyResponse.body() as GeopifyResponseObject
                            loadMarkers(geopifyGeopifyResponseObject)
                        } else{
                            Toast.makeText(myContext,"Something went wrong!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<GeopifyResponseObject>, t: Throwable) {
                        Toast.makeText(myContext,t.message.toString(), Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                Toast.makeText(myContext,"O raio não pode ser 0 nem ultrapassar 20km!", Toast.LENGTH_LONG).show()
            }

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)
    }

    private fun loadMarkers(obj: GeopifyResponseObject) {
        val locals = obj.features
        for (local in locals) {
            val position = LatLng(local.properties!!.lat!!, local.properties!!.lon!!)
            var info = "Fitness: "
            info += "${local.properties!!.formatted}"
            Log.d("asd", "tentou adicionar marker")
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Ponto de Desporto")
                    .snippet(info)
            )
        }
    }
}
