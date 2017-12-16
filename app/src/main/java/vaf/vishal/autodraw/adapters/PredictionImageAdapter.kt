package vaf.vishal.autodraw.adapters

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.TransitionOptions
import com.caverock.androidsvg.SVG
import kotlinx.android.synthetic.main.prediction_images.view.*
import org.json.JSONArray
import org.json.JSONObject
import vaf.vishal.autodraw.R
import vaf.vishal.autodraw.interfaces.OnPredictionSelectListener
import vaf.vishal.autodraw.utils.GlideApp
import vaf.vishal.autodraw.utils.SvgSoftwareLayerSetter
import java.io.InputStream

/**
 * Created by vishal on 13/12/17.
 */

class PredictionImageAdapter(val list: ArrayList<JSONObject>) : RecyclerView.Adapter<PredictionImageAdapter.ViewHolder>() {

    lateinit var predictionSelect: OnPredictionSelectListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.prediction_images, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list.get(position), predictionSelect)
    }

    fun setOnPredictionSelectListener(onPredictionSelectListener: OnPredictionSelectListener) {
        predictionSelect = onPredictionSelectListener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var requestBuilder: RequestBuilder<PictureDrawable> = GlideApp.with(itemView.context)
                .`as`(PictureDrawable::class.java)
                .listener(SvgSoftwareLayerSetter())

        fun bindData(predictionObject: JSONObject, onPredictionSelectListener: OnPredictionSelectListener) {
            val imageUrl = predictionObject.optString("src")
            requestBuilder.load(Uri.parse(imageUrl)).into(itemView.predictionPic)
            itemView.predictionPic.setOnClickListener {
                onPredictionSelectListener.onSelect(imageUrl)
            }
        }
    }
}