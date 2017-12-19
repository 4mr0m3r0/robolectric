package org.robolectric.shadows;

import dalvik.system.SocketTagger;
import java.net.Socket;
import java.net.SocketException;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(value = SocketTagger.class, isInAndroidSdk = false)
public class ShadowSocketTagger {

  @Implementation
  protected final void tag(Socket socket) throws SocketException {
  }

  @Implementation
  protected final void untag(Socket socket) throws SocketException {
  }
}
