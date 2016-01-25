package com.xuehexi.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;
import android.os.Environment;
import android.content.res.Resources;


public class BaiduTts extends CordovaPlugin implements SpeechSynthesizerListener {

    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";


    @Override 
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //Log.i("a","插件调用");
    Context ctx = cordova.getActivity().getApplicationContext();
    
    JSONObject options = new JSONObject();
    try {
        options = args.getJSONObject(0);
    } catch (JSONException e) {
        //Log.v(TAG, "options 未传入");
    }
    if (action.equals("init")) {
        String appId = options.getString("appId");
        String apiKey = options.getString("apiKey");
        String secretKey = options.getString("secretKey");
        String speed = options.getString("speed");
        String pitch = options.getString("pitch");

        initialEnv();
        initialTts(ctx,appId,apiKey,secretKey,speed,pitch);
    }
    if (action.equals("speak")) {
        String txt = options.getString("txt");        
        callbackContext.success(txt);
        speak(txt);
        return true;
    }

    if (action.equals("stop")) {
        stop();
        return true;
    }
    
    return false;
    }


     private void speak(String txt) {
         int result = this.mSpeechSynthesizer.speak(txt);
     }

     private void stop() {
         this.mSpeechSynthesizer.stop();
     }

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        //copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
    }
    
    
    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private Resources getResources() {
        Resources mResources = null;
        mResources = getResources();
        return mResources;
    }

    /**
     * 将需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     * 
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    private void initialTts(Context ctx,String appId,String apiKey, String secretKey,String speed,String pitch) {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(ctx);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        //this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"+ LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId(appId);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey(apiKey, secretKey);
        // 设置在线发音人参数（还可设置其他参数）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, SpeechSynthesizer.SPEAKER_FEMALE);
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        //语速
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED,speed);
        //语调
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH,pitch);
        
        
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
           //Log.i("xuehexi","auth success");
           //callbackContext.success("auth success");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            //Log.i("xuehexi","auth failed errorMsg=" + errorMsg);
            //callbackContext.success(errorMsg);
        }
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
    }

    /*
     * @param arg0
     */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        Log.i("xuehexi","onSynthesizeStart utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     * 
     * @param arg2
     */
    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] data, int progress) {
        // toPrint("onSynthesizeDataArrived");
    }

    /*
     * @param arg0
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        Log.i("xuehexi","onSynthesizeFinish utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     */
    @Override
    public void onSpeechStart(String utteranceId) {
        Log.i("xuehexi","onSpeechStart utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        // toPrint("onSpeechProgressChanged");
    }

    /*
     * @param arg0
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        Log.i("xuehexi","onSpeechFinish utteranceId=" + utteranceId);
    }

    /*
     * @param arg0
     * 
     * @param arg1
     */
    @Override
    public void onError(String utteranceId, SpeechError error) {
        Log.i("xuehexi","onError error=" + "(" + error.code + ")" + error.description + "--utteranceId=" + utteranceId);
    }
}