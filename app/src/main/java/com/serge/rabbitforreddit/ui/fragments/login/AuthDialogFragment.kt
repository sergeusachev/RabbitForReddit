package com.serge.rabbitforreddit.ui.fragments.login

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.serge.rabbitforreddit.R




class AuthDialogFragment : DialogFragment() {

    companion object {
        private const val OAUTH_URL ="https://www.reddit.com/api/v1/authorize.compact"
        private const val CLIENT_ID = "ipveMvWlL7GTAw"
        private const val REDIRECT_URI = "https://rabbit-for-reddit-redirect-android"
        private const val OAUTH_SCOPE="read"
    }

    private lateinit var webView: WebView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_login, null)
        builder.setView(view)


        webView = view.findViewById(R.id.webview) as WebView

        webView.settings.javaScriptEnabled = true
        val url = "$OAUTH_URL?client_id=$CLIENT_ID&response_type=code&state=TEST&redirect_uri=$REDIRECT_URI&scope=$OAUTH_SCOPE"

        webView.loadUrl(url)

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view!!.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }

        return builder.create()
    }
}