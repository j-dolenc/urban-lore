package ae.urbanlore


import ae.urbanlore.databinding.ActivityMapsBinding
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapsActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private lateinit var map : MapView;

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK);

        val marker = Marker(map)
        val startPoint = GeoPoint(21, 73)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)

        // Doesn't work yet
        /*
        marker.setOnMarkerClickListener(object: Marker.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
                Log.d("DEBUG","click")

                return true
            }

        })
        */




        // Marks the position of the current user and zooms to it
        val mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        val mapController = map.controller
        mMyLocationOverlay.disableMyLocation()
        mMyLocationOverlay.disableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                mapController.animateTo(mMyLocationOverlay.myLocation)
                mapController.setZoom(19.00)
            }
        }
        map.overlays.add(mMyLocationOverlay)





    }

    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private fun requestPermissionsIfNecesarry(permissions: Array<String>) {

        val permissionsToRequest = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(this,
                permissionsToRequest.toArray() as Array<out String>, REQUEST_PERMISSIONS_REQUEST_CODE)
        }

    }

}