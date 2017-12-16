package vaf.vishal.autodraw

import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.bumptech.glide.RequestBuilder
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import vaf.vishal.autodraw.adapters.PredictionImageAdapter
import vaf.vishal.autodraw.interfaces.OnActionsCountChangeListener
import vaf.vishal.autodraw.interfaces.OnDrawEventListener
import vaf.vishal.autodraw.interfaces.OnPredictionSelectListener
import vaf.vishal.autodraw.utils.GlideApp
import vaf.vishal.autodraw.utils.SvgSoftwareLayerSetter

class MainActivity : AppCompatActivity() {

    lateinit var stencilData: JSONObject
    val dataArray = ArrayList<ArrayList<ArrayList<Int>>>()
    var time = 0L
    lateinit var xPointsArray: ArrayList<Int>
    lateinit var yPointsArray: ArrayList<Int>
    lateinit var timeDiffArray: ArrayList<Int>
    lateinit var requestBuilder: RequestBuilder<PictureDrawable>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getStencilData()

        requestBuilder = GlideApp.with(this)
                .`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())

        suggestionsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        placeholderText.text = "Start doodling!"

        undoBtn.setOnClickListener {
            autodraw.undo()
        }
        clearBtn.setOnClickListener {
            autodraw.clear()
        }

        autodraw.setOnDrawEventListener(object : OnDrawEventListener {

            override fun onDrawStart() {
                time = System.currentTimeMillis()
                xPointsArray = ArrayList()
                yPointsArray = ArrayList()
                timeDiffArray = ArrayList()
                placeholderCard.visibility = View.GONE
                undoBtn.visibility = View.GONE
                clearBtn.visibility = View.GONE
            }

            override fun onDrawStop() {
                val dataPointsArrayList = ArrayList<ArrayList<Int>>()
                dataPointsArrayList.add(xPointsArray)
                dataPointsArrayList.add(yPointsArray)
                dataPointsArrayList.add(timeDiffArray)
                dataArray.add(dataPointsArrayList)

                getSuggestions(autodraw.width, autodraw.height, dataArray)
                placeholderCard.visibility = View.VISIBLE
                undoBtn.visibility = View.VISIBLE
                clearBtn.visibility = View.VISIBLE
            }

            override fun onDrawing(pointX: Int, pointY: Int) {
                xPointsArray.add(pointX)
                yPointsArray.add(pointY)
                timeDiffArray.add((System.currentTimeMillis() - time).toInt())
            }

        })
        autodraw.setOnActionsCountChangeListener(object: OnActionsCountChangeListener {
            override fun onUndoCountChange(undoCount: Int) {
                if (undoCount > 0) {
                    undoBtn.visibility = View.VISIBLE
                    clearBtn.visibility = View.VISIBLE
                    suggestionsRecycler.visibility = View.VISIBLE
                } else {
                    undoBtn.visibility = View.GONE
                    clearBtn.visibility = View.GONE
                    placeholderText.text = "Start doodling!"
                    suggestionsRecycler.visibility = View.GONE
                }
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

        val adapterData = ArrayList<JSONObject>()

        AndroidNetworking.post("https://inputtools.google.com/request?ime=handwriting&app=autodraw&dbg=1&cs=1&oe=UTF-8")
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(body)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        val predictions = ((response[1] as JSONArray)[0] as JSONArray)[1] as JSONArray
                        for (i in 0 until predictions.length()) {
                            Log.d("array", predictions.optString(i))
                            val predictionArray = stencilData.optJSONArray(predictions.optString(i))
                            if (predictionArray !== null)
                                (0 until predictionArray.length()).mapTo(adapterData) { predictionArray.optJSONObject(it) }
                        }
                        placeholderText.text = "Did you mean:"
                        val adapter = PredictionImageAdapter(adapterData)
                        adapter.setOnPredictionSelectListener(object: OnPredictionSelectListener {
                            override fun onSelect(imageUrl: String) {
                                autodraw.clear()
                                requestBuilder.load(Uri.parse(imageUrl)).into(autodraw)
                            }

                        })
                        suggestionsRecycler.adapter = adapter
                    }

                    override fun onError(anError: ANError?) {

                    }

                })
    }

    private fun getStencilData() {
        AndroidNetworking.get("https://www.autodraw.com/assets/stencils.json")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        stencilData = response
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(anError: ANError?) {
                        anError?.printStackTrace()
                    }

                })
    }
}
