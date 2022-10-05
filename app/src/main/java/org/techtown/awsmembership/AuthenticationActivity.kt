package org.techtown.awsmembership

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.auth.userpools.SignUpActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.SignInResult
import com.amazonaws.mobile.client.results.SignInState


class AuthenticationActivity : AppCompatActivity() {
    private val TAG = AuthenticationActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val button = findViewById<Button>(R.id.button2) // 로그인 버튼
        val sign_button = findViewById<Button>(R.id.signup_button) // 회원가입 버튼
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())
                    when (userStateDetails.userState) {
                        UserState.SIGNED_IN -> {
                            val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
                            startActivity(i)
                        }
                        else -> {}
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })

        // 로그인 버튼
        button.setOnClickListener { showSignIn() }

        // 회원가입 버튼
        sign_button.setOnClickListener {
            val i = Intent(this@AuthenticationActivity, SignUpActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    // 로그인 함수
    private fun showSignIn() {
        val login_id = findViewById<EditText>(R.id.login_id)
        val login_paw = findViewById<EditText>(R.id.login_paw)
        val username = login_id.text.toString()
        val password = login_paw.text.toString()
        AWSMobileClient.getInstance()
            .signIn(username, password, null, object : Callback<SignInResult> {
                override fun onResult(signInResult: SignInResult) {
                    runOnUiThread {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.signInState)
                        when (signInResult.signInState) {
                            SignInState.DONE -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign-in done.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
                                startActivity(i)
                                finish()
                            }
                            SignInState.SMS_MFA -> Toast.makeText(
                                applicationContext,
                                "Please confirm sign-in with SMS.",
                                Toast.LENGTH_SHORT
                            ).show()
                            SignInState.NEW_PASSWORD_REQUIRED -> Toast.makeText(
                                applicationContext,
                                "Please confirm sign-in with new password.",
                                Toast.LENGTH_SHORT
                            ).show()
                            else -> Toast.makeText(
                                applicationContext,
                                "Unsupported sign-in confirmation: " + signInResult.signInState,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, "Sign-in error", e)
                }
            })
    }
}