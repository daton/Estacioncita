package com.unitec.estacioncita

import android.content.Context
import android.graphics.Bitmap
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.util.LruCache
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var url="https://benesuela.herokuapp.com/api/usuario"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->

   //La temperatura
           texto.text=     response.get("nombre").toString()
            },
            Response.ErrorListener { error ->
                // TODO: Handle error
                texto.text = "Hubo un error, ${error}"
            }
        )

        // Acceso al request por medio de una clase Singleton
        MiSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)


    }



}


class MiSingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MiSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MiSingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    //Para el caso d cargar un objeto como una imagen.
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext es para evitar fuga de mmoria

        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}