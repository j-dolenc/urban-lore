package ae.urbanlore

import ae.urbanlore.databinding.ActivityAddLoreBinding
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
        binding.photoHolder.setImageBitmap(imgBitmap)
    }
}