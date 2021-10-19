package com.chopchop.minecloud

import android.app.UiModeManager.MODE_NIGHT_NO
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CommonPool
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.DriverManager.getConnection
import java.sql.SQLException
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet))
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val ballanceTW = findViewById<TextView>(R.id.balanceTW)
        val rubBalance = findViewById<TextView>(R.id.rubBalance)
        val speedTW = findViewById<TextView>(R.id.speedTW)
        val hashTW = findViewById<TextView>(R.id.hashTW)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        var schet:Double = 0.00000000
        var razgon = 1;

        start()


        launch(UI) {

                while (true){
                schet+=0.00000001;
                    progressBar.progress = 0
                    while(progressBar.progress < 99){
                        progressBar.progress +=10
                        delay((5000..7000).random().toLong()/10/razgon)
                        speedTW.text = "116"+ (0..99).random()+"."+(1000..9999).random()+" H/s"

                        val source = "abcdefghijklmnopqrstuvwxyz123456789123456789123456789"

                        hashTW.text = java.util.Random().ints(55, 0, source.length)
                            .toArray()
                            .map(source::get)
                            .joinToString("")
                    }

                    val strSchet = schet.toBigDecimal().toPlainString().subSequence(0,10).toString()
                    ballanceTW.text = "$strSchet BTC"
                    rubBalance.text = "~ "+(schet/0.000000371).toBigDecimal().toPlainString().subSequence(0,5).toString() + " ₽"

            }
        }
    }
    fun onClickBoost(v: View){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    internal var conn: Connection? = null



    fun start(){
        getConnection()
    }
    fun getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            conn = DriverManager.getConnection("jdbc:mysql://cloud-mine-rus.zzz.com.ua:3306/cloudmine", "cloudmine", "ceepercom123COM")

        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } catch (ex: Exception) {
            // handle any errors
            ex.printStackTrace()
        }
    }

    fun enterCode(view: android.view.View) {

    }
}