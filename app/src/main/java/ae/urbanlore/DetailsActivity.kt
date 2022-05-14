package ae.urbanlore

import ae.urbanlore.databinding.ActivityDetailsBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
//    fun getMarkerInfo(markerId:String):MutableMap<String,String>{
//        val res = mutableMapOf("title" to "test", "description" to "test", "imgString" to "test")
//        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
//        query.whereEqualTo("objectId", markerId);
//        query.getFirstInBackground { obj, e ->
//            if (e == null) {
//
//                val long: String? = obj?.getString("longitude")
//                val lat: String? = obj?.getString("latitude")
//                val title: String? = obj?.getString("name")
//                val description: String? = obj?.getString("description")
//                val imgString: String? = obj?.getString("stringPicture");
//
//                if (long!=null && lat != null && title != null && description != null && imgString != null){
//                    //TODO: naredi kar hoces z info o enem eventu
//                    res["title"] = title
//                    res["description"] = description
//                    res["imgString"] = imgString
//                    //val decoded = Base64.getDecoder().decode(imgString)
//                    //val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
//                    //itemImage.setImageBitmap(img)
//
//                }
//
//            }
//
//            myArray = dataList.toTypedArray()
//
//
//            Log.d("DEBUG",myArray.toString())
//
//        }
//        return res
//    }

}