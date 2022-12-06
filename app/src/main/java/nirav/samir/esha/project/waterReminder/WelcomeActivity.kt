package nirav.samir.esha.project.waterReminder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        this.getStartedBtn.setOnClickListener {
            startActivity(Intent(this, UserInfoRegisterActivity::class.java))
            finish()
        }
    }
}
