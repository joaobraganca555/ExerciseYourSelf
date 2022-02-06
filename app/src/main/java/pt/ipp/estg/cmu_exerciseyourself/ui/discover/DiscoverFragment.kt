package pt.ipp.estg.cmu_exerciseyourself.ui.discover

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.GeopifyResponseObject
import pt.ipp.estg.cmu_exerciseyourself.utils.PERMISSION_REQUEST_CODE
import pt.ipp.estg.trashtalkerapp.retrofitService.IGeopify
import retrofit2.Call
import retrofit2.Response

class DiscoverFragment : Fragment() {
    private lateinit var myContext : Context
    private lateinit var refreshLocation : ImageView
    private lateinit var oneKilometer : Button
    private lateinit var fiveKilometer : Button
    private lateinit var teenKilometer : Button
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var geopifyGeopifyResponseObject: GeopifyResponseObject
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "DiscoverFragment"

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        if (ActivityCompat.checkSelfPermission(
                myContext,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                myContext,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "SEM PERMISSOES")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            return@OnMapReadyCallback
        } else {
            setUpMap()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpMap() {
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity()) { location ->
                if (location != null) {
                    Log.d(TAG, "getLastKnownLocation == ${location} ")
                    lastLocation = LatLng(location.latitude, location.longitude)
                    mMap.clear()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,12f))
                    oneKilometer.isEnabled = true
                    fiveKilometer.isEnabled = true
                    teenKilometer.isEnabled = true
                } else {
                        oneKilometer.isEnabled = false
                        fiveKilometer.isEnabled = false
                        teenKilometer.isEnabled = false
                    Toast.makeText(myContext, "Por favor, ative a localização!", Toast.LENGTH_LONG).show()
                }
            }
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

        refreshLocation = view.findViewById(R.id.refreshLocation)
        oneKilometer = view.findViewById(R.id.oneKilometer)
        fiveKilometer = view.findViewById(R.id.fiveKilometer)
        teenKilometer = view.findViewById(R.id.teenKilometer)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)

        refreshLocation.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)
            mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
            mapFragment.getMapAsync(callback)
        }

        /*
        findPlacesButton.setOnClickListener {
            val radius = radiusText.text.toString()
            if (radius.isNullOrEmpty()) {
                Toast.makeText(myContext,"Por favor preencha o raio!", Toast.LENGTH_LONG).show()
            } else if (radius.toInt() in 1..20000) {
                //locationHelper.stopUpdates()
                mMap.clear()
                var filter = "circle:${lastLocation.longitude},${lastLocation.latitude},"
                val bias = "proximity:${lastLocation.longitude},${lastLocation.latitude}"
                val retrofitClient = IGeopify.getApi()
                filter += radius
                val responseCallback = retrofitClient.findPlaces(filter, bias)
                responseCallback.enqueue(object: retrofit2.Callback<GeopifyResponseObject> {

                    override fun onResponse(call: Call<GeopifyResponseObject>, geopifyResponse: Response<GeopifyResponseObject>) {
                        if(geopifyResponse.code() == 200){
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
            }else {
                 Toast.makeText(myContext,"O raio não pode ser 0 nem ultrapassar 20km!", Toast.LENGTH_LONG).show()
            }
        }*/

        oneKilometer.setOnClickListener {
            getPlacesWithCertainRadius(1000)
        }

        fiveKilometer.setOnClickListener {
            getPlacesWithCertainRadius(5000)
        }

        teenKilometer.setOnClickListener {
            getPlacesWithCertainRadius(10000)
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
            Log.d(TAG, "loadMarkers: Adicinou novo marker!")
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Ponto de Desporto")
                    .snippet(info)

            )
        }
    }


    private fun getPlacesWithCertainRadius(radius: Int) {
            mMap.clear()
            var filter = "circle:${lastLocation.longitude},${lastLocation.latitude},${radius}"
            val bias = "proximity:${lastLocation.longitude},${lastLocation.latitude}"
            val retrofitClient = IGeopify.getApi()
            val responseCallback = retrofitClient.findPlaces(filter, bias)
            responseCallback.enqueue(object: retrofit2.Callback<GeopifyResponseObject> {
                override fun onResponse(call: Call<GeopifyResponseObject>, geopifyResponse: Response<GeopifyResponseObject>) {
                    if(geopifyResponse.code() == 200){
                        Log.d(TAG, geopifyResponse.body().toString())
                        geopifyGeopifyResponseObject = geopifyResponse.body() as GeopifyResponseObject
                        loadMarkers(geopifyGeopifyResponseObject)
                    } else{
                        Toast.makeText(myContext,"Aconteceu um erro! Por favor verifique se tem internet!", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GeopifyResponseObject>, t: Throwable) {
                    Toast.makeText(myContext,t.message.toString(), Toast.LENGTH_LONG).show()
                }
            })
    }
}
