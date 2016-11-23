
package com.wicare.wistorm.zbar.camera;

import java.io.IOException;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;


@SuppressWarnings("deprecation")
public final class CameraManager
{

  private static CameraManager cameraManager;

  static final int SDK_INT; 
  static 
  {
    int sdkInt;
    try 
    {
      sdkInt = Integer.parseInt(Build.VERSION.SDK);
    } 
    catch (NumberFormatException nfe) 
    {
      sdkInt = 10000;
    }
    SDK_INT = sdkInt;
  }

  private final Context context;
  private final CameraConfigurationManager configManager;
  private Camera camera;
  private Rect framingRect;
  private Rect framingRectInPreview;
  private boolean initialized;
  private boolean previewing;
  private final boolean useOneShotPreviewCallback;

  private final PreviewCallback previewCallback;
  private final AutoFocusCallback autoFocusCallback;


  public static void init(Context context) 
  {
    if (cameraManager == null)
    {
      cameraManager = new CameraManager(context);
    }
  }

  public static CameraManager get() 
  {
    return cameraManager;
  }

  private CameraManager(Context context) 
  {

    this.context = context;
    this.configManager = new CameraConfigurationManager(context);

    // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
    // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
    // the more efficient one shot callback, as the older one can swamp the system and cause it
    // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
    //useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.CUPCAKE;
    useOneShotPreviewCallback = Integer.parseInt(Build.VERSION.SDK) > 3; // 3 = Cupcake
    previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
    autoFocusCallback = new AutoFocusCallback();
    
  }

  /**
   * Opens the camera driver and initializes the hardware parameters.
   *
   * @param holder The surface object which the camera will draw preview frames into.
   * @throws IOException Indicates the camera driver failed to open.
   */
  public void openDriver(SurfaceHolder holder) throws IOException {
    if (camera == null) 
    {
      camera = Camera.open();
      if (camera == null) 
      {
        throw new IOException();
      }
      camera.setPreviewDisplay(holder);

      if (!initialized) 
      {
        initialized = true;
        configManager.initFromCameraParameters(camera);
      }
      configManager.setDesiredCameraParameters(camera);

      FlashlightManager.enableFlashlight();
    }
  }

  public void closeDriver() 
  {
    if (camera != null) 
    {
      FlashlightManager.disableFlashlight();
      camera.release();
      camera = null;
    }
  }

  /**
   * Asks the camera hardware to begin drawing preview frames to the screen.
   */
  public void startPreview() 
  {
    if (camera != null && !previewing) 
    {
      camera.startPreview();
      previewing = true;
    }
  }

  /**
   * Tells the camera to stop drawing preview frames.
   */
  public void stopPreview() {
    if (camera != null && previewing) 
    {
      if (!useOneShotPreviewCallback) 
      {
        camera.setPreviewCallback(null);
      }
      camera.stopPreview();
      previewCallback.setHandler(null, 0);
      autoFocusCallback.setHandler(null, 0);
      previewing = false;
    }
  }

  /**
   * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
   * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
   * respectively.
   *
   * @param handler The handler to send the message to.
   * @param message The what field of the message to be sent.
   */
  public void requestPreviewFrame(Handler handler, int message) 
  {
    if (camera != null && previewing) 
    {
      previewCallback.setHandler(handler, message);
      if (useOneShotPreviewCallback) 
      {
        camera.setOneShotPreviewCallback(previewCallback);
      } 
      else 
      {
        camera.setPreviewCallback(previewCallback);
      }
    }
  }

  /**
   * Asks the camera hardware to perform an autofocus.
   *
   * @param handler The Handler to notify when the autofocus completes.
   * @param message The message to deliver.
   */
  public void requestAutoFocus(Handler handler, int message) 
  {
    if (camera != null && previewing) 
    {
      autoFocusCallback.setHandler(handler, message);
      camera.autoFocus(autoFocusCallback);
    }
  }

  /**
   * Calculates the framing rect which the UI should draw to show the user where to place the
   * barcode. This target helps with alignment as well as forces the user to hold the device
   * far enough away to ensure the image will be in focus.
   *
   * @return The rectangle to draw on screen in window coordinates.
   */
  public Rect getFramingRect() 
  {
    Point localPoint = configManager.getScreenResolution();
    if (framingRect == null) 
    {
      if (camera == null)
      {
        return null;
      }
      float density = this.context.getResources().getDisplayMetrics().density;
      int width = localPoint.x - 2 * (int)(10.0F * density);
      int height = localPoint.y / 3;
      int left = (localPoint.x - width) / 2;
      int top = (localPoint.y - height) / 2;
      this.framingRect = new Rect(left, top, left +width, top + height);
    }
    return framingRect;
  }


  public Rect getFramingRectInPreview() 
  {
    if (framingRectInPreview == null) 
    {
      Rect rect = new Rect(getFramingRect());
      Point cameraResolution = configManager.getCameraResolution();
      Point screenResolution = configManager.getScreenResolution();
      //modify here
      //rect.left = rect.left * cameraResolution.x / screenResolution.x;
      //rect.right = rect.right * cameraResolution.x / screenResolution.x;
      //rect.top = rect.top * cameraResolution.y / screenResolution.y;
      //rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
      rect.left = rect.left * cameraResolution.y / screenResolution.x;
      rect.right = rect.right * cameraResolution.y / screenResolution.x;
      rect.top = rect.top * cameraResolution.x / screenResolution.y;
      rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
      framingRectInPreview = rect;
    }
    return framingRectInPreview;
  }
  
  public Context getContext() 
  {
		return context;
  }
	
  public void openLight()   //打开闪光灯
  {
      if(camera!=null)
      {
          Parameters parameter=camera.getParameters();  
          parameter.setFlashMode(Parameters.FLASH_MODE_TORCH); 
          camera.setParameters(parameter);
      }
  }
    
  public void offLight()  //关闭闪光灯
  {
      if(camera!=null)
      {
         Parameters parameter=camera.getParameters();  
         parameter.setFlashMode(Parameters.FLASH_MODE_OFF); 
         camera.setParameters(parameter);
      }
  }

}
