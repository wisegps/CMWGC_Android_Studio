package com.wicare.wistorm.zbar.camera;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback {

  private static final String TAG = PreviewCallback.class.getSimpleName();
  private final boolean useOneShotPreviewCallback;
  private Handler previewHandler;
  private int previewMessage;

  public PreviewCallback(CameraConfigurationManager configManager, boolean useOneShotPreviewCallback) 
  {
    this.useOneShotPreviewCallback = useOneShotPreviewCallback;
  }

  public void setHandler(Handler previewHandler, int previewMessage) 
  {
    this.previewHandler = previewHandler;
    this.previewMessage = previewMessage;
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) 
  {
	 if (!useOneShotPreviewCallback) 
	 {
	   camera.setPreviewCallback(null);
	 }
	 if(previewHandler != null) 
	 {
		Camera.Parameters parameters = camera.getParameters();
	    Size size = parameters.getPreviewSize();
	    Message message = previewHandler.obtainMessage(previewMessage, size.width,size.height, data);
	    message.sendToTarget();
	    previewHandler = null;
	 } 
	 else 
	 {
	    Log.d(TAG, "Got preview callback, but no handler for it"); 
	 }
	 
   }

	
}
