package com.chopchop.minecloud

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import com.chopchop.minecloud.Utils.getRubBalance
import com.chopchop.minecloud.Utils.getUserId
import com.chopchop.minecloud.Utils.saveBalance
import com.chopchop.minecloud.Utils.saveRazgon
import com.chopchop.minecloud.Utils.saveRubBalance
import com.chopchop.minecloud.Utils.saveUserId
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
    private lateinit var balanceTW:TextView
    private lateinit var rubBalanceTW:TextView
    var rubBalance: Float = 0f
    var schet:Double = 0.00000001
    var razgon = 1f;
    var mRewardedAd: RewardedAd? = null
    private var mySubs = 0
    private var mInterstitialAd: InterstitialAd? = null
    val isDebug = true
    var isNetworkAvaible = true
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = Firebase.auth
        var currentUser = auth.currentUser
// Configure Google Sign In
        if(Utils.getUserId(this) == -1) {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("849378068088-5vtfcgld47rd2kpd4vl420a1et8q0dlr.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

        startActivityForResult(googleSignInClient.signInIntent,1)
        }
        speedBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet))
        enterCodeBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.enterCodeSheet))
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED



        balanceTW= findViewById<TextView>(R.id.balanceTW)
        rubBalanceTW = findViewById<TextView>(R.id.rubBalance)
        val speedTW = findViewById<TextView>(R.id.speedTW)
        val hashTW = findViewById<TextView>(R.id.hashTW)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        schet = getBalance(this)
        razgon = getRazgon(this)
        mySubs = getMySubs(this)
        rubBalance = getRubBalance(this)
        if(rubBalance != 0f) {
            val balanceTW2 = findViewById<TextView>(R.id.balanceTW2)
            balanceTW2.visibility = View.VISIBLE
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            balanceTW2.text = df.format(rubBalance)+" "
        }
        updateRazgon()

        addBilling()



        object : CountDownTimer(99999999,300000){
            override fun onTick(p0: Long) {
                var adRequest = AdRequest.Builder().build()
                InterstitialAd.load(this@MainActivity,"ca-app-pub-7011036469996496/2445671764", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("TAG", adError?.message)
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("TAG", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                        mInterstitialAd?.show(this@MainActivity)
                    }
                })
            }

            override fun onFinish() {
                finish()
            }

        }.start()

        object : CountDownTimer(99999999,10000){
            override fun onTick(p0: Long) {
                if(!verifyAvailableNetwork(this@MainActivity)){
                    Toast.makeText(this@MainActivity, "???????????????????? ??????????????????. ???????? ???????????? ????????????????",Toast.LENGTH_LONG).show()
                    isNetworkAvaible = false
                }else
                    isNetworkAvaible = true

            }

            override fun onFinish() {
                finish()
            }

        }.start()



        launch(UI) {
                while (true){
                    if(isNetworkAvaible) {
                        mySubs = getMySubs(this@MainActivity)
                        saveBalance(this@MainActivity, schet)
                        schet += 0.00000001;
                        progressBar.progress = 0
                        while (progressBar.progress < 99) {
                            progressBar.progress += 10
                            delay(((5000..7000).random() / 10 / razgon).toLong())
                            speedTW.text = (116 * razgon).toInt()
                                .toString() + (0..99).random() + "." + (1000..9999).random() + " H/s"

                            val source = "abcdefghijklmnopqrstuvwxyz123456789123456789123456789"

                            hashTW.text = java.util.Random().ints(55, 0, source.length)
                                .toArray()
                                .map(source::get)
                                .joinToString("")
                        }

                        val strSchet = (schet + mySubs * 0.00005).toBigDecimal().toPlainString()
                            .subSequence(0, 10).toString()
                        balanceTW.text = "$strSchet "
                        rubBalanceTW.text =
                            "~ " + (balanceTW.text.toString().toFloat()/0.000000371).toBigDecimal()
                                .toPlainString().subSequence(0, 5).toString() + " "

                    }else
                        delay(1000)
                }
        }
        //init ADD
        initAdMob()

    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser

                    if(isDebug) {
                        FirebaseApp.initializeApp(/*context=*/this)
                        val firebaseAppCheck = FirebaseAppCheck.getInstance()
                        firebaseAppCheck.installAppCheckProviderFactory(
                            DebugAppCheckProviderFactory.getInstance()
                        )
                        firebaseAppCheck.setTokenAutoRefreshEnabled(true)
                    }else{
                        FirebaseApp.initializeApp(/*context=*/this)
                        val firebaseAppCheck = FirebaseAppCheck.getInstance()
                        firebaseAppCheck.installAppCheckProviderFactory(
                            SafetyNetAppCheckProviderFactory.getInstance()
                        )
                        firebaseAppCheck.setTokenAutoRefreshEnabled(true)
                    }

                    val database = Firebase.database
                    myRef = database.getReference("users_refers")

                    createUser(balanceTW,rubBalanceTW)



                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun addBilling() {
        val inAppBilling = inAppBilling(){addRazgonCount:Int ->
            razgon += addRazgonCount
            updateRazgon()
        }
        inAppBilling.initBilling(this)
        findViewById<Button>(R.id.add2xRazgon).setOnClickListener { inAppBilling.getBillingProcessor().purchase(this,"2x") }
        findViewById<Button>(R.id.add6xRazgon).setOnClickListener { inAppBilling.getBillingProcessor().purchase(this,"6x") }
        findViewById<Button>(R.id.add25xRazgon).setOnClickListener { inAppBilling.getBillingProcessor().purchase(this,"25x") }
        findViewById<Button>(R.id.add50xRazgon).setOnClickListener {  inAppBilling.getBillingProcessor().purchase(this,"50x")}
        findViewById<Button>(R.id.add100xRazgon).setOnClickListener { inAppBilling.getBillingProcessor().purchase(this,"100x") }
        findViewById<Button>(R.id.add200xRazgon).setOnClickListener {  inAppBilling.getBillingProcessor().purchase(this,"200x")}
    }

    private fun initAdMob() {
        MobileAds.initialize(this) {}
        var adRequest = AdRequest.Builder().build()
        findViewById<AdView>(R.id.adView).loadAd(adRequest)
        adRequest = AdRequest.Builder().build()
        RewardedAd.load(this,"ca-app-pub-7011036469996496/3865606789", adRequest, object : RewardedAdLoadCallback() {
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
    fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!= null && networkInfo.isConnected
    }

    fun onClickBoost(v: View){
        speedBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun enterCode(view: android.view.View) {
        enterCodeBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun createUser(balanceTW: TextView, rubBalanceTW: TextView) {
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
                    if(snapshot.value!= null)
                        if(snapshot.value.toString().toInt() != getMySubs(this@MainActivity)){
                                addMySubs(this@MainActivity,snapshot.value.toString().toInt())
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            userIdTW.text = userId.toString()
            balanceTW.text = "${(schet+mySubs*0.00005).toBigDecimal().toPlainString().subSequence(0,10)} "
            rubBalanceTW.text = "~ "+((schet+mySubs*0.00005)/0.000000371).toBigDecimal().toPlainString().subSequence(0,5).toString() + " "

        }
    }

    fun sendCode(view: android.view.View) {
        val userId2 = findViewById<EditText>(R.id.userId2)
        myRef.child(userId2.text.toString()).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            if(it.value != null)
                if(addSub(this,userId2.text.toString().toInt())){
                    myRef.child(userId2.text.toString()).setValue(it.value.toString().toInt()+1)
                    Toast.makeText(this,"???? ?????????????????????? ???? ???????????????????????? ${userId2.text}", Toast.LENGTH_SHORT).show()
                    enterCodeBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else
                    Toast.makeText(this,"???? ?????? ?????????????????????? ???? ?????????? ????????????????????????", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,"???????????????????????? ???? ????????????", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            Toast.makeText(this,"???????????? ?????????????????? ????????????", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateRazgon(){

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        findViewById<TextView>(R.id.razgonTW).text = "????????????: ${df.format(razgon)}X"
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
                        addRazgonByAd.text = "$addRazgonByAdText ${tick}??????"
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
                        Toast.makeText(this@MainActivity,"?????? ?????????????? ????????????????????, ?????????????????? ?????????????? ???????? ??????????" ,Toast.LENGTH_SHORT).show()
                        initAdMob()
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

    fun trade(view: android.view.View) {
        if((schet+mySubs*0.00005) < 0.0001) {
            Toast.makeText(
                this,
                "???????????????????????? ?????????????? (?????? ???????????? ?????????? ?????????? 0,00010000)",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if(razgon < 10){
            Toast.makeText(
                this,
                "?????????????????????????? ???????????? (?????? ???????????? ?????????? ?????????? ??10)",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val balanceTW2 = findViewById<TextView>(R.id.balanceTW2)
        Toast.makeText(
            this,
            "??????????????????????, ?????? ???????????? ??????????????????????????????. ?????????????? ?????? ?????????????? ?? ????????!",
            Toast.LENGTH_LONG
        ).show()
            balanceTW2.visibility = View.VISIBLE
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            rubBalance += ((schet+mySubs*0.00005)/0.000000371).toFloat()
            balanceTW2.text = df.format(rubBalance)+" "
            saveRubBalance(this,rubBalance)
        schet = 0.00000002
        saveBalance(this@MainActivity,schet)
        val userId = getUserId(this)
        myRef.child(userId.toString()).setValue(0)
        addMySubs(this@MainActivity,0)

        balanceTW.text = "0 "
        rubBalanceTW.text = "~ 0 "

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
   //     }
    }
}