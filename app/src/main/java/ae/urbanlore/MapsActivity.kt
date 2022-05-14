package ae.urbanlore

import ae.urbanlore.databinding.ActivityMapsBinding
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseObject
import com.parse.ParseQuery
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latG = ""
    var longG = ""
    var dataList = ArrayList<String>()
    var myArray = arrayOf<String>()

    companion object{
        private const val REQUEST_ID_LOCATION_PERMISSIONS = 8
        private const val MSG_UPDATE_TIME = 1
        private const val UPDATE_RATE_MS = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        binding.add.setOnClickListener{
            val intent = Intent(applicationContext, AddLoreActivity::class.java)
            intent.putExtra("LAT", latG)
            intent.putExtra("LONG", longG)
            startActivity(intent)
        }

        val locationRequest = LocationRequest()
        locationRequest.interval = 10
        locationRequest.fastestInterval = 15 * 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        //locationUpdateHandler.sendEmptyMessage(MSG_UPDATE_TIME)
    }

    fun showLastKnownLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            Log.d("Debug", "What ios going on here")
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale (this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("Debug", "snekbar")
                Snackbar.make(
                    binding.root,
                    R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok) {
                    ActivityCompat.requestPermissions(
                        this@MapsActivity, arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        REQUEST_ID_LOCATION_PERMISSIONS
                    )
                }.show()
            } else{
                ActivityCompat.requestPermissions(
                    this@MapsActivity, arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_ID_LOCATION_PERMISSIONS
                )
            }
        }else{

            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location != null){
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(location.latitude, location.longitude))
                            .title(
                                LocalDateTime.now().format(
                                    DateTimeFormatter.ofLocalizedDateTime(
                                        FormatStyle.SHORT, FormatStyle.SHORT)))
                    )
                    val zoom_level = 15.0F
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), zoom_level))
                    val long = "Longitude" + location.longitude.toString()
                    val lat = "Latitude: " + location.latitude.toString()
                    longG = location.longitude.toString()
                    latG = location.latitude.toString()
                    Log.d("DEBUG", "NEW LOCATION: " + long + " " + lat)
                    /*binding.longitude.text = long
                    binding.latitude.text = lat*/
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            showLastKnownLocation()
        }
    }

    private val locationUpdateHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if(MSG_UPDATE_TIME == msg.what){
                Log.d("HANDLER", "Updating location...")

                showLastKnownLocation()

                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS)
            }
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        addMarkersFromDB()
        //val decoded = Base64.getDecoder().decode(memos[position].imgView)
        //val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
        //itemImage.setImageBitmap(img)
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
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

                if (long!=null && lat != null && title != null && description != null && imgString != null){
                    //TODO: naredi kar hoces z info o enem eventu
                    res["title"] = title
                    res["description"] = description
                    res["imgString"] = imgString
                    //val decoded = Base64.getDecoder().decode(imgString)
                    //val img = BitmapFactory.decodeByteArray( decoded,0, decoded.size)
                    //itemImage.setImageBitmap(img)

                }

            }

            myArray = dataList.toTypedArray()


            Log.d("DEBUG",myArray.toString())

        }
        return res
    }

    fun decodeImg(stringImg: String): Bitmap {
        val decoded = Base64.getDecoder().decode(stringImg)

        return BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    }

    fun addMarkersFromDB(){

        val query: ParseQuery<ParseObject> = ParseQuery.getQuery("lore")
        query.findInBackground { objects, e ->
            if (e == null) {
                for (obj in objects) {

                    val long: String? = obj?.getString("longitude")
                    val lat: String? = obj?.getString("latitude")
                    val title: String? = obj?.getString("name")
                    val markId: String? = obj?.getString("objectId")

                    if (long!=null && lat != null){
                        val sydney = LatLng(lat.toDouble(), long.toDouble())
                        mMap.addMarker(MarkerOptions().position(sydney).title(title))?.snippet =
                            markId
                        //dataList.add(element)
                    }
                }
            }

            myArray = dataList.toTypedArray()


            Log.d("DEBUG",myArray.toString())

        }
    }

    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }
}