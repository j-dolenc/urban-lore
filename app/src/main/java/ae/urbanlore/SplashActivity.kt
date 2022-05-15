package ae.urbanlore

import ae.urbanlore.MapsActivity
import ae.urbanlore.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}