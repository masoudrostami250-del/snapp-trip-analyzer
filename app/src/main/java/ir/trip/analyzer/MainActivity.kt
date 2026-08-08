package ir.trip.analyzer
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.widget.*
import android.view.Gravity
class MainActivity:Activity(){
 override fun onCreate(b:Bundle?){super.onCreate(b)
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(32,48,32,48)}
  box.addView(TextView(this).apply{text="آنالیز سفر\nنسخه آزمایشی";textSize=24f;gravity=Gravity.CENTER})
  box.addView(TextView(this).apply{text="\nحداقل مبلغ: ۵۵٬۰۰۰ تومان\nحداقل درآمد: ۲۰٬۰۰۰ تومان/کیلومتر\n\nAccessibility را فعال کنید.";textSize=16f;gravity=Gravity.CENTER})
  box.addView(Button(this).apply{text="باز کردن تنظیمات Accessibility";setOnClickListener{startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))}})
  setContentView(box)
 }
}