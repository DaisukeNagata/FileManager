package com.example.nagatadaisuke.FileManager

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // リクエスト識別用のユニークな値
        val REQUEST_PERMISSIONS_ID = 1000
        // リクエスト用
        val reqPermissions = ArrayList<String>()
        // リクエスト に追加
        reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // パーミション確認
        ActivityCompat.requestPermissions(this, reqPermissions.toTypedArray(), REQUEST_PERMISSIONS_ID)

        if ( PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS),
                    "init.txt"
                )
                if (file.exists()) {
                    try{
                        val file = File(file.path)
                        val scan = Scanner(file)
                        var i = scan.nextLine()
                        var d = scan.next()
                        print(i)
                        print(d)
                    }catch (e: FileNotFoundException){
                        println(e)
                }
            }
        }
    }
}
