package com.example.nagatadaisuke.FileManager

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*
決済をするときにtimeCount = trueにする
*/
class MainActivity : AppCompatActivity() {
    var imei = ""
    var check = ""
    var number = 0
    private val fileName = "test.txt"
    private val timeName = "time.txt"
    var certainDay = ""
    lateinit var prefs: SharedPreferences

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("FLAG_NAME", AppCompatActivity.MODE_PRIVATE)
        check = (prefs.getString("FLAG_NAME", check))
        if  (check == "") {
            val today = getToday()
            check = today.substring(7, 8)
            saveFile(timeName,check)
            val e : SharedPreferences.Editor = prefs.edit()
            e.putString("FLAG_NAME" , check)
            e.apply()
            check = prefs.getString("FLAG_NAME", check)
        }

        // リクエスト識別用のユニークな値
        val REQUEST_PERMISSIONS_ID = 1000
        // リクエスト用
        val reqPermissions = ArrayList<String>()
        // リクエスト に追加
        reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        reqPermissions.add(Manifest.permission.READ_PHONE_STATE)
        // パーミション確認
        ActivityCompat.requestPermissions(this, reqPermissions.toTypedArray(), REQUEST_PERMISSIONS_ID)

            // 拒否されている時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE) && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒否されている時　（一度、requestPermissionsで拒否されているとこちらに来る）
                // 再度許可を求める
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), REQUEST_PERMISSIONS_ID)
            } else {
                //　許可を求めるダイアログを表示
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), REQUEST_PERMISSIONS_ID)
        }

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                imei = telephonyManager.imei
            } else {
                imei = telephonyManager.getImei()
            }
        }
        idGet()
    }

    private fun idGet(){
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS),
            "init.txt"
        )

        if (readFiles(fileName) != null){
            number = readFiles(fileName)!!.toInt()
        }

        if (file.exists()) {
            try{
                val file = File(file.path)
                val scan = Scanner(file)
                var i = scan.nextLine()
                val yymmdd = getToday().substring(3, 8)
                var y = yymmdd.substring(0,1)
                var mdd = yymmdd.substring(2,5)
                var ymdd = y+mdd
                var id = i+ymdd
                var n = String.format("%06d",number)
                var trationID = imei + id + n
                print(trationID)
                number += 1
            }catch (e: FileNotFoundException){
                println(e)
            }
        }
        saveFile(fileName,number.toString())
        checkTIme()
    }

    //日付の変更でnumberの変更
    private fun checkTIme() {
        certainDay = getToday().substring(7, 8)
        if (certainDay != check){
            number = 0
            saveFile(fileName,number.toString())
            check = getToday().substring(7, 8)
            saveFile(timeName,check)
            val e : SharedPreferences.Editor = prefs.edit()
            e.putString("FLAG_NAME" , certainDay)
            e.apply()
            check = prefs.getString("FLAG_NAME", certainDay)
        }
    }

    //yMdd形式の日付取得
    private fun getToday(): String {
            val date = Date()
            val format = SimpleDateFormat("yMdd", Locale.getDefault())
            return format.format(date)
    }

    // ファイルを読み出し
    private fun readFiles(file: String): String? {
        // to check whether file exists or not
        val readFile = File(applicationContext.filesDir, file)

        if(!readFile.exists()){
            Log.d("debug","No file exists")
            return null
        }
        else{
            return readFile.bufferedReader().use(BufferedReader::readText)
        }
    }

    // ファイルを保存
    private fun saveFile(file: String, str: String) {
        // try-with-resources
        try {
            openFileOutput(
                file,
                Context.MODE_PRIVATE
            ).use { fileOutputstream ->

                fileOutputstream.write(str.toByteArray())

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}