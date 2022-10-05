package org.techtown.awsmembership

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.results.SignUpResult


class SignUpActivity : AppCompatActivity() {
    var TAG: String = AuthenticationActivity::class.java.getSimpleName()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val signUp_button2 = findViewById<Button>(R.id.signUp_button2) // 회원 가입 버튼

        // 회원 가입 버튼
        signUp_button2.setOnClickListener {
            // 이름, 아이디(이메일), 비밀번호 순
            val signUpName = findViewById<View>(R.id.signUpName) as EditText
            val signUpUsername = findViewById<View>(R.id.signUpUsername) as EditText
            val signUpPassword = findViewById<View>(R.id.signUpPassword) as EditText
            val name = signUpName.text.toString()
            val username = signUpUsername.text.toString()
            val password = signUpPassword.text.toString()
            val attributes: MutableMap<String, String> =
                HashMap()
            attributes["name"] = name
            attributes["email"] = username
            Log.d("TTT", attributes.toString())
            AWSMobileClient.getInstance().signUp(
                username,
                password,
                attributes,
                null,
                object :
                    Callback<SignUpResult> {
                    override fun onResult(signUpResult: SignUpResult) {
                        runOnUiThread {
                            Log.d(
                                TAG,
                                "Sign-up callback state: " + signUpResult.confirmationState
                            )
                            if (!signUpResult.confirmationState) {
                                val details = signUpResult.userCodeDeliveryDetails
                                Toast.makeText(
                                    applicationContext,
                                    "인증 메일을 보냈습니다.: " + details.destination,
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 이메일에 문제가 없으면 인증 코드 창으로 이동
                                val i = Intent(this@SignUpActivity, OkActivity::class.java)
                                i.putExtra("email", username) // username을 인증 코드 창에서 사용하기 위해
                                startActivity(i)
                                finish()
                            } else {
                                // 인증 코드 창으로 이동
                                Toast.makeText(
                                    applicationContext,
                                    "Sign-up done.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(TAG, "Sddd")
                            }
                        }
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, "Sign-up error", e)
                    }
                })
        }
    }

    // 뒤로가기 2번 눌러야 종료
    private val FINISH_INTERVAL_TIME: Long = 1000
    private var backPressedTime: Long = 0
    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - backPressedTime

        // 뒤로 가기 할 경우 AuthActivity 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            val i = Intent(this@SignUpActivity, AuthenticationActivity::class.java)
            startActivity(i)
            finish()
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}