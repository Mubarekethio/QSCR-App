package com.mukee.amharic.qafaraf.voicecommand.qscra.ui


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mukee.amharic.qafaraf.voicecommand.qscra.DeviceInfoModel
import com.mukee.amharic.qafaraf.voicecommand.qscra.MainActivity
import com.mukee.amharic.qafaraf.voicecommand.qscra.R
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class DeviceListAdapter(private val context: Context, private val deviceList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textName: TextView
        var textAddress: TextView
        var linearLayout: LinearLayout

        init {
            textName = v.findViewById(R.id.textViewDeviceName)
            textAddress = v.findViewById(R.id.textViewDeviceAddress)
            linearLayout = v.findViewById(R.id.linearLayoutDeviceInfo)
        }
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemHolder = holder as ViewHolder
        val deviceInfoModel = deviceList[position] as DeviceInfoModel
        itemHolder.textName.text = deviceInfoModel.getDeviceName()
        itemHolder.textAddress.text = deviceInfoModel.getDeviceHardwareAddress()



        // When a device is selected
        itemHolder.linearLayout.setOnClickListener {

            val intent = Intent(context, MainActivity::class.java)

            // Send device details to the MainActivity
            //findNavController().navigate(R.id.audioFragment)
            intent.putExtra("deviceName", deviceInfoModel.getDeviceName())
            intent.putExtra("deviceAddress", deviceInfoModel.getDeviceHardwareAddress())
            context.startActivity(intent)
            //findNavController().navigate(R.id.audioFragment)//navigate(R.id.audioFragment)

        }
    }



    override fun getItemCount(): Int {
        return deviceList.size
    }


}
