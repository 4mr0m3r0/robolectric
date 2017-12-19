package org.robolectric.shadows;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(Picture.class)
public class ShadowPicture {

  private int width;
  private int height;

  @Implementation
  protected void __constructor__() {}

  @Implementation
  protected void __constructor__(long nativePicture) {}

  @Implementation
  protected void __constructor__(int nativePicture, boolean fromStream) {}

  @Implementation
  protected void __constructor__(Picture src) {
    width = src.getWidth();
    height = src.getHeight();
  }

  @Implementation
  protected int getWidth() {
    return width;
  }

  @Implementation
  protected int getHeight() {
    return height;
  }

  @Implementation
  protected Canvas beginRecording(int width, int height) {
    this.width = width;
    this.height = height;
    return new Canvas(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
  }
}
