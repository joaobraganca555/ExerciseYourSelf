package pt.ipp.estg.cmu_exerciseyourself.utils

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import pt.ipp.estg.cmu_exerciseyourself.R
import pt.ipp.estg.cmu_exerciseyourself.ui.exercise.ComunicationSportFragment

class SportsAdapter(var listSports:List<String>, var context: Context,var hostFragment:ComunicationSportFragment): RecyclerView.Adapter<SportsAdapter.MyViewholder>() {
    var selectedItemPos = RecyclerView.NO_POSITION
    var lastItemSelectedPos = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
        var view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_sports_item,
            parent,false)
        return MyViewholder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        when(listSports[position]){
            Sport.RUNNING_OUTDOOR.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_running)
                holder.txtSport.text = "Corrida"
                if(position == selectedItemPos)
                    holder.selectedBg()
                else
                    holder.defaultBg()
            }
            Sport.WALKING.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_walking)
                holder.txtSport.text = "Caminhada"
                if(position == selectedItemPos)
                    holder.selectedBg()
                else
                    holder.defaultBg()
            }
            Sport.GYM.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_supino)
                holder.txtSport.text = "Ginásio"
                if(position == selectedItemPos)
                    holder.selectedBg()
                else
                    holder.defaultBg()
            }
            Sport.HOME_TRAINING.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_self_improvement_24)
                holder.txtSport.text = "Treino em Casa"
                if(position == selectedItemPos)
                    holder.selectedBg()
                else
                    holder.defaultBg()
            }
            Sport.OTHER.toString() ->{
                holder.imgSport.setImageResource(R.drawable.ic_baseline_sports_martial_arts)
                holder.txtSport.text = "Outro Treino"
                if(position == selectedItemPos)
                    holder.selectedBg()
                else
                    holder.defaultBg()
            }
        }
    }

    override fun getItemCount(): Int = listSports.size

    inner class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtSport: TextView
        var imgSport: ImageView
        var linearLayout: View

        init{
            txtSport = itemView.findViewById(R.id.txtSportChoice)
            imgSport = itemView.findViewById(R.id.imgSportChoice)
            linearLayout = itemView.findViewById(R.id.linearLayout)

            itemView.setOnClickListener {
                var choice:Sport = when(txtSport.text.toString()){
                    "Corrida" -> Sport.RUNNING_OUTDOOR
                    "Caminhada" -> Sport.WALKING
                    "Ginásio" -> Sport.GYM
                    "Treino em Casa" -> Sport.HOME_TRAINING
                    "Outro Treino" -> Sport.OTHER
                    else -> Sport.OTHER
                }

                (hostFragment as ComunicationSportFragment).updateSport(choice)
                selectedItemPos = adapterPosition
                if(lastItemSelectedPos == RecyclerView.NO_POSITION)
                    lastItemSelectedPos = selectedItemPos
                else {
                    notifyItemChanged(lastItemSelectedPos)
                    lastItemSelectedPos = selectedItemPos
                }
                notifyItemChanged(selectedItemPos)
            }

        }

        fun defaultBg() {
            linearLayout.background = context.getDrawable(R.drawable.unselected_item)
        }

        fun selectedBg() {
            linearLayout.background = context.getDrawable(R.drawable.selected_item)
        }
    }
}