package pt.ipp.estg.cmu_exerciseyourself.model.room.entities

import androidx.room.Embedded
import androidx.room.Relation

class WorkoutWithCoord (
    @Embedded
    val workout:Workouts,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "workoutId"
    )
    val coordinates:List<Coordinates>
    )