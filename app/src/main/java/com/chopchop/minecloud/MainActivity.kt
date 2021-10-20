package com.chopchop.minecloud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.constraintlayout.widget.ConstraintLayout
import com.chopchop.minecloud.Utils.addMySubs
import com.chopchop.minecloud.Utils.addSub
import com.chopchop.minecloud.Utils.getBalance
import com.chopchop.minecloud.Utils.getMySubs
import com.chopchop.minecloud.Utils.getRazgon
import com.chopchop.minecloud.Utils.getUserId
import com.chopchop.minecloud.Utils.saveBalance
import com.chopchop.minecloud.Utils.saveRazgon
import com.chopchop.minecloud.Utils.saveUserId
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var speedBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var enterCodeBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var myRef:DatabaseReference
    var schet:Double = 0.00000001
    var razgon = 1f;
    var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        speedBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet))
        enterCodeBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.enterCodeSheet))
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("users_refers")


        val ballanceTW = findViewById<TextView>(R.id.balanceTW)
        val rubBalance = findViewById<TextView>(R.id.rubBalance)
        val speedTW = findViewById<TextView>(R.id.speedTW)
        val hashTW = findViewById<TextView>(R.id.hashTW)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        schet = getBalance(this)
        razgon = getRazgon(this)
        updateRazgon()

        createUser(ballanceTW,rubBalance)






        launch(UI) {

                while (true){
                    saveBalance(this@MainActivity,schet)
                    schet+=0.00000001;
                    progressBar.progress = 0
                    while(progressBar.progress < 99){
                        progressBar.progress +=10
                        delay(((5000..7000).random()/10/razgon).toLong())
                        speedTW.text = "116"+ (0..99).random()+"."+(1000..9999).random()+" H/s"

                        val source = "abcdefghijklmnopqrstuvwxyz123456789123456789123456789"

                        hashTW.text = java.util.Random().ints(55, 0, source.length)
                            .toArray()
                            .map(source::get)
                            .joinToString("")
                    }

                    val strSchet = schet.toBigDecimal().toPlainString().subSequence(0,10).toString()
                    ballanceTW.text = "$strSchet "
                    rubBalance.text = "~ "+(schet/0.000000371).toBigDecimal().toPlainString().subSequence(0,5).toString() + " "

            }
        }
        //init ADD
        initAdMob()

    }

    private fun initAdMob() {
        MobileAds.initialize(this) {}
        findViewById<AdView>(R.id.adView).loadAd(AdRequest.Builder().build())
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("TAG", adError?.message)
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("TAG", "Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })


    }

    fun onClickBoost(v: View){
        speedBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun enterCode(view: android.view.View) {
        enterCodeBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun createUser(ballanceTW: TextView, rubBalance: TextView) {
        val userIdTW = findViewById<TextView>(R.id.userId)
        if(getUserId(this) == -1) {
            val userId = (100000..999999).random()
            saveUserId(this,userId)
            myRef.child(userId.toString()).setValue(0)
            userIdTW.text = userId.toString()
        }else{
            val userId = getUserId(this)
            myRef.child(userId.toString()).addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value.toString().toInt() > getMySubs(this@MainActivity)){
                        addMySubs(this@MainActivity)
                        schet+= 0.00005
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            userIdTW.text = userId.toString()
            ballanceTW.text = "${schet.toBigDecimal().toPlainString().subSequence(0,10)} BTC"
            rubBalance.text = "~ "+(schet/0.000000371).toBigDecimal().toPlainString().subSequence(0,5).toString() + " ₽"

        }
    }

    fun sendCode(view: android.view.View) {
        val userId2 = findViewById<EditText>(R.id.userId2)
        myRef.child(userId2.text.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            if(it.value != null)
                if(addSub(this,userId2.text.toString().toInt())){
                    myRef.child(userId2.text.toString()).setValue(it.value.toString().toInt()+1)
                    Toast.makeText(this,"Вы подписались на пользователя ${userId2.text}", Toast.LENGTH_SHORT).show()
                    enterCodeBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else
                    Toast.makeText(this,"Вы уже подписались на этого пользователя", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"Пользователь не найден", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this,"Ошибка получения данных", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateRazgon(){
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        findViewById<TextView>(R.id.razgonTW).text = "Разгон: ${df.format(razgon)}X"
        saveRazgon(this,razgon)
    }

    fun addRazgonByAd(view: android.view.View) {
        val addRazgonByAd = findViewById<Button>(R.id.addRazgonByAd)
        val addRazgonByAdText = addRazgonByAd.text.toString()
        if (mRewardedAd != null) {
            mRewardedAd?.show(this@MainActivity) {
                razgon += 0.05f
                updateRazgon()
                speedBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                addRazgonByAd.isEnabled = false
                var tick = 15
                object : CountDownTimer(15000,1000){
                    override fun onTick(p0: Long) {
                        addRazgonByAd.text = "$addRazgonByAdText ${tick}сек"
                        tick--
                    }
                    override fun onFinish() {
                        addRazgonByAd.text = addRazgonByAdText
                        addRazgonByAd.isEnabled = true
                    }
                }.start()
                var adRequest = AdRequest.Builder().build()
                RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("TAG", adError?.message)
                        mRewardedAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        Log.d("TAG", "Ad was loaded.")
                        mRewardedAd = rewardedAd
                    }
                })
            }
        }

    }
}