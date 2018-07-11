package com.ibm.cio.plugins;

import android.util.Log;

import com.ibm.watson.developer_cloud.android.speech_to_text.v1.ISpeechToTextDelegate;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.dto.STTConfiguration;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mihui on 5/29/16.
 */
public class Stt extends CordovaPlugin implements ISpeechToTextDelegate{

    private final String TAG = this.getClass().getSimpleName();

    private CallbackContext recognizeContext = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
       // this.initSTT();
    }

    /**
     * execute plugin invokes
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return boolean
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext){
        if(action.equals("init")){
            boolean result = this.initSTT(args);
            Log.d(TAG, "### init result : "+result);
            return true;
        }
        if(action.equals("recognize")){
            this.recognizeContext = callbackContext;
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    recognize();
                }
            });

            return true;
        }
        if(action.equals("endTransmission")){
            this.cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    endTransmission();
                }
            });
            return true;
        }
        return false;
    }

    private void endTransmission() {
        SpeechToText.sharedInstance().endTransmission();
    }

    private void recognize() {
        SpeechToText.sharedInstance().recognize();
    }

    // initialize the connection to the Watson STT service
    private boolean initSTT(JSONArray args) {
        
        try {


            STTConfiguration sConfig = new STTConfiguration();

            String username="";
            String password="";
            String url;

            Log.d(TAG, "### Args: "+args);
            JSONArray vcapJson= args.getJSONArray(0);
            Log.d(TAG, "### vcapJson: "+vcapJson);

            JSONObject obj = vcapJson.getJSONObject(0);
            if(obj.getJSONArray("speech_to_text") != null) {
                    JSONArray credentialsList = obj.getJSONArray("speech_to_text");

                    if (credentialsList.length() > 0) {
                         username = credentialsList.getJSONObject(0).getJSONObject("credentials").getString("username");
                         url = credentialsList.getJSONObject(0).getJSONObject("credentials").getString("url");
                         password = credentialsList.getJSONObject(0).getJSONObject("credentials").getString("password");            
                        sConfig.basicAuthUsername = username;
                        sConfig.basicAuthPassword = password;
                        Log.d(TAG, "### username: "+username);
                        Log.d(TAG, "### password: "+password);
                    }
            }
            JSONObject optionsObj = vcapJson.getJSONObject(1);
                if(optionsObj.getJSONObject("options") != null){
                    JSONObject options = optionsObj.getJSONObject("options");
                    Log.d(TAG, "### options: "+options);

                    String audioFormat = options.getString("audioFormat");
                    sConfig.audioFormat = audioFormat;
                    int audioSampleRate = options.getInt("audioSampleRate");
                    sConfig.audioSampleRate = audioSampleRate;
                    int inactivityTimeout = options.getInt("inactivityTimeout");
                    sConfig.inactivityTimeout = inactivityTimeout;
                    boolean continuous = options.getBoolean("continuous");
                    sConfig.continuous = continuous;
                    boolean interimResults = options.getBoolean("interimResults");
                    sConfig.interimResults =interimResults;
                    String languageModel= options.getString("languageModel");
                    sConfig.languageModel = languageModel;
                    boolean isAuthNeeded = options.getBoolean("isAuthNeeded");
                    sConfig.isAuthNeeded = isAuthNeeded;
                    boolean isSSL = options.getBoolean("isSSL");
                    sConfig.isSSL = isSSL;
                    int connectionTimeout = options.getInt("connectionTimeout");
                    sConfig.connectionTimeout = connectionTimeout;
                    double keywordsThreshold = options.getDouble("keywordsThreshold");
                    sConfig.keywordsThreshold = keywordsThreshold;
                    double wordAlternativesThreshold = options.getDouble("wordAlternativesThreshold");
                    sConfig.wordAlternativesThreshold = wordAlternativesThreshold;
                    boolean smartFormatting = options.getBoolean("smartFormatting");
                    sConfig.smartFormatting = smartFormatting;
                    boolean timestamps = options.getBoolean("timestamps");
                    sConfig.timestamps = timestamps;
                    boolean profanityFilter = options.getBoolean("profanityFilter");
                    sConfig.profanityFilter = profanityFilter;
                    boolean wordConfidence = options.getBoolean("wordConfidence");
                    sConfig.wordConfidence = wordConfidence;
                    String apiEndpoint = options.getString("apiEndpoint");
                    if(options.getString("apiURL") != null && !options.getString("apiURL").isEmpty()) {
                        String apiURL = options.getString("apiURL");
                        sConfig.setAPIURL(apiURL); 
                    }
                    boolean xWatsonLearningOptOut = options.getBoolean("xWatsonLearningOptOut");
                    sConfig.xWatsonLearningOptOut = xWatsonLearningOptOut;
                    Log.d(TAG, "### sConfig: "+sConfig.toString());
                }

                SpeechToText.sharedInstance().initWithConfig(sConfig, this);


        }catch (JSONException e) {
                e.printStackTrace();
                this.sendStatus("onMessage: Error parsing JSON");
                this.recognizeContext.error("Data error");
                //SpeechToText.sharedInstance().stopRecognition();
                return false;
        }




        return true;
    }

    private void sendStatus(final String status) {
        Log.d(TAG, "### Status: "+status);
    }

    @Override
    public void onOpen() {
        sendStatus("onOpen: successfully connected to the STT service");
    }

    @Override
    public void onBegin() { }

    @Override
    public void onError(int statusCode, String error) {
        sendStatus("onError: " + error);
        this.recognizeContext.error("Data error");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        try {
            sendStatus("onClose: connection closed: code: " + code + " reason: " + reason);
            JSONObject finalResult = new JSONObject();

            finalResult.put("iscompleted", "Yes");
            finalResult.put("isfinal", "Yes");
            finalResult.put("message", "");
            PluginResult result = new PluginResult(PluginResult.Status.OK, finalResult);
            result.setKeepCallback(true);
            this.recognizeContext.sendPluginResult(result);
            SpeechToText.sharedInstance().disConnect();
            SpeechToText.sharedInstance().stopRecording();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject jObj = new JSONObject(message);

            JSONArray resultList = jObj.getJSONArray("results");

            for (int i = 0; i < resultList.length(); i++) {
                JSONObject obj = resultList.getJSONObject(i);
                JSONArray alternativeList = obj.getJSONArray("alternatives");
                if(alternativeList.length() > 0) {
                    String str = alternativeList.getJSONObject(0).getString("transcript");
                    boolean isFinal = obj.getString("final").equals("true");

                    JSONObject finalResult = new JSONObject();
                    finalResult.put("isfinal", isFinal ? "Yes" : "No");
                    finalResult.put("iscomplete", "No");
                    finalResult.put("message", str);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, finalResult);
                    result.setKeepCallback(true);
                    this.recognizeContext.sendPluginResult(result);
                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
            this.sendStatus("onMessage: Error parsing JSON");
            this.recognizeContext.error("Data error");
            SpeechToText.sharedInstance().stopRecognition();
        }
    }

    @Override
    public void onAmplitude(double v, double v1) {

    }

    @Override
    public void onData(byte[] bytes) {

    }
}
