package org.robolectric.shadows;

import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;

import android.os.Trace;
import android.util.Log;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.concurrent.GuardedBy;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.Resetter;

/**
 * Shadow implementation for {@link Trace}, which stores the traces locally in arrays (unlike the
 * real implementation) and allows reading them.
 */
@Implements(Trace.class)
public class ShadowTrace {

  @GuardedBy("lock")
  private static final Deque<String> currentSections = new LinkedList<>();

  @GuardedBy("lock")
  private static final Queue<String> previousSections = new LinkedList<>();

  private static boolean crashOnIncorrectUsage = false;
  private static final Object lock = new Object();

  /** Starts a new trace section with given name. */
  @Implementation(minSdk = JELLY_BEAN_MR2)
  protected static void beginSection(String sectionName) {
    if (crashOnIncorrectUsage && sectionName == null) {
      throw new NullPointerException("Section name cannot be null");
    }

    synchronized (lock) {
      currentSections.addFirst(sectionName);
    }
  }

  /**
   * Ends the most recent active trace section.
   *
   * @throws {@link AssertionError} if called without any active trace section.
   */
  @Implementation(minSdk = JELLY_BEAN_MR2)
  protected static void endSection() {
    synchronized (lock) {
      if (currentSections.isEmpty()) {
        if (crashOnIncorrectUsage) {
          throw new IllegalStateException("Trying to end a trace section that was never started");
        }
        Log.w("ShadowTrace", "Trying to end a trace section that was never started");
        return;
      }

      previousSections.offer(currentSections.removeFirst());
    }
  }

  /** Returns a stack of the currently active trace sections. */
  public static Deque<String> getCurrentSections() {
    synchronized (lock) {
      return new LinkedList<>(currentSections);
    }
  }

  /** Returns a queue of all the previously active trace sections. */
  public static Queue<String> getPreviousSections() {
    synchronized (lock) {
      return new LinkedList<>(previousSections);
    }
  }

  /**
   * Set whether to crash on incorrect usage (e.g., calling {@link #endSection()} before {@link
   * beginSection(String)}. Default value - {@code false}.
   */
  public static void setCrashOnIncorrectUsage(boolean crashOnIncorrectUsage) {
    ShadowTrace.crashOnIncorrectUsage = crashOnIncorrectUsage;
  }

  /** Resets internal lists of active trace sections. */
  @Resetter
  public static void reset() {
    synchronized (lock) {
      currentSections.clear();
      previousSections.clear();
    }
    crashOnIncorrectUsage = false;
  }
}
