package com.chopchop.minecloud

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.SkuDetails
import com.chopchop.minecloud.Utils.getRazgon
import com.chopchop.minecloud.Utils.saveRazgon

val gplay_lience = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0qMnNx/5piQt1dXUdFSb2yn5qqvyhA6Rx67n6jWGDeEtuKgbBIF+BcIxaWywe7EllISqC/sAuXrh/dAM6rd3BCWTfmosX8J/vannLw2/0UP9sEOjUxQ8Q4oHCCLWjE8bhx0/MlYtSMDRAmhoKWHJ8+vhlU5Jz/R7a6UMUFJDKWVedOrEnKdjS/hARsrF6+CQQG7o7UXru562dIetY6w+ZCHthSYyG6cNUVJVh+kkP9vV0JQqIDhc/XVvnIObw2U9UYxzNin4J+IN5wyE8d5tJfY1ztc8eIogHEIjhPt1y0MSfmqJVpdlKH7XSIZfsNg2+fb37AvSdKZv5+CgBc6bmwIDAQAB"

class inAppBilling(val addRazgonCount: (Int) -> Unit) :BillingProcessor.IBillingHandler,BillingProcessor.IPurchasesResponseListener , BillingProcessor.ISkuDetailsResponseListener{
    lateinit var mBillingProcessor: BillingProcessor
    lateinit var context: Context
    fun getBillingProcessor():BillingProcessor{
        return mBillingProcessor
    }
//
    fun initBilling(context:Context){
        this.context = context
        mBillingProcessor = BillingProcessor(context, gplay_lience, this)
        val isAvailable: Boolean = BillingProcessor.isIabServiceAvailable(context)
        if (!isAvailable) {
            Log.i(TAG, "initBilling: INIT NOT COMPLETE")
        } else {
            mBillingProcessor.initialize()
        }
    }
//
    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         *
         *
         *
         */
        Log.i(TAG, "onProductPurchased: onProductPurchased")
        Log.i(TAG, "purchase: $productId COMPLETED")
            when (productId) {
                "2x" -> addRazgonCount.invoke(2)
                "6x" -> addRazgonCount.invoke(6)
                "25x" -> addRazgonCount.invoke(25)
                "100x" -> addRazgonCount.invoke(100)
                "50x" -> addRazgonCount.invoke(50)
                "200x" -> addRazgonCount.invoke(200)
            }

    }
    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "onBillingError: ", )
    }

    override fun onBillingInitialized() {
        Log.i(TAG, "initBilling: INIT COMPLETE")
        mBillingProcessor.loadOwnedPurchasesFromGoogleAsync(this)
    }

    override fun onPurchasesSuccess() {
        Log.i(TAG, "initBilling: onPurchasesSuccess")
    }

    override fun onPurchasesError() {
        Log.i(TAG, "initBilling: onPurchasesError")
    }




    override fun onSkuDetailsResponse(products: MutableList<SkuDetails>?) {

    }

    override fun onSkuDetailsError(error: String?) {
    }
}