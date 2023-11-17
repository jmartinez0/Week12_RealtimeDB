package edu.farmingdale.alrajab.week12_auth_ml_api_demo

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import edu.farmingdale.alrajab.week12_auth_ml_api_demo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpTv.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener{
            login();
        }

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()

        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val email = credential.id
                    Toast.makeText(applicationContext, "Email : $email", Toast.LENGTH_SHORT).show()
                }
                Log.d("TA6", "Got ID token.")
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

        binding.googleBtn.setOnClickListener {
            Log.d(TAG, "GGGGGGGGGGGGGGGG")
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(
                            result.pendingIntent.intentSender).build()
                        activityResultLauncher.launch(intentSenderRequest)
                    } catch (e: Exception) {
                        Log.d(TAG, e.localizedMessage)
                    }
                }
                .addOnFailureListener(this) { e ->
                    Log.d(TAG, e.localizedMessage)
                }
        }
    }

    private fun login() {
        val email = binding.emailET.text.toString()
        val pass = binding.passET.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, LandingActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

        }
    }


}