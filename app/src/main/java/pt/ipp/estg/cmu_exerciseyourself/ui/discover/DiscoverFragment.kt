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
    private lateinit var refreshLocation : Button
    private lateinit var findPlacesButton : ImageButton
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var radiusText : TextInputEditText
    private lateinit var geopifyGeopifyResponseObject: GeopifyResponseObject
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private var locationHelper = LocationHelper()
    //var currentLat:Double? = null
    //var currentLong:Double? = null
    //var isUpdatingLocation:Boolean = true

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
            Log.d("asd", "SEM PERMISSOES")
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
                    Log.d("asd", "getLastKnownLocation== ${location} ")
                    lastLocation = LatLng(location.latitude, location.longitude)
                    mMap.clear()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,12f))
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
        findPlacesButton = view.findViewById(R.id.findButton)
        radiusText = view.findViewById(R.id.radiusText)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)

        refreshLocation.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(myContext)
            mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
            mapFragment.getMapAsync(callback)
            //setUpMap()
        }

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

        //Adicionar o ponto onde a pessoa se encontra
        /*
        mMap.addMarker(
            MarkerOptions()
            .position(lastLocation)
            .title("Eu")).showInfoWindow()
         */

        for (local in locals) {
            val position = LatLng(local.properties!!.lat!!, local.properties!!.lon!!)
            var info = "Fitness: "
            info += "${local.properties!!.formatted}"
            Log.d("asd", "tentou adicionar marker")
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("Ponto de Desporto")
                    .snippet(info)

            )
        }
    }

    /*
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                myContext,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                myContext,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("asd", "SEM PERMISSOES")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            mapFragment.getMapAsync(callback)
            return
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        Log.d("asd", "getLastKnownLocation== ${location} ")
                        lastLocation = LatLng(location?.latitude!!, location.longitude)
                        mMap!!.clear()
                        mMap!!.addMarker(
                            MarkerOptions()
                                .position(lastLocation)
                                .title("Eu")
                        ).showInfoWindow()
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,12f))
                    }
                }
            /*
           locationHelper.startListeningUserLocation(
            myContext, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    var mLocation = location
                    currentLat = mLocation?.latitude
                    currentLong = mLocation?.longitude

                    if(currentLat != null && currentLong!=null){
                        actualLocation = LatLng(location?.latitude!!, location.longitude)
                        googleMap!!.clear()
                        googleMap!!.addMarker(
                            MarkerOptions()
                                .position(actualLocation)
                                .title("Eu")
                        ).showInfoWindow()
                        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(actualLocation,12f))
                    }

                }
            })*/
        }
    }

    override fun onStop() {
        super.onStop()
        locationHelper.stopUpdates()
        isUpdatingLocation = false
    }

    override fun onPause() {
        super.onPause()
        locationHelper.stopUpdates()
        isUpdatingLocation = false
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.stopUpdates()
        isUpdatingLocation = false
    }*/
}
