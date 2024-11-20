/*
   Android FreeRDP JNI Wrapper

   Copyright 2013 Thincast Technologies GmbH, Author: Martin Fleisz

   This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
   If a copy of the MPL was not distributed with this file, You can obtain one at
   http://mozilla.org/MPL/2.0/.
*/

package com.freerdp.services;

import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalTime;

public class LibFreeRDP
{
    private static final Logger logger = LoggerFactory.getLogger(LibFreeRDP.class);
/*
    public static final long VERIFY_CERT_FLAG_NONE = 0x00;
    public static final long VERIFY_CERT_FLAG_LEGACY = 0x02;
    public static final long VERIFY_CERT_FLAG_REDIRECT = 0x10;
    public static final long VERIFY_CERT_FLAG_GATEWAY = 0x20;
    public static final long VERIFY_CERT_FLAG_CHANGED = 0x40;
    public static final long VERIFY_CERT_FLAG_MISMATCH = 0x80;
    public static final long VERIFY_CERT_FLAG_MATCH_LEGACY_SHA1 = 0x100;
    public static final long VERIFY_CERT_FLAG_FP_IS_PEM = 0x200;
*/
    static
    {
        try
        {
            System.loadLibrary("JNI");
            logger.info("Successfully loaded native library.");
        }
        catch (UnsatisfiedLinkError e)
        {
            logger.error("Failed to load library: {}", e.toString());
            throw e;
        }
    }
    private static native long freerdp_new();
    private static native void freerdp_free(long inst);
    private static native boolean freerdp_login(long inst, String s);
    private static native boolean freerdp_connect(long inst);
    private static native boolean freerdp_disconnect(long inst);
    private static native int get_freerdp_desktop_height(long inst);
    private static native int get_freerdp_desktop_width(long inst);
    private static native boolean freerdp_copy_bitmap(long inst, byte[] bitmap, int x, int y, int width, int height);
    private static native boolean freerdp_send_cursor_event(long inst, int x, int y, int flags);
    private static native boolean freerdp_send_key_event(long inst, int keycode, boolean isDown);

    public static long newInstance() { return freerdp_new(); }
    public static void freeInstance(long inst) { freerdp_free(inst); }
    public static boolean login(long inst, String s) {
        return freerdp_login(inst, s);
    }
    public static boolean connect(long inst) {
        int width = LibFreeRDP.get_width(inst);
        int height = LibFreeRDP.get_height(inst);
        UserData.bitmap = new byte[4 * width * height];
        return freerdp_connect(inst);
    }
    public static boolean disconnect(long inst) { return freerdp_disconnect(inst); }
    public static int get_height(long inst) { return get_freerdp_desktop_height(inst); }
    public static int get_width(long inst) { return get_freerdp_desktop_width(inst); }

    // require implementation
    public static boolean copy_bitmap(long inst, byte[] bitmap, int x, int y, int width, int height) {
        return freerdp_copy_bitmap(inst, bitmap, x, y, width, height);
    }


    public static boolean send_cursor_event(long inst, int x, int y, int flags)
    {
        return freerdp_send_cursor_event(inst, x, y, flags);
    }

    public static boolean send_key_event(long inst, int keycode, boolean isDown)
    {
        return freerdp_send_key_event(inst, keycode, isDown);
    }

