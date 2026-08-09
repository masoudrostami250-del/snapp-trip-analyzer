package ir.trip.analyzer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import android.util.Log

class TripAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "آنالیز سفر فعال شد", Toast.LENGTH_LONG).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null) return

        val pkg = event.packageName?.toString() ?: ""

        Log.d("TripAnalyzer", "APP=$pkg TYPE=${event.eventType}")

    if (pkg.lowercase().contains("snapp")) {
        takeScreenshot(
            { result ->
                Toast.makeText(
                    this,
                    "SCREENSHOT OK",
                    Toast.LENGTH_SHORT
                ).show()
            },
            null
        )
    }

        Toast.makeText(
            this,
            "EVENT: $pkg",
            Toast.LENGTH_SHORT
        ).show()

        val root = rootInActiveWindow ?: return

        val text = StringBuilder()
        collectText(root, text)

        if (text.isNotEmpty()) {
            Log.d("TripAnalyzer", "TEXT=$text")

            Toast.makeText(
                this,
                text.toString().take(80),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun collectText(
        node: AccessibilityNodeInfo,
        out: StringBuilder
    ) {
        node.text?.let {
            out.append(it).append(" ")
        }

        node.contentDescription?.let {
            out.append(it).append(" ")
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                collectText(it, out)
            }
        }
    }

    override fun onInterrupt() {
    }
}
