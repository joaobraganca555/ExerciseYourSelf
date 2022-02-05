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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatAdapter(var listWorkouts:List<WorkoutChat>): RecyclerView.Adapter<ChatAdapter.MyViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        var view = LayoutInflater.from(parent.context).inflate(
            R.layout.chat_item_layout,
            parent,false)
        return MyViewholder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val formatted = listWorkouts[position].data
        holder.txtBeginDate.text = formatted
        when(listWorkouts[position].sport){
            Sport.RUNNING_OUTDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_running)
                holder.txtSport.text = "Corrida"
            }
            Sport.RUNNING_INDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_running)
                holder.txtSport.text = "Corrida"
            }
            Sport.WALKING.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_walking)
                holder.txtSport.text = "Caminhada"
            }
            Sport.GYM.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_walking)
                holder.txtSport.text = "Ginásio"
            }
            Sport.HOME_TRAINING.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_walking)
                holder.txtSport.text = "Treino em Casa"
            }
        }
    }

    override fun getItemCount(): Int = listWorkouts.size

    fun updateList(newListChallenges: List<WorkoutChat>){
        this.listWorkouts = newListChallenges
        this.notifyDataSetChanged()
    }

    inner class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var txtSport: TextView
        var imgSport: ImageView
        var txtBeginDate: TextView

        init{
            txtSport = itemView.findViewById(R.id.txtSport)
            imgSport = itemView.findViewById(R.id.imgSport)
            txtBeginDate = itemView.findViewById(R.id.txtDateBegin)
        }
    }
}