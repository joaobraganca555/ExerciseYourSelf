package pt.ipp.estg.cmu_exerciseyourself.ui.exercise

import android.app.DatePickerDialog
import android.content.Context
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.interfaces.IServiceController
import pt.ipp.estg.cmu_exerciseyourself.model.room.FitnessRepository
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.WorkoutWithCoord
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import pt.ipp.estg.cmu_exerciseyourself.utils.Sport
import pt.ipp.estg.cmu_exerciseyourself.utils.Status
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.Executors

class AddManualExercise : Fragment() {
    lateinit var repository: FitnessRepository
    lateinit var txtBeginDate: TextInputEditText
    lateinit var txtDistance: TextInputEditText
    lateinit var txtCal: TextInputEditText
    lateinit var txtDuration: TextInputEditText
    lateinit var txtFootSteps: TextInputEditText
    lateinit var myContext: Context
    lateinit var checkBoxSchedule: CheckBox
    var activityAction: String = "Registar_Treino"
    var beginDateActivity: LocalDateTime? = null

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
        txtBeginDate = view.findViewById(R.id.dateText)
        txtDistance = view.findViewById(R.id.txtDistance)
        txtCal = view.findViewById(R.id.txtCal)
        txtDuration = view.findViewById(R.id.txtDuration)
        txtFootSteps = view.findViewById(R.id.txtNumFootsteps)
        checkBoxSchedule = view.findViewById(R.id.checkbox_schedule)

        checkBoxSchedule.setOnClickListener {
            if (this.activityAction.equals("Agendar_Treino")) {
                this.activityAction = "Registar_Treino"
            } else {
                this.activityAction = "Agendar_Treino"
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
                    sport = Sport.RUNNING_OUTDOOR.toString(),
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
}