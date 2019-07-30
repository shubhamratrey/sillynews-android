package com.sillylife.sillynews.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sillylife.sillynews.R
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment : BaseFragment() {

    companion object {

        fun newInstance(url: String): WebViewFragment {
            val bundle = Bundle()
            bundle.putString("url", url)
            val fragment = WebViewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_webview, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = arguments?.getString("url")
        loadWebUrl(url)
    }

    private fun loadWebUrl(url: String?) {
        try {
            preLoader?.visibility = View.VISIBLE
            val webSettings = webView.getSettings()
            if (!webSettings.javaScriptEnabled) {
                webSettings.javaScriptEnabled = true
            }
            webView?.loadUrl(url)
            webView?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    preLoader?.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
