package ir.trip.analyzer

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.util.Locale

class TripAccessibilityService : AccessibilityService() {

    private var last = ""

    private val minFare = 55000.0
    private val minPerKm = 20000.0

    private val handler = Handler(Looper.getMainLooper())

    private val scanner = object : Runnable {
        override fun run() {
            analyzeCurrentScreen()
            handler.postDelayed(this, 700)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        serviceInfo = serviceInfo.apply {
            eventTypes =
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED or
                AccessibilityEvent.TYPE_VIEW_SCROLLED or
                AccessibilityEvent.TYPE_VIEW_CLICKED
        }

        handler.post(scanner)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        analyzeCurrentScreen()
    }

    private fun analyzeCurrentScreen() {
        val root = rootInActiveWindow ?: return

        val out = StringBuilder()
        collect(root, out)

        val text = out.toString()

        val fare = findFare(text)
        val distances = findDistances(text)

        if (fare == null || distances.isEmpty()) return

        val km = distances.sum()

        if (km <= 0) return

        val rate = fare / km
        val good = fare >= minFare && rate >= minPerKm

        val sig = "$fare|$km|$good"

        if (sig == last) return

        last = sig

        val message =
            "سفر ${if (good) "مناسب" else "نامناسب"}\n" +
            "مبلغ: ${fare.toInt()} تومان\n" +
            "مسافت: ${String.format(Locale.US, "%.1f", km)} کیلومتر\n" +
            "درآمد/کیلومتر: ${rate.toInt()} تومان"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun collect(
        node: AccessibilityNodeInfo?,
        out: StringBuilder
    ) {
        if (node == null) return

        node.text?.let {
            out.append(it).append(" ")
        }

        node.contentDescription?.let {
            out.append(it).append(" ")
        }

        for (i in 0 until node.childCount) {
            collect(node.getChild(i), out)
        }
    }

    private fun findFare(text: String): Double? {
        val normalized = digits(text)
            .replace(",", "")
            .replace("٬", "")
            .replace("،", "")

        val regex = Regex(
            "(?<!\\d)(\\d{2,7})\\s*تومان"
        )

        return regex
            .findAll(normalized)
            .map { it.groupValues[1].toDouble() }
            .maxOrNull()
    }

    private fun findDistances(text: String): List<Double> {
        val normalized = digits(text)
            .replace("٬", "")
            .replace("،", "")

        val regex = Regex(
            "(\\d+(?:[.,]\\d+)?)\\s*(?:کیلومتر|km)"
        )

        return regex
            .findAll(normalized)
            .mapNotNull {
                it.groupValues[1]
                    .replace(",", ".")
                    .toDoubleOrNull()
            }
            .filter { it > 0 }
            .toList()
    }

    private fun digits(text: String): String {
        val persian = "۰۱۲۳۴۵۶۷۸۹"
        val arabic = "٠١٢٣٤٥٦٧٨٩"

        var result = text

        persian.forEachIndexed { index, char ->
            result = result.replace(
                char,
                ('0'.code + index).toChar()
            )
        }

        arabic.forEachIndexed { index, char ->
            result = result.replace(
                char,
                ('0'.code + index).toChar()
            )
        }

        return result
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        handler.removeCallbacks(scanner)
        super.onDestroy()
    }
}
