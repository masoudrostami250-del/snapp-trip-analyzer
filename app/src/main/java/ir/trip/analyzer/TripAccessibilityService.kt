package ir.trip.analyzer

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

class TripAccessibilityService : AccessibilityService() {

    private var shown = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "آنالیز سفر: سرویس فعال شد", Toast.LENGTH_LONG).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!shown) {
            shown = true
            Toast.makeText(this, "آنالیز سفر: صفحه اسنپ دریافت شد", Toast.LENGTH_LONG).show()
        }
    }

    override fun onInterrupt() {
    }
}
