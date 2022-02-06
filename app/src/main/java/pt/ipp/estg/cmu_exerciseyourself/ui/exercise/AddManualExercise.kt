package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.ListDays
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.OpenWeatherResponse
import pt.ipp.estg.cmu_exerciseyourself.model.retrofit.OpenWeatherService
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.SportsAdapter
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

const val KELVIN = 273.15

class AddManualExercise : Fragment(),ComunicationSportFragment {
    lateinit var txtMaxTemp:TextView
    lateinit var txtHumidade:TextView
    var sportActivity:Sport = Sport.RUNNING_OUTDOOR
    lateinit var imgWeather:ImageView
    lateinit var forecastView:View
    var forecastForDate:List<ListDays> = ArrayList()
    lateinit var repository: FitnessRepository
    lateinit var recViewChoices:RecyclerView
    var listSports = ArrayList<String>(ArrayList(
        Arrays.asList("RUNNING_OUTDOOR", "WALKING", "GYM", "HOME_TRAINING", "OTHER")))
    lateinit var txtBeginDate: TextInputEditText
    lateinit var txtDistance: TextInputEditText
    lateinit var txtCal: TextInputEditText
    lateinit var txtPlaces:TextInputEditText
    lateinit var txtDuration: TextInputEditText
    lateinit var txtFootSteps: TextInputEditText
    lateinit var myContext: Context
    lateinit var checkBoxSchedule: CheckBox
    var activityAction: String = "Registar_Treino"
    var beginDateActivity: LocalDateTime? = null
    var forecast:OpenWeatherResponse? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_manual_exercise, container, false)
        repository = FitnessRepository(requireActivity().application)

        val beginDateImage = view.findViewById<ImageView>(R.id.beginDateImageButton)
        //View Forecast
        txtMaxTemp = view.findViewById(R.id.txtTempMax)
        txtHumidade = view.findViewById(R.id.txtHumidade)
        imgWeather = view.findViewById(R.id.imgWeather)
        forecastView = view.findViewById(R.id.viewForecast)
        txtPlaces = view.findViewById(R.id.txtPlace)
        recViewChoices = view.findViewById(R.id.recViewSportChoices)
        val sportsAdapter = SportsAdapter(listSports, myContext,this)
        recViewChoices.apply {
            adapter = sportsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        }
        txtBeginDate = view.findViewById(R.id.dateText)
        txtDistance = view.findViewById(R.id.txtDistance)
        txtCal = view.findViewById(R.id.txtCal)
        txtDuration = view.findViewById(R.id.txtDuration)
        txtFootSteps = view.findViewById(R.id.txtNumFootsteps)
        checkBoxSchedule = view.findViewById(R.id.checkbox_schedule)


        checkBoxSchedule.setOnClickListener {
            if (this.activityAction.equals("Agendar_Treino")) {
                this.activityAction = "Registar_Treino"
                forecast = null
                updateUI(false)
            } else {
                this.activityAction = "Agendar_Treino"
                if(!txtBeginDate.text.isNullOrEmpty() && !txtPlaces.text.isNullOrEmpty()
                    && beginDateActivity!!.isAfter(LocalDateTime.now()) &&
                    ChronoUnit.DAYS.between(LocalDateTime.now(),beginDateActivity) <= 5){
                    fetchForecast(txtPlaces.text.toString())
                }
            }
        }

        var myCalendar = Calendar.getInstance();

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(myCalendar)
        }

        view.findViewById<Button>(R.id.saveBtn).setOnClickListener {
            registerActivity()
        }

        beginDateImage.setOnClickListener {
            activity?.let { activity ->
                DatePickerDialog(
                    activity,
                    datePicker,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        // Inflate the layout for this fragment
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        txtBeginDate.setText(sdf.format(myCalendar.time))
        beginDateActivity = LocalDateTime.ofInstant(myCalendar.time.toInstant(), ZoneId.systemDefault())
    }

    private fun fetchForecast(place:String){
        Executors.newFixedThreadPool(1).execute {
            OpenWeatherService.getApi().getForecast(place).enqueue(object: retrofit2.Callback<OpenWeatherResponse> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<OpenWeatherResponse>, geopifyResponse: Response<OpenWeatherResponse>) {
                    if(geopifyResponse.code() === 200){
                        Log.d("asd", geopifyResponse.body().toString())
                        var listResults:ArrayList<ListDays>? = null
                        forecast = geopifyResponse.body() as OpenWeatherResponse
                        forecast?.let {
                            listResults = it.list
                        }
                        forecastForDate = listResults!!.filter {
                            LocalDateTime.parse(it.dtTxt?.replace(" ","T")).dayOfMonth
                                .equals(beginDateActivity?.dayOfMonth) &&
                            LocalDateTime.parse(it.dtTxt?.replace(" ","T")).month
                                .equals(beginDateActivity?.month) &&
                            LocalDateTime.parse(it.dtTxt?.replace(" ","T")).year
                                .equals(beginDateActivity?.year)
                        }
                        updateUI(true)
                    } else{
                        Log.d("asd", "something went wrong")
                    }
                }

                override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                    Log.d("asd", t.message.toString())
                }
            })
        }
    }

    private fun updateUI(visibility:Boolean){
        if(visibility){
            populateUIForecast()
        }else{
            this.forecastView.visibility = View.GONE
        }
    }

    private fun populateUIForecast(){

        val tempMax = BigDecimal((this.forecastForDate?.get(4)?.main?.tempMax?.minus(KELVIN)!!)).setScale(2, RoundingMode.HALF_EVEN)
        this.txtMaxTemp.text = "Temp: " + tempMax.toString() + " ºC"

        this.txtHumidade.text = "Humidade: " + this.forecastForDate?.get(4)?.main?.humidity.toString()

        val resourceID:Int = when(this.forecastForDate?.get(4)?.weather?.get(0)?.main){
            "Clouds" -> {
                R.drawable.ic_clouds
            }
            "Clear" -> {
                R.drawable.ic_sun
            }
            "Rain" -> {
                R.drawable.ic_rains
            }
            "Snow" -> {
                R.drawable.ic_snow
            }
            else -> {
                R.drawable.ic_clouds
            }
        }
        this.imgWeather.setImageResource(resourceID)
        this.forecastView.visibility = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerActivity() {
        val distance: Double
        val footSteps: Int
        val beginDateTime: String
        val calories: Int
        val duration: Int
        val status: String

        distance = if (txtDistance.text.toString().isEmpty()) 0.0 else txtDistance.text.toString().toDouble()
        footSteps = if (txtFootSteps.text.toString().isEmpty()) 0 else txtFootSteps.text.toString().toInt()
        calories = if (txtCal.text.toString().isEmpty()) 0 else txtCal.text.toString().toInt()
        duration = if (txtDuration.text.toString().isEmpty()) 0 else txtDuration.text.toString().toInt()
        status = if (this.activityAction == "Registar_Treino") Status.SUCCESSFULLY.toString() else Status.PLANNED.toString()
        if (beginDateActivity == null) {
            Toast.makeText(myContext, "Data de Inicio deve ser definida", Toast.LENGTH_LONG).show()
            return
        } else if (beginDateActivity!!.isAfter(LocalDateTime.now())
            && activityAction == "Registar_Treino"
        ) {
            Toast.makeText(myContext, "A data deve ser anterior à data atual", Toast.LENGTH_LONG)
                .show()
        } else if (beginDateActivity!!.isBefore(LocalDateTime.now())
            && activityAction == "Agendar_Treino"
        ) {
            Toast.makeText(myContext, "A data deve ser posterior à data atual", Toast.LENGTH_LONG)
                .show()
        } else {
            Executors.newFixedThreadPool(1).execute {
                val workout = Workouts(
                    sport = sportActivity.toString(),
                    status = status,
                    distance = distance,
                    duration = duration.toString(),
                    local = "",
                    footsteps = footSteps,
                    beginDate = beginDateActivity.toString(),
                    finishedDate = beginDateActivity.toString(),
                    workoutId = null,
                    calories = calories
                )
                Log.d("WORKOUTS", workout.toString())
                val listCoord = ArrayList<Coordinates>()
                val workoutWithCoord = WorkoutWithCoord(workout, listCoord)

                repository.insertWorkoutWithCoord(workoutWithCoord)
            }
            (myContext as IServiceController).openExerciseView()
        }
    }

    override fun updateSport(sport:Sport) {
        this.sportActivity = sport
        Log.d("asd", "updateSport = $sport")
    }
}

interface ComunicationSportFragment{
    fun updateSport(sport:Sport)
}