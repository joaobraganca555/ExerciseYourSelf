package pt.ipp.estg.cmu_exerciseyourself.ui.profile

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import pt.ipp.estg.cmu_exerciseyourself.databinding.FragmentProfileBinding
import pt.ipp.estg.cmu_exerciseyourself.model.models.UserProfile
import android.graphics.Bitmap


import android.graphics.BitmapFactory




class ProfileFragment : Fragment() {
    private val IMAGE_REQUEST: Int = 2
    private var _binding: FragmentProfileBinding? = null
    private lateinit var saveChanges: Button
    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    private lateinit var nameText: TextInputEditText
    private lateinit var birthDate: TextInputEditText
    private lateinit var uploadButton: ImageButton
    private lateinit var profileImage: ImageView
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private lateinit var myContext: Context
    private val binding get() = _binding!!
    private val TAG = "ProfileFragment"
    private lateinit var imageUri: Uri

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        saveChanges = binding.btnUpdateUser
        emailText = binding.email
        nameText = binding.name
        passwordText = binding.password
        birthDate = binding.birthDate
        uploadButton = binding.uploadImageButton
        profileImage = binding.imageView

        if (!getImage(profileImage)) {
            profileImage.setImageResource(pt.ipp.estg.cmu_exerciseyourself.R.drawable.ic_baseline_person_24)
        }

        val docRef = db.collection("users").document(auth.currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    emailText.setText(document.get("email").toString())
                    nameText.setText(document.get("name").toString())
                    birthDate.setText(document.get("birthDate").toString())
                    uploadButton.isClickable = true
                } else {
                    Toast.makeText(myContext, "Este utilizador não existe!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        saveChanges.setOnClickListener {
            if (!passwordText.text.isNullOrBlank()) {
                if (passwordText.text.toString().length < 6) {
                    Toast.makeText(
                        myContext,
                        "Password deve conter mais de 6 caracteres!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    updateCredentials(emailText.text.toString(), passwordText.text.toString())
                }
            } else {
                updateCredentials(emailText.text.toString(), "")
            }
        }

        uploadButton.setOnClickListener {
            openImage()
        }

        return root
    }

    private fun getImage(profileImage: ImageView): Boolean {
        var result = false
        val storage = FirebaseStorage.getInstance()
        val userAuth = Firebase.auth

        val storageRef = storage.getReference()
        var islandRef = storageRef.child("images/${userAuth.currentUser!!.uid}.jpg")

        val ONE_MEGABYTE: Long = 20480 * 20480
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            // Data for "images/island.jpg" is returned, use this as needed
            Log.d(TAG, "getImage: Imagem descarregada!")
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            profileImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, profileImage.width, profileImage.height, false))
            result = true
        }.addOnFailureListener {
            // Handle any errors
            Log.d(TAG, "getImage: Erro ao descarregar imagem!")
            Toast.makeText(myContext, "Erro ao descarregar imagem! Tamanho elevado!", Toast.LENGTH_LONG).show()
        }
        return result
    }

    private fun openImage() {
        val intent = Intent()
        intent.setType("image/")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data!!.data!!
            uploadImage()
        }
    }

    private fun uploadImage() {
        val pd = ProgressDialog(myContext)
        pd.setMessage("Uploading Image...")
        pd.show()

        val storage = FirebaseStorage.getInstance()
        val userAuth = Firebase.auth

        val storageRef = storage.getReference()

        val imagesRef = storageRef.child("images")

        val filename = "${userAuth.currentUser!!.uid}.${getFileExtension(imageUri)}"
        Log.d(TAG, "uploadImage: " + filename)
        val spaceRef = imagesRef.child(filename)

        val uploadTask: UploadTask = spaceRef.putFile(imageUri)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            pd.dismiss()
            Toast.makeText(myContext, "Imagem atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            getImage(profileImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCredentials(newEmail: String, newPassword: String) {
        // [START update_email]
        val user = Firebase.auth.currentUser

        user!!.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email address updated.")
                }
            }
        // [END update_email]

        if (!newPassword.isNullOrBlank()) {
            // [START update_password]
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                    }
                }
            // [END update_password]
        }

        val newUser = UserProfile(nameText.text.toString(), birthDate.text.toString(), newEmail)
        updateUserInFirestore(newUser)
    }

    private fun updateUserInFirestore(newUser: UserProfile) {
        db.collection("users").document(auth.currentUser!!.uid)
            .set(newUser)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot updated with ID: ${auth.currentUser!!.uid}")
                Toast.makeText(
                    myContext,
                    "Dados atualizados com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getFileExtension(imageUri: Uri): String {
        val cr = myContext.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(imageUri)).toString()
    }
}