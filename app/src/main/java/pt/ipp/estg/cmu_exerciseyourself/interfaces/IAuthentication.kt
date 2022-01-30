package pt.ipp.estg.cmu_exerciseyourself.interfaces

import com.google.android.material.textfield.TextInputLayout
import pt.ipp.estg.cmu_exerciseyourself.model.models.UserProfile

interface IAuthentication {
    fun login(email: String, password: String, passwordLayout: TextInputLayout, emailLayout: TextInputLayout)
    fun startRegisterFragment()
    fun register(email: String, password: String, user: UserProfile)
}