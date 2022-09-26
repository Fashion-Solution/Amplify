package org.techtown.awsmembership

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val signOut_button = findViewById<Button>(R.id.signOut_button) // 로그아웃 버튼

        // 로그아웃 버튼
        signOut_button.setOnClickListener {
            AWSMobileClient.getInstance().initialize(
                applicationContext,
                object : Callback<UserStateDetails?> {
                    override fun onResult(userStateDetails: UserStateDetails?) {
                        // 로그아웃 후 로그인 창으로 이동
                        AWSMobileClient.getInstance().signOut()
                        val i = Intent(this@MainActivity, AuthenticationActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                    override fun onError(e: Exception) {}
                })
        }
    }
}