package pt.ipp.estg.cmu_exerciseyourself.utils

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChallengesAdapter(var listChallenges:List<Workouts>): RecyclerView.Adapter<ChallengesAdapter.MyViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.workout_recview_item_layout,
        parent,false)
        return MyViewholder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val date = LocalDateTime.parse(listChallenges[position].beginDate)
        val formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm")
        val formatted = date.format(formatter)
        holder.txtBeginDate.text = formatted
        when(listChallenges[position].sport){
            Sport.RUNNING_OUTDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_running)
                holder.txtSport.text = "Corrida"
            }
            Sport.RUNNING_INDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_running)
                holder.txtSport.text = "Corrida"
            }
            Sport.RUNNING_OUTDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_walking)
                holder.txtSport.text = "Caminhada"
            }
            Sport.GYM.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_supino)
                holder.txtSport.text = "Ginásio"
            }
            Sport.HOME_TRAINING.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_self_improvement_24)
                holder.txtSport.text = "Treino em Casa"
            }
            Sport.OTHER.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_sports_martial_arts)
                holder.txtSport.text = "Outro Treino"
            }
        }
    }

    override fun getItemCount(): Int = listChallenges.size

    fun updateList(newListChallenges: List<Workouts>){
        this.listChallenges = newListChallenges
        this.notifyDataSetChanged()
    }

    inner class MyViewholder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var txtSport:TextView
        var imgSport:ImageView
        var txtBeginDate:TextView

        init{
            txtSport = itemView.findViewById(R.id.txtSport)
            imgSport = itemView.findViewById(R.id.imgSport)
            txtBeginDate = itemView.findViewById(R.id.txtDateBegin)
        }
    }
}