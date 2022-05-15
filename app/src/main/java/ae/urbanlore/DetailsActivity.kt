package ae.urbanlore

import ae.urbanlore.databinding.ActivityDetailsBinding
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.*
import java.util.Base64.getDecoder

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val markerId = intent.getStringExtra("ID").toString()
        val markerInfo = getMarkerInfo(markerId)

        binding.downvote.setOnClickListener{

        }
        binding.downvote.setOnClickListener{

        }

    }

    fun upDownVote(markerId:String, up:Boolean){
        val res = mutableMapOf("title" to "test", "description" to "test", "imgString" to "test")
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
        query.whereEqualTo("objectId", markerId);
        query.getFirstInBackground { obj, e ->
            if (e == null) {

                val long: String? = obj?.getString("longitude")
                val lat: String? = obj?.getString("latitude")
                val title: String? = obj?.getString("name")
                val description: String? = obj?.getString("description")
                val imgString: String? = obj?.getString("stringPicture");

                if (long != null && lat != null && title != null && description != null && imgString != null) {

                    res["title"] = title
                    res["description"] = description
                    res["imgString"] = imgString
                    //val decoded = Base64.getDecoder().decode(imgString)
                    //val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
                    //itemImage.setImageBitmap(img)

                }

            }

        }
    }


    fun getMarkerInfo(markerId:String):MutableMap<String,String>{
        val res = mutableMapOf("title" to "test", "description" to "test", "imgString" to "test")
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
        query.whereEqualTo("objectId", markerId);
        query.getFirstInBackground { obj, e ->
            if (e == null) {

                val long: String? = obj?.getString("longitude")
                val lat: String? = obj?.getString("latitude")
                val title: String? = obj?.getString("name")
                val description: String? = obj?.getString("description")
                val imgString: String? = obj?.getString("stringPicture");

                if (long != null && lat != null && title != null && description != null && imgString != null) {

                    res["title"] = title
                    res["description"] = description
                    res["imgString"] = imgString
                    //val decoded = Base64.getDecoder().decode(imgString)
                    //val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
                    //itemImage.setImageBitmap(img)

                    Log.d("PIZDA",res["title"].toString())
                    Log.d("PIZDA",res["description"].toString())
                    Log.d("PIZDA",res["imgString"].toString())
                    binding.title.text = res["title"].toString()
                    binding.description.text = res["description"].toString()
                    val decoded = Base64.getDecoder().decode(res["imgString"].toString())
                    val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
                    binding.image.setImageBitmap(img)
                }

            }

        }
        return res
    }

}