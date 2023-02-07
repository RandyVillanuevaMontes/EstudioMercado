package com.example.poligonos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.poligonos.databinding.MapWhitoutConectionBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.FileInputStream

class MapWithOutConection : AppCompatActivity(){

    companion object{
        val CA = LatLong(29.3034,-101.0004)
    }

    private lateinit var b: MapWhitoutConectionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidGraphicFactory.createInstance(application)

        b = MapWhitoutConectionBinding.inflate(layoutInflater)
        setContentView(b.root)

        val contract = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            //openMap(result?.data?.data!!)
            result?.data?.data?.let{uri->
                openMap(uri)
            }
        }
        b.openMap.setOnClickListener {
            contract.launch(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply{
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            )
        }
    }

    fun openMap(uri: Uri){
        b.map.mapScaleBar.isVisible = true
        b.map.setBuiltInZoomControls(true)
        val cache = AndroidUtil.createTileCache(
            this,
            "mycache",
            b.map.model.displayModel.tileSize,
            1f,
            b.map.model.frameBufferModel.overdrawFactor
        )

        val stream = contentResolver.openInputStream(uri) as FileInputStream

        val mapStore = MapFile(stream)
        val renderLayer = TileRendererLayer(
            cache,
            mapStore,
            b.map.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(
            InternalRenderTheme.DEFAULT
        )

        b.map.layerManager.layers.add(renderLayer)

        b.map.setCenter(CA)
        b.map.setZoomLevel(12)
    }
}