package ae.urbanlore

import ae.urbanlore.databinding.ActivityDetailsBinding
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Base64.getDecoder


class DetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailsBinding
    private var upVoted = false
    private var downVoted = false
    private var votes: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val markerId = intent.getStringExtra("ID").toString()
        val markerInfo = getMarkerInfo(markerId)

        binding.upvote.setOnClickListener{
            upDownVote(markerId,false)
        }
//        binding.downvote.setOnClickListener{
//            upDownVote(markerId,true)
//        }
        binding.back.setOnClickListener{
            finish()
        }
        binding.ar.setOnClickListener {
            val launchIntent = Intent(Intent.ACTION_MAIN);
            launchIntent.setClassName("com.google.ar.core.examples.kotlin.helloar", "com.google.ar.core.examples.kotlin.helloar.HelloArActivity");
            if(launchIntent != null){
                Log.d("SUCCESS", "GHHKJHGJJGJHGJG")
                startActivity(launchIntent)
            }else{
                Log.d("FAILLLLL", "BHGHJKSAHKSJFKAXGHKJL")
                Toast.makeText(this, "AR app not found", Toast.LENGTH_SHORT)
            }
        }

    }

    fun upDownVote(markerId:String, up:Boolean){

        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
        query.whereEqualTo("objectId", markerId);
        query.getFirstInBackground { obj, e ->
            if (e == null) {


                var votes1: Int? = votes

                if (null != votes1) {
                    if(!upVoted){
                        upVoted = true
                        votes1++
                        //binding.upvote.setBackgroundResource(R.drawable.upvote_fill)
                        binding.upvote.setImageResource(R.drawable.upvote_fill)
                    }
                    else{
                        upVoted = false
                        votes1--
                        binding.upvote.setImageResource(R.drawable.upvote)
                    }
//                    if(up){
//
//                        if(!upVoted){
//
//                            if(downVoted){
//                                downVoted = false
//                                votes1+=2
//                            }
//                            else{
//                                votes1 +=1
//                            }
//                            upVoted = true
//                        }
//                        else{
//                            if(upVoted && !downVoted){
//                                votes1 -= 1
//                                upVoted = false
//                            }
//                            else{
//                                Toast.makeText(application, "You cannot upvote again.", Toast.LENGTH_SHORT).show()
//                            }
//
//                        }
//                    }
//                    else{
//                        Log.d("PIZDA",votes.toString())
//                        if(!downVoted){
//                            if(upVoted){
//                                upVoted = false
//                                votes1-=2
//                            }
//                            else{
//                                votes1 -=1
//                            }
//                            downVoted = true
//
//                        }
//                        else{
//                            if(downVoted && !upVoted){
//                                votes1 += 1
//                                downVoted = false
//                            }
//                            else{
//                                Toast.makeText(application, "You cannot downvote again.", Toast.LENGTH_SHORT).show()
//                            }
//
//                        }
//                    }


                    val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
                    query.whereEqualTo("objectId", markerId);
                    query.getFirstInBackground() { obje, e ->
                        if(null != obje){
                            obje.put("upvoteNumber", votes1);
                            obje.saveInBackground()
                            binding.votes.text = votes1.toString()
                            votes = votes1

                        }else{

                        }

                    }
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
                val imgString: String? = obj?.getString("stringPicture")
                votes = obj?.getInt("upvoteNumber")
                if (long != null && lat != null && title != null && description != null && imgString != null && null != votes) {

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
                    val decoded = getDecoder().decode(res["imgString"].toString())
                    val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
                    img.apply {
                        binding.image.setImageBitmap(this)
                        // create rounded corners bitmap
                        binding.image.setImageBitmap(toRoundedCorners(8F))

                    }
                    binding.votes.text=votes.toString()
                }

            }

        }
        return res
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