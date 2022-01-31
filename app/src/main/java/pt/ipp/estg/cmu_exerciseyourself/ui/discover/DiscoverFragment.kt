package pt.ipp.estg.cmu_exerciseyourself.ui.discover

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.ResponseObject
import pt.ipp.estg.trashtalkerapp.retrofitService.TrashtalkerAPI
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Executors

class DiscoverFragment : Fragment() {
    private lateinit var myContext : Context
    private lateinit var findMySelfButton : Button
    private lateinit var findButton : Button
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var radiusText : TextInputEditText
    private lateinit var geopifyResponseObject: ResponseObject
    private val felgueiras = LatLng(41.36735, -8.20094)

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.addMarker(
            MarkerOptions()
                .position(felgueiras)
                .title("Eu")
        )
        googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(felgueiras,10f))
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
        var view = inflater.inflate(R.layout.fragment_discover, container, false)

        findMySelfButton = view.findViewById(R.id.findMySelfButton)
        findButton = view.findViewById(R.id.findButton)
        radiusText = view.findViewById(R.id.radiusText)

        findMySelfButton.setOnClickListener {
            mapFragment?.getMapAsync(callback)
        }


        findButton.setOnClickListener {
            Log.d("asd",radiusText.text.toString())
                var retrofitClient = TrashtalkerAPI.getApi()
                val responseCallback = retrofitClient.findPlaces(radiusText.text.toString())
                responseCallback.enqueue(object: retrofit2.Callback<ResponseObject> {
                    override fun onResponse(call: Call<ResponseObject>, response: Response<ResponseObject>) {
                        if(response.code() === 200){
                            Log.d("asd", response.body().toString())
                            geopifyResponseObject = response.body() as ResponseObject
                        } else{
                            Toast.makeText(myContext,"Something went wrong!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                        Toast.makeText(myContext,t.message.toString(), Toast.LENGTH_LONG).show()
                    }
                })
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment?.getMapAsync(callback)
    }

}
