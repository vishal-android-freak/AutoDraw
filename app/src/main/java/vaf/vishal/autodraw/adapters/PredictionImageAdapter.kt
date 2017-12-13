package vaf.vishal.autodraw.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.prediction_images.view.*
import org.json.JSONArray
import org.json.JSONObject
import vaf.vishal.autodraw.R

/**
 * Created by vishal on 13/12/17.
 */

class PredictionImageAdapter(val list: ArrayList<JSONObject>): RecyclerView.Adapter<PredictionImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.prediction_images, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        init {
            val requestBuilder = Glide.with(itemView.context)
                    .usin
        }

        fun bindData(predictionObject: JSONObject) {
           itemView.predictionPic
        }
    }
}