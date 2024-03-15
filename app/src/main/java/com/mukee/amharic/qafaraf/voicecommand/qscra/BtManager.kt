
package com.mukee.amharic.qafaraf.voicecommand.qscra

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.util.Log
import com.mukee.amharic.qafaraf.voicecommand.qscra.fragments.AudioFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * Bluetooth Manager class responsible for managing Bluetooth connections and data transfer.
 */
class BtManager {

    /**
     * Thread for creating and connecting Bluetooth sockets.
     */
    @DelicateCoroutinesApi
    @SuppressLint("MissingPermission")
    class CreateConnectThread(private val context: Context, private val address: String?) : Thread() {
        private val bluetoothAdapter: BluetoothAdapter? by lazy {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }
        private val bluetoothDevice: BluetoothDevice by lazy {
            bluetoothAdapter?.getRemoteDevice(address) ?: throw IllegalArgumentException("Bluetooth adapter or address is null.")
        }
        private var bluetoothSocket: BluetoothSocket? = null

        override fun run() {
            try {
                val uuid =
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPortService ID
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                Log.e("Status", "Device connected")
                handler?.obtainMessage(AudioFragment.CONNECTING_STATUS, 1, -1)?.sendToTarget()
            } catch (e: IOException) {
                handleConnectionFailure(e)
            }

            AudioFragment.connectedThread = ConnectedThread(bluetoothSocket)
            AudioFragment.connectedThread?.start()
        }

        /**
         * Cancels the connection and releases resources.
         */
        fun cancel() {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }

        private fun handleConnectionFailure(e: IOException) {
            try {
                bluetoothSocket?.close()
                Log.e("Status", "Cannot connect to device", e)
                handler?.obtainMessage(AudioFragment.CONNECTING_STATUS, -1, -1)?.sendToTarget()
            } catch (closeException: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", closeException)
            }
        }
    }

    /**
     * Thread for managing data transfer over Bluetooth.
     */
    @DelicateCoroutinesApi
    class ConnectedThread(private val bluetoothSocket: BluetoothSocket?) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            try {
                tmpIn = bluetoothSocket?.inputStream
                tmpOut = bluetoothSocket?.outputStream
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error getting input/output stream", e)
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes = 0
            while (true) {
                try {
                    buffer[bytes] = mmInStream?.read()?.toByte() ?: break
                    var readMessage: String
                    if (buffer[bytes] == '\n'.code.toByte()) {
                        readMessage = String(buffer, 0, bytes)
                        Log.e("Arduino Message", readMessage)
                        handler?.obtainMessage(AudioFragment.MESSAGE_READ, readMessage)?.sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Error reading from input stream", e)
                    break
                }
            }
        }

        /**
         * Writes data to the Bluetooth socket.
         *
         * @param input The data to write.
         */
        fun write(input: String?) {
            val bytes = input?.toByteArray()
            try {
                mmOutStream?.write(bytes)
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        /**
         * Cancels the connection and releases resources.
         */
        fun cancel() {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Error closing socket", e)
            }
        }



    }



    companion object {
        // Handler for communicating with the UI thread
        var handler: Handler? = null
    }
}
