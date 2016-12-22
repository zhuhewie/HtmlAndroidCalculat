package com.example.administrator.htmlandroidcalculat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {


    private Activity mActivity = null;
    private WebView mWebView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        showWebView();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showWebView(){		// webView与js交互代码
        try {
            mWebView = new WebView(this);
            setContentView(mWebView);

            mWebView.requestFocus();

            mWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int progress){
                    MainActivity.this.setTitle("Loading...");
                    MainActivity.this.setProgress(progress);

                    if(progress >= 80) {
                        MainActivity.this.setTitle("加法计算器");
                    }
                }
            });

            // webview can go back
            mWebView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        mWebView.goBack();
                        return true;
                    }
                    return false;
                }
            });

            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            //Android（Java）与js（HTML）交互的接口函数

            mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");

            mWebView.loadUrl("file:///android_asset/index.html");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private Object getHtmlObject(){
        Object insertObj = new Object(){
            @JavascriptInterface
            public String HtmlcallJava(){
                return "Html call Java";
            }

            @JavascriptInterface
            public String HtmlcallJava2(final String algorithm,final String a,final String b){
                if (!TextUtils.isEmpty(algorithm)) {
                    if(!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b)){
                        try {
                            float result;
                            BigDecimal ba = new BigDecimal(a);
                            BigDecimal bb = new BigDecimal(b);
                            float fa = Float.parseFloat(a);
                            float fb = Float.parseFloat(b);
                            switch (algorithm) {
                                case "加":
                                    result = ba.add(bb).floatValue();
                                    break;
                                case "减":
                                    result = ba.subtract(bb).floatValue();
                                    break;
                                case "乘":
                                    result =  ba.multiply(bb).floatValue();
                                    break;
                                case "除":
                                    result = ba.divide(bb,5,BigDecimal.ROUND_HALF_UP).floatValue();
                                    break;
                                default:
                                    return "无法识别的算法";

                            }
                            return result + "";
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            return "输入数字不合法";
                        } catch (Exception e) {
                            e.printStackTrace();
                            return "未知异常,无法计算";
                        }

                    }
                    else {
                        return "输入数字不合法";
                    }
                } else {
                    return "请选着算法";
                }

            }
        };

        return insertObj;
    }
}
