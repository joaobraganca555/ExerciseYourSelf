package pt.ipp.estg.cmu_exerciseyourself.interfaces

import com.google.android.material.textfield.TextInputLayout

interface IAuthentication {
    fun login(email: String, password: String, passwordLayout: TextInputLayout, emailLayout: TextInputLayout)
    fun startRegisterFragment()
    fun register(email: String, password: String)
}