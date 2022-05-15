package ae.urbanlore

import ae.urbanlore.databinding.ActivityAddLoreBinding
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import com.parse.ParseGeoPoint
import com.parse.ParseObject
import com.parse.Parse
import java.io.ByteArrayOutputStream

class AddLoreActivity : AppCompatActivity() {
    private lateinit var binding :ActivityAddLoreBinding

    val res = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            camImg(result.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        binding = ActivityAddLoreBinding.inflate(layoutInflater)


        listenerSetup()

        setContentView(binding.root)
    }


    fun listenerSetup(){
        binding.photo.setOnClickListener {
            takePhoto()
        }

        binding.post.setOnClickListener{
            val toBase = ParseObject("lore")
            toBase.put("name", binding.title.text.toString())
            toBase.put("description", binding.description.text.toString())

            val lat = intent.getStringExtra("LAT")
            val long = intent.getStringExtra("LONG")
            if(lat != null && long != null){
                toBase.put("latitude", lat)
                toBase.put("longitude", long)
            }
            val drawable = binding.photoHolder.drawable

            val bitMap:Bitmap = drawable.toBitmap()
            val byteStream = ByteArrayOutputStream()
            bitMap.compress(Bitmap.CompressFormat.JPEG,100,byteStream)
            val byteArr = byteStream.toByteArray()
            val imgString = Base64.encodeToString(byteArr, Base64.NO_WRAP)
            toBase.put("stringPicture", imgString)
            toBase.saveInBackground {
                if (it != null) {
                    it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
                } else {
                    Log.d("MainActivity", "Object saved.")
                }
            }
            finish()
        }
    }

    fun takePhoto(){
        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        res.launch(photoIntent)
    }

    fun camImg(intent: Intent?){
        val imgBitmap = intent?.extras?.get("data") as Bitmap
        imgBitmap.apply {
            binding.photoHolder.setImageBitmap(this)
            // create rounded corners bitmap
            binding.photoHolder.setImageBitmap(toRoundedCorners(8F))

        }

    }
    fun Bitmap.toRoundedCorners(
        cornerRadius: Float = 25F
    ):Bitmap?{
        val bitmap = Bitmap.createBitmap(
            width, // width in pixels
            height, // height in pixels
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // path to draw rounded corners bitmap
        val path = Path().apply {
            addRoundRect(
                RectF(0f,0f,width.toFloat(),height.toFloat()),
                cornerRadius,
                cornerRadius,
                Path.Direction.CCW
            )
        }
        canvas.clipPath(path)

        // draw the rounded corners bitmap on canvas
        canvas.drawBitmap(this,0f,0f,null)
        return bitmap
    }
}