    public static byte[] createBitmapImage() {
        try {
            int total_width = LibFreeRDP.get_width(UserData.instance);
            int total_height = LibFreeRDP.get_height(UserData.instance);
            logger.info("{}, {}", total_width, total_height);
            copy_bitmap(UserData.instance, UserData.bitmap, 0, 0, total_width, total_height);
            BufferedImage image = new BufferedImage(total_width, total_height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, total_width, total_height);
            graphics.dispose();

            int index = 0;
            for (int y = 0; y < total_height; y++) {
                for (int x = 0; x < total_width; x++) {
                    int b = UserData.bitmap[index] & 0xFF;
                    int g = UserData.bitmap[index + 1] & 0xFF;
                    int r = UserData.bitmap[index + 2] & 0xFF;
                    int pixel = (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, pixel);
                    index += 4;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height)
    {
        if(UserData.ws == null)
            return;

        logger.info("{}, {}, {}, {}, {}", inst, x, y, width, height);
        copy_bitmap(inst, UserData.bitmap, x, y, width, height);

        if(UserData.bitmap != null) {
            int total_width = LibFreeRDP.get_width(UserData.instance);
            int total_height = LibFreeRDP.get_height(UserData.instance);
            if(UserData.image == null)
                UserData.image = new BufferedImage(total_width, total_height, BufferedImage.TYPE_INT_RGB);

            int index = 0;
            for (int yy = y; yy < y + height; yy++) {
                for (int xx = x; xx < x + width; xx++) {
                    int b = UserData.bitmap[(yy*total_width + xx)*4] & 0xFF;
                    int g = UserData.bitmap[(yy*total_width + xx)*4 + 1] & 0xFF;
                    int r = UserData.bitmap[(yy*total_width + xx)*4 + 2] & 0xFF;
                    int pixel = (r << 16) | (g << 8) | b;
                    UserData.image.setRGB(xx, yy, pixel);
                    index += 4;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(UserData.image, "bmp", baos);
                byte[] bitmapData = baos.toByteArray();
                if (bitmapData != null)
                    UserData.ws.sendMessage(new BinaryMessage(bitmapData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
      private static native String freerdp_get_build_revision();

      private static native String freerdp_get_build_config();

      private static native long freerdp_new(Context context);

      private static native void freerdp_free(long inst);

      private static native boolean freerdp_parse_arguments(long inst, String[] args);

      private static native boolean freerdp_connect(long inst);

      private static native boolean freerdp_disconnect(long inst);

      private static native boolean freerdp_update_graphics(long inst, Bitmap bitmap, int x, int y,
                                                            int width, int height);

      private static native boolean freerdp_send_cursor_event(long inst, int x, int y, int flags);

      private static native boolean freerdp_send_key_event(long inst, int keycode, boolean down);

      private static native boolean freerdp_send_unicodekey_event(long inst, int keycode,
                                                                  boolean down);

      private static native boolean freerdp_send_clipboard_data(long inst, String data);

      private static native String freerdp_get_last_error_string(long inst);

      public static void setEventListener(EventListener l)
      {
          listener = l;
      }

      public static long newInstance(Context context)
      {
          return freerdp_new(context);
      }

      public static void freeInstance(long inst)
      {
          synchronized (mInstanceState)
          {
              if (mInstanceState.get(inst, false))
              {
                  freerdp_disconnect(inst);
              }
              while (mInstanceState.get(inst, false))
              {
                  try
                  {
                      mInstanceState.wait();
                  }
                  catch (InterruptedException e)
                  {
                      throw new RuntimeException();
                  }
              }
          }
          freerdp_free(inst);
      }

      public static boolean connect(long inst)
      {
          synchronized (mInstanceState)
          {
              if (mInstanceState.get(inst, false))
              {
                  throw new RuntimeException("instance already connected");
              }
          }
          return freerdp_connect(inst);
      }

      public static boolean disconnect(long inst)
      {
          synchronized (mInstanceState)
          {
              if (mInstanceState.get(inst, false))
              {
                  return freerdp_disconnect(inst);
              }
              return true;
          }
      }

      public static boolean cancelConnection(long inst)
      {
          synchronized (mInstanceState)
          {
              if (mInstanceState.get(inst, false))
              {
                  return freerdp_disconnect(inst);
              }
              return true;
          }
      }

      private static String addFlag(String name, boolean enabled)
      {
          if (enabled)
          {
              return "+" + name;
          }
          return "-" + name;
      }

      public static boolean setConnectionInfo(Context context, long inst, BookmarkBase bookmark)
      {
          BookmarkBase.ScreenSettings screenSettings = bookmark.getActiveScreenSettings();
          BookmarkBase.AdvancedSettings advanced = bookmark.getAdvancedSettings();
          BookmarkBase.DebugSettings debug = bookmark.getDebugSettings();

          String arg;
          ArrayList<String> args = new ArrayList<>();

          args.add(TAG);
          args.add("/gdi:sw");

          final String clientName = ApplicationSettingsActivity.getClientName(context);
          if (!clientName.isEmpty())
          {
              args.add("/client-hostname:" + clientName);
          }
          String certName = "";
          if (bookmark.getType() != BookmarkBase.TYPE_MANUAL)
          {
              return false;
          }

          int port = bookmark.<ManualBookmark>get().getPort();
          String hostname = bookmark.<ManualBookmark>get().getHostname();

          args.add("/v:" + hostname);
          args.add("/port:" + String.valueOf(port));

          arg = bookmark.getUsername();
          if (!arg.isEmpty())
          {
              args.add("/u:" + arg);
          }
          arg = bookmark.getDomain();
          if (!arg.isEmpty())
          {
              args.add("/d:" + arg);
          }
          arg = bookmark.getPassword();
          if (!arg.isEmpty())
          {
              args.add("/p:" + arg);
          }

          args.add(
                  String.format("/size:%dx%d", screenSettings.getWidth(), screenSettings.getHeight()));
          args.add("/bpp:" + String.valueOf(screenSettings.getColors()));

          if (advanced.getConsoleMode())
          {
              args.add("/admin");
          }

          switch (advanced.getSecurity())
          {
              case 3: // NLA
                  args.add("/sec:nla");
                  break;
              case 2: // TLS
                  args.add("/sec:tls");
                  break;
              case 1: // RDP
                  args.add("/sec:rdp");
                  break;
              default:
                  break;
          }

          if (!certName.isEmpty())
          {
              args.add("/cert-name:" + certName);
          }

          BookmarkBase.PerformanceFlags flags = bookmark.getActivePerformanceFlags();
          if (flags.getRemoteFX())
          {
              args.add("/rfx");
          }

          if (flags.getGfx())
          {
              args.add("/gfx");
          }

          if (flags.getH264() && mHasH264)
          {
              args.add("/gfx:AVC444");
          }

          args.add(addFlag("wallpaper", flags.getWallpaper()));
          args.add(addFlag("window-drag", flags.getFullWindowDrag()));
          args.add(addFlag("menu-anims", flags.getMenuAnimations()));
          args.add(addFlag("themes", flags.getTheming()));
          args.add(addFlag("fonts", flags.getFontSmoothing()));
          args.add(addFlag("aero", flags.getDesktopComposition()));

          if (!advanced.getRemoteProgram().isEmpty())
          {
              args.add("/shell:" + advanced.getRemoteProgram());
          }

          if (!advanced.getWorkDir().isEmpty())
          {
              args.add("/shell-dir:" + advanced.getWorkDir());
          }

          args.add(addFlag("async-channels", debug.getAsyncChannel()));
          args.add(addFlag("async-update", debug.getAsyncUpdate()));

          if (advanced.getRedirectSDCard())
          {
              String path = android.os.Environment.getExternalStorageDirectory().getPath();
              args.add("/drive:sdcard," + path);
          }

          args.add("/clipboard");

          // Gateway enabled?
          if (bookmark.getType() == BookmarkBase.TYPE_MANUAL &&
                  bookmark.<ManualBookmark>get().getEnableGatewaySettings())
          {
              ManualBookmark.GatewaySettings gateway =
                      bookmark.<ManualBookmark>get().getGatewaySettings();

              args.add(String.format("/g:%s:%d", gateway.getHostname(), gateway.getPort()));

              arg = gateway.getUsername();
              if (!arg.isEmpty())
              {
                  args.add("/gu:" + arg);
              }
              arg = gateway.getDomain();
              if (!arg.isEmpty())
              {
                  args.add("/gd:" + arg);
              }
              arg = gateway.getPassword();
              if (!arg.isEmpty())
              {
                  args.add("/gp:" + arg);
              }
          }

          // 0 ... local
          // 1 ... remote
          // 2 ... disable
          args.add("/audio-mode:" + String.valueOf(advanced.getRedirectSound()));
          if (advanced.getRedirectSound() == 0)
          {
              args.add("/sound");
          }

          if (advanced.getRedirectMicrophone())
          {
              args.add("/microphone");
          }

          args.add("/kbd:unicode:on");
          args.add("/cert:ignore");
          args.add("/log-level:" + debug.getDebugLevel());
          String[] arrayArgs = args.toArray(new String[0]);
          return freerdp_parse_arguments(inst, arrayArgs);
      }

      public static boolean setConnectionInfo(Context context, long inst, Uri openUri)
      {
          ArrayList<String> args = new ArrayList<>();

          // Parse URI from query string. Same key overwrite previous one
          // freerdp://user@ip:port/connect?sound=&rfx=&p=password&clipboard=%2b&themes=-

          // Now we only support Software GDI
          args.add(TAG);
          args.add("/gdi:sw");

          final String clientName = ApplicationSettingsActivity.getClientName(context);
          if (!clientName.isEmpty())
          {
              args.add("/client-hostname:" + clientName);
          }

          // Parse hostname and port. Set to 'v' argument
          String hostname = openUri.getHost();
          int port = openUri.getPort();
          if (hostname != null)
          {
              hostname = hostname + ((port == -1) ? "" : (":" + String.valueOf(port)));
              args.add("/v:" + hostname);
          }

          String user = openUri.getUserInfo();
          if (user != null)
          {
              args.add("/u:" + user);
          }

          for (String key : openUri.getQueryParameterNames())
          {
              String value = openUri.getQueryParameter(key);

              if (value.isEmpty())
              {
                  // Query: key=
                  // To freerdp argument: /key
                  args.add("/" + key);
              }
              else if (value.equals("-") || value.equals("+"))
              {
                  // Query: key=- or key=+
                  // To freerdp argument: -key or +key
                  args.add(value + key);
              }
              else
              {
                  // Query: key=value
                  // To freerdp argument: /key:value
                  if (key.equals("drive") && value.equals("sdcard"))
                  {
                      // Special for sdcard redirect
                      String path = android.os.Environment.getExternalStorageDirectory().getPath();
                      value = "sdcard," + path;
                  }

                  args.add("/" + key + ":" + value);
              }
          }

          String[] arrayArgs = args.toArray(new String[0]);
          return freerdp_parse_arguments(inst, arrayArgs);
      }

      public static boolean updateGraphics(long inst, Bitmap bitmap, int x, int y, int width,
                                           int height)
      {
          return freerdp_update_graphics(inst, bitmap, x, y, width, height);
      }

      public static boolean sendCursorEvent(long inst, int x, int y, int flags)
      {
          return freerdp_send_cursor_event(inst, x, y, flags);
      }

      public static boolean sendKeyEvent(long inst, int keycode, boolean down)
      {
          return freerdp_send_key_event(inst, keycode, down);
      }

      public static boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down)
      {
          return freerdp_send_unicodekey_event(inst, keycode, down);
      }

      public static boolean sendClipboardData(long inst, String data)
      {
          return freerdp_send_clipboard_data(inst, data);
      }

      private static void OnConnectionSuccess(long inst)
      {
          if (listener != null)
              listener.OnConnectionSuccess(inst);
          synchronized (mInstanceState)
          {
              mInstanceState.append(inst, true);
              mInstanceState.notifyAll();
          }
      }

      private static void OnConnectionFailure(long inst)
      {
          if (listener != null)
              listener.OnConnectionFailure(inst);
          synchronized (mInstanceState)
          {
              mInstanceState.remove(inst);
              mInstanceState.notifyAll();
          }
      }

      private static void OnPreConnect(long inst)
      {
          if (listener != null)
              listener.OnPreConnect(inst);
      }

      private static void OnDisconnecting(long inst)
      {
          if (listener != null)
              listener.OnDisconnecting(inst);
      }

      private static void OnDisconnected(long inst)
      {
          if (listener != null)
              listener.OnDisconnected(inst);
          synchronized (mInstanceState)
          {
              mInstanceState.remove(inst);
              mInstanceState.notifyAll();
          }
      }

      private static void OnSettingsChanged(long inst, int width, int height, int bpp)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              uiEventListener.OnSettingsChanged(width, height, bpp);
      }

      private static boolean OnAuthenticate(long inst, StringBuilder username, StringBuilder domain,
                                            StringBuilder password)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return false;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              return uiEventListener.OnAuthenticate(username, domain, password);
          return false;
      }

      private static boolean OnGatewayAuthenticate(long inst, StringBuilder username,
                                                   StringBuilder domain, StringBuilder password)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return false;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              return uiEventListener.OnGatewayAuthenticate(username, domain, password);
          return false;
      }

      private static int OnVerifyCertificateEx(long inst, String host, long port, String commonName,
                                               String subject, String issuer, String fingerprint,
                                               long flags)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return 0;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              return uiEventListener.OnVerifiyCertificateEx(host, port, commonName, subject, issuer,
                      fingerprint, flags);
          return 0;
      }

      private static int OnVerifyChangedCertificateEx(long inst, String host, long port,
                                                      String commonName, String subject,
                                                      String issuer, String fingerprint,
                                                      String oldSubject, String oldIssuer,
                                                      String oldFingerprint, long flags)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return 0;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              return uiEventListener.OnVerifyChangedCertificateEx(host, port, commonName, subject,
                      issuer, fingerprint, oldSubject,
                      oldIssuer, oldFingerprint, flags);
          return 0;
      }

      private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              uiEventListener.OnGraphicsUpdate(x, y, width, height);
      }

      private static void OnGraphicsResize(long inst, int width, int height, int bpp)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              uiEventListener.OnGraphicsResize(width, height, bpp);
      }

      private static void OnRemoteClipboardChanged(long inst, String data)
      {
          SessionState s = GlobalApp.getSession(inst);
          if (s == null)
              return;
          UIEventListener uiEventListener = s.getUIEventListener();
          if (uiEventListener != null)
              uiEventListener.OnRemoteClipboardChanged(data);
      }

    public static String getVersion()
    {
        return freerdp_get_version();
    }

    public static interface EventListener {
        void OnPreConnect(long instance);

        void OnConnectionSuccess(long instance);

        void OnConnectionFailure(long instance);

        void OnDisconnecting(long instance);

        void OnDisconnected(long instance);
    }

    public static interface UIEventListener {
        void OnSettingsChanged(int width, int height, int bpp);

        boolean OnAuthenticate(StringBuilder username, StringBuilder domain,
                               StringBuilder password);

        boolean OnGatewayAuthenticate(StringBuilder username, StringBuilder domain,
                                      StringBuilder password);

        int OnVerifiyCertificateEx(String host, long port, String commonName, String subject, String issuer,
                                   String fingerprint, long flags);

        int OnVerifyChangedCertificateEx(String host, long port, String commonName, String subject, String issuer,
                                         String fingerprint, String oldSubject, String oldIssuer,
                                         String oldFingerprint, long flags);

        void OnGraphicsUpdate(int x, int y, int width, int height);

        void OnGraphicsResize(int width, int height, int bpp);

        void OnRemoteClipboardChanged(String data);
    }
    */
}