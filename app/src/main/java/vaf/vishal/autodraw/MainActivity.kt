package vaf.vishal.autodraw

import android.graphics.Path
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import vaf.vishal.autodraw.interfaces.OnDrawEventListener

class MainActivity : AppCompatActivity() {

    lateinit var stencilData: JSONObject
    val dataArray = ArrayList<ArrayList<ArrayList<Int>>>()
    var time = 0L
    lateinit var xPointsArray: ArrayList<Int>
    lateinit var yPointsArray: ArrayList<Int>
    lateinit var timeDiffArray: ArrayList<Int>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getStencilData()

        suggestionsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        autodraw.setOnDrawEventListener(object: OnDrawEventListener {

            override fun onDrawStart() {
                time = System.currentTimeMillis()
                xPointsArray = ArrayList()
                yPointsArray = ArrayList()
                timeDiffArray = ArrayList()
            }

            override fun onDrawStop(path: Path) {
                val dataPointsArrayList = ArrayList<ArrayList<Int>>()
                dataPointsArrayList.add(xPointsArray)
                dataPointsArrayList.add(yPointsArray)
                dataPointsArrayList.add(timeDiffArray)
                dataArray.add(dataPointsArrayList)

                getSuggestions(autodraw.width, autodraw.height, dataArray)
            }

            override fun onDrawing(pointX: Int, pointY: Int) {
                xPointsArray.add(pointX)
                yPointsArray.add(pointY)
                timeDiffArray.add((System.currentTimeMillis() - time).toInt())
            }

        })
    }

    private fun getSuggestions(canvasWidth: Int, canvasHeight: Int, dataArray: ArrayList<ArrayList<ArrayList<Int>>>) {
        val body = JSONObject()
        body.put("input_type", 0)
        val requestBody = JSONArray()
        val requestObject = JSONObject()
        requestObject.put("language", "autodraw")
        requestObject.put("ink", JSONArray(dataArray))
        val writingGuideObject = JSONObject()
        writingGuideObject.put("width", canvasWidth)
        writingGuideObject.put("height", canvasHeight)
        requestObject.put("writing_guide", writingGuideObject)
        requestBody.put(0, requestObject)
        body.put("requests", requestBody)

        AndroidNetworking.post("https://inputtools.google.com/request?ime=handwriting&app=autodraw&dbg=1&cs=1&oe=UTF-8")
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(body)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(object: JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        val predictions = ((response[1] as JSONArray)[0] as JSONArray)[1]
                    }

                    override fun onError(anError: ANError?) {

                    }

                })
    }

    private fun getStencilData() {
        AndroidNetworking.get("https://www.autodraw.com/assets/stencils.json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object: JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        stencilData = response
                    }

                    override fun onError(anError: ANError?) {
                        anError?.printStackTrace()
                    }

                })
    }
}
