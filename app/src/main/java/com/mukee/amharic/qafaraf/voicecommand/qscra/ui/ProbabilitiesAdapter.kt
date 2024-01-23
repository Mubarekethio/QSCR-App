/*
  Copyright 2023 Mukee. All Rights Reserved.
    ~ Name:- Mubarek Kebede
    ~ Email:- mubare.k1449@gmail.com
    ~ Ph.Number:- +251929920355/+251914070683
    ~ Github:- https://github.com/Mubarekethio
    ~ Linkedin:- https://www.linkedin.com/in/mubarek-kebede-582012148/
 */

package com.mukee.amharic.qafaraf.voicecommand.qscra.ui



import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import com.mukee.amharic.qafaraf.voicecommand.qscra.databinding.ItemProbabilityBinding
import org.tensorflow.lite.support.label.Category


//private val context: Context, private val deviceList: List<Any>
internal class ProbabilitiesAdapter : RecyclerView.Adapter<ProbabilitiesAdapter.ViewHolder>() {
    var categoryList: List<Category> = emptyList()


    //DeviceInfoLayoutBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProbabilityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category.label, category.score, category.index)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(private val binding: ItemProbabilityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var primaryProgressColorList: IntArray = binding.root.resources.getIntArray((R.array.colors_progress_primary))
        private var backgroundProgressColorList: IntArray = binding.root.resources.getIntArray((R.array.colors_progress_background))

        @SuppressLint("SetTextI18n")
        fun bind(label: String, score: Float, index: Int) {
            with(binding) {


                labelTextView.text = label

                //println(label)
                progressBar.progressBackgroundTintList =
                    ColorStateList.valueOf(
                        backgroundProgressColorList[index % backgroundProgressColorList.size])

                progressBar.progressTintList =
                    ColorStateList.valueOf(
                        primaryProgressColorList[index % primaryProgressColorList.size])

                val newValue = (score * 100).toInt()

                progressBar.progress = newValue


            }
        }


    }
}
