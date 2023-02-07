package com.example.poligonos

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.RequestQueue
import com.example.poligonos.Utility.NetworkChangerListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.GsonBuilder
import java.util.ArrayList


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    var supportMapFragment: SupportMapFragment? = null
    var gMap: GoogleMap? = null
    var clear: Button? = null
    var save: Button? = null
    var polygon: Polygon? = null
    var latlngList: ArrayList<LatLng>? = ArrayList()
    var markerList = ArrayList<Marker?>()
    var prettyGson = GsonBuilder().setPrettyPrinting().create()
    private val queue: RequestQueue? = null
    var networkChangerListener = NetworkChangerListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poligonos)

        screenSplash.setKeepOnScreenCondition {false}
//        val mapa = Intent(this, Poligonos::class.java)
//        mapa.action

        supportMapFragment =
            supportFragmentManager!!.findFragmentById(R.id.google_map) as SupportMapFragment?
        assert(supportMapFragment != null)
        supportMapFragment!!.getMapAsync(this)

        clear = findViewById(R.id.btn_clear_polygon)
        save = findViewById(R.id.btn_save_json)

        clear?.setOnClickListener {
            if (polygon != null) {
                polygon!!.remove()
            }
            for (marker in markerList) marker!!.remove()
            latlngList!!.clear()
            markerList!!.clear()
        }

        save?.setOnClickListener{
            if (latlngList != null) {
                val primerCoor = latlngList!![0]
                latlngList!!.add(primerCoor)

                val prettyJson = prettyGson.toJson(latlngList)
                println(prettyJson)
                polygon!!.remove()

                for (marker in markerList) marker!!.remove()
                latlngList!!.clear()
                markerList!!.clear()
            }
            SweetAlertDialog(this).setTitleText("Predio Guardado").show()
        }
//        queue = Volley.newRequestQueue(this);
    }

    //    private void metodoPost(){
    //        String url = "Aqui va la URL";
    //
    //        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
    //            @Override
    //            public void onResponse(JSONObject response) {
    //                try {
    //                    JSONArray mJsonArray = response.getJSONArray(String.valueOf(prettyGson));
    //
    //                    Toast.makeText(MainActivity2.this, "Predio Guardado: " + mJsonArray, Toast.LENGTH_LONG).show();
    //
    //                } catch (JSONException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }, new Response.ErrorListener() {
    //            @Override
    //            public void onErrorResponse(VolleyError error) {
    //
    //            }
    //        });
    //
    //        queue.add(request);
    //    }


    override fun onMapReady(googleMap: GoogleMap) {
        val acuña = LatLng(29.32322, -100.95217)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(acuña, 14f),
            4000,
            null
        )

        gMap = googleMap
        gMap!!.setOnMapLongClickListener { latLng: LatLng ->
            val markerOptions = MarkerOptions().position(latLng)
            val marker = gMap!!.addMarker(markerOptions)
            latlngList!!.add(latLng)
            markerList.add(marker)
            if (polygon != null) {
                polygon!!.remove()
            }

            val polygonOptions = PolygonOptions().addAll(latlngList!!).clickable(true)
            polygon = gMap!!.addPolygon(polygonOptions)
            polygon!!.fillColor = Color.RED
            polygon!!.strokeColor = Color.BLACK
        }

    }

    override fun onStart() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangerListener, filter)
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(networkChangerListener)
        super.onStop()
    }

}

