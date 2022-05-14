package ae.urbanlore

import ae.urbanlore.databinding.ActivityAddLoreBinding
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import com.parse.ParseObject

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