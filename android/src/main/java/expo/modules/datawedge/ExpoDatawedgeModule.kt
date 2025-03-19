DataWedge modificações para fazer funcionar

package expo.modules.datawedge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import androidx.core.content.ContextCompat
import  android.os.Build

class ExpoDatawedgeModule : Module() {

    private var myBroadcastReceiver: BroadcastReceiver? = null

    override fun definition() = ModuleDefinition {
        Name("ExpoDatawedgeModule")

        OnCreate {
            registerReceiver()
        }

        OnDestroy {
            unregisterReceiver()
        }

        // Function to start listening for broadcasts
        Function("startListening") {
            registerReceiver()
        }

        // Function to stop listening for broadcasts
        Function("stopListening") {
            unregisterReceiver()
        }

        // Declare events that can be sent to JavaScript
        Events("onScanReceived")
    }

    private fun registerReceiver() {
        if (myBroadcastReceiver != null) return

        val context = appContext.reactContext ?: return
        val filter = IntentFilter().apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addAction("us.domkalan.expo-datawedge.ACTION") // Update with your action name
        }

        myBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                val extras = intent.extras

                if (action == "us.domkalan.expo-datawedge.ACTION" && extras != null) {
                    // Extract the scanned data from the intent
                    val decodedData = extras.getString("com.symbol.datawedge.data_string") ?: "No data"
                    val decodedLabel = extras.getString("com.symbol.datawedge.label_type") ?: "No data"
                    val decodedSource = extras.getString("com.symbol.datawedge.source") ?: "No data"

                    // Emit the event to JavaScript
                    sendEvent("onScanReceived", mapOf(
                        "data" to decodedData,
                        "labelType" to decodedLabel,
                        "source" to decodedSource
                    ))
                }
            }
        }
    if (Build.VERSION.SDK_INT >= 34 && context.getApplicationInfo().targetSdkVersion >= 34) {
        context.registerReceiver(myBroadcastReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        }else{
            context.registerReceiver(myBroadcastReceiver, filter)
        }

        
    }

    private fun unregisterReceiver() {
        val context = appContext.reactContext ?: return
        if (myBroadcastReceiver != null) {
            context.unregisterReceiver(myBroadcastReceiver)
            myBroadcastReceiver = null
        }
    }
}
