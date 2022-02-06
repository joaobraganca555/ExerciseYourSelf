package pt.ipp.estg.cmu_exerciseyourself.interfaces

import pt.ipp.estg.cmu_exerciseyourself.utils.ViewMode

interface IServiceController {
    fun startAutomaticExercise()
    fun stopAutomaticExercise()
    fun openAddActivity()
    fun openExerciseView()
    fun openMeasurementView()
}