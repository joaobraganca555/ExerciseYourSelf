package pt.ipp.estg.cmu_exerciseyourself.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.MeasurementsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.dao.WorkoutsDao
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Coordinates
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Measurements
import pt.ipp.estg.cmu_exerciseyourself.model.room.entities.Workouts

@Database(entities = arrayOf(Workouts::class,Coordinates::class,Measurements::class),version = 10)
abstract class FitnessDb: RoomDatabase() {
    companion object{
        const val DATABASE_NAME = "FitnessDb"
        @Volatile
        private var INSTANCE:FitnessDb? = null

        fun getInstance(context: Context):FitnessDb{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                    FitnessDb::class.java,
                    DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    abstract fun WorkoutsDao(): WorkoutsDao
    abstract fun MeasurementsDao(): MeasurementsDao
}