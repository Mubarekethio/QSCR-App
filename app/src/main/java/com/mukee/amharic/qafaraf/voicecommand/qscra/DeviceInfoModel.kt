package com.mukee.amharic.qafaraf.voicecommand.qscra


class DeviceInfoModel(private var deviceName: String?,
                      private var deviceHardwareAddress: String?) {


    fun getDeviceName(): String? {
        return deviceName
    }

    fun getDeviceHardwareAddress(): String? {
        return deviceHardwareAddress
    }




}

