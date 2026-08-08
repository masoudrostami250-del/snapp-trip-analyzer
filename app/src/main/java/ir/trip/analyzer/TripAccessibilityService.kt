package ir.trip.analyzer
import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import java.util.Locale
class TripAccessibilityService:AccessibilityService(){
 private var last=""
 private val minFare=55000.0
 private val minPerKm=20000.0
 override fun onAccessibilityEvent(e:AccessibilityEvent?){
  if(e==null)return
  val root=rootInActiveWindow?:return
  val out=StringBuilder();collect(root,out);val text=out.toString()
  val fare=findFare(text);val ds=findDistances(text)
  if(fare!=null&&ds.isNotEmpty()){
   val km=ds.sum();val rate=fare/km;val good=fare>=minFare&&rate>=minPerKm
   val sig="$fare|$km|$good"
   if(sig!=last){last=sig
    Toast.makeText(this,"سفر ${if(good)"مناسب" else "نامناسب"}\nمبلغ: ${fare.toInt()} تومان\nمسافت: %.1f کیلومتر\nدرآمد/کیلومتر: ${rate.toInt()}".format(Locale.US,km),Toast.LENGTH_SHORT).show()
   }
  }
 }
 private fun collect(n:AccessibilityNodeInfo?,o:StringBuilder){if(n==null)return;n.text?.let{o.append(it).append(" ")};n.contentDescription?.let{o.append(it).append(" ")};for(i in 0 until n.childCount)collect(n.getChild(i),o)}
 private fun findFare(s:String):Double?{
  val n=digits(s).replace(",","").replace("٬","").replace("،","")
  val r=Regex("(?<!\\d)(\\d{2,7})\\s*تومان")
  return r.findAll(n).map{it.groupValues[1].toDouble()}.maxOrNull()
 }
 private fun findDistances(s:String):List<Double>{
  val r=Regex("(\\d+(?:[.,]\\d+)?)\\s*(?:کیلومتر|km)")
  return r.findAll(digits(s)).mapNotNull{it.groupValues[1].replace(",",".").toDoubleOrNull()}.filter{it>0}.toList()
 }
 private fun digits(s:String):String{
  val fa="۰۱۲۳۴۵۶۷۸۹";val ar="٠١٢٣٤٥٦٧٨٩";var r=s
  fa.forEachIndexed{i,c->r=r.replace(c,('0'.code+i).toChar())}
  ar.forEachIndexed{i,c->r=r.replace(c,('0'.code+i).toChar())};return r
 }
 override fun onInterrupt(){}
}