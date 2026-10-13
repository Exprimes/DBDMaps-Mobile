package com.exprimes.dbdmaps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Garder l'écran toujours allumé pendant les parties DBD
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Charger la web app officielle directement dans la WebView
                if (url.contains("exprimes.github.io/dbd-maps")) {
                    view.loadUrl(url);
                    return true;
                }
                // Ouvrir les liens externes et téléchargements dans le navigateur
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    try {
                        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // En cas d'absence de connexion Internet, basculer sur l'archive locale
                try {
                    view.loadUrl("file:///android_asset/www/index.html");
                } catch (Exception ignored) {}
            }
        });
        webView.setBackgroundColor(0xFF0D1117);

        // Charger l'application officielle en ligne (toujours 100% à jour sans re-télécharger d'APK)
        webView.loadUrl("https://exprimes.github.io/dbd-maps/app.html");
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            // Si une modal (Mise à jour, Lobby, Viewer) est ouverte, la fermer au lieu de quitter l'app
            webView.evaluateJavascript(
                "(function() { " +
                "  var u = document.getElementById('updateModal'); if (u && u.style.display === 'flex') { document.getElementById('btnCloseUpdate').click(); return true; } " +
                "  var l = document.getElementById('lobbyModal'); if (l && l.style.display === 'flex') { document.getElementById('btnCloseLobby').click(); return true; } " +
                "  var v = document.getElementById('viewerModal'); if (v && v.style.display === 'flex') { document.getElementById('btnCloseViewer').click(); return true; } " +
                "  return false; " +
                "})()",
                value -> {
                    if ("false".equals(value) || value == null) {
                        super.onBackPressed();
                    }
                }
            );
        } else {
            super.onBackPressed();
        }
    }
}
