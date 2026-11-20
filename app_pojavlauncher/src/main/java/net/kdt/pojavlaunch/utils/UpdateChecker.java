package net.kdt.pojavlaunch.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import net.kdt.pojavlaunch.BuildConfig;
import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks GitHub releases of this repository for a newer APK.
 * When one exists, asks the user, downloads it and opens the system installer.
 */
public final class UpdateChecker {
    private static final String TAG = "UpdateChecker";
    public static final String RELEASES_API =
            "https://api.github.com/repos/meow67676767/mc-launcher./releases?per_page=10";
    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
    private static boolean sCheckedThisRun = false;
    /** Downloaded APK that waits for the "install unknown apps" permission. */
    private static File sPendingApk = null;

    private UpdateChecker() {}

    /** Holds one newer release found on GitHub. */
    static final class Release {
        final String tag, name, notes, apkUrl;
        Release(String tag, String name, String notes, String apkUrl) {
            this.tag = tag; this.name = name; this.notes = notes; this.apkUrl = apkUrl;
        }
    }

    /** Converts "v0.2.0" or "0.2.0-debug" to a comparable number, or -1. */
    static long parseVersion(String s) {
        if (s == null) return -1;
        Matcher m = VERSION.matcher(s);
        if (!m.find()) return -1;
        return Long.parseLong(m.group(1)) * 1_000_000L
                + Long.parseLong(m.group(2)) * 1_000L
                + Long.parseLong(m.group(3));
    }

    /** Picks the newest release that is newer than currentVersion and has an APK. */
    static Release pickNewer(JSONArray releases, String currentVersion, boolean preferDebug) {
        long current = parseVersion(currentVersion);
        Release best = null;
        long bestVer = current;
        for (int i = 0; i < releases.length(); i++) {
            JSONObject r = releases.optJSONObject(i);
            if (r == null || r.optBoolean("draft")) continue;
            String tag = r.optString("tag_name");
            long ver = parseVersion(tag);
            if (ver <= bestVer) continue;
            JSONArray assets = r.optJSONArray("assets");
            if (assets == null) continue;
            String apk = null;
            for (int j = 0; j < assets.length(); j++) {
                JSONObject a = assets.optJSONObject(j);
                String n = a == null ? "" : a.optString("name").toLowerCase();
                if (!n.endsWith(".apk")) continue;
                boolean isDebug = n.contains("debug");
                if (apk == null || isDebug == preferDebug) apk = a.optString("browser_download_url");
            }
            if (apk == null) continue;
            best = new Release(tag, r.optString("name", tag), r.optString("body", ""), apk);
            bestVer = ver;
        }
        return best;
    }

    /** Runs the check. With manual=false, runs at most once per app start and stays silent on errors. */
    public static void check(Activity activity, boolean manual) {
        if (!manual) {
            if (sCheckedThisRun) return;
            sCheckedThisRun = true;
        } else {
            Toast.makeText(activity, R.string.update_checking, Toast.LENGTH_SHORT).show();
        }
        PojavApplication.sExecutorService.execute(() -> {
            try {
                String json = DownloadUtils.downloadString(RELEASES_API);
                Release rel = pickNewer(new JSONArray(json), BuildConfig.VERSION_NAME, BuildConfig.DEBUG);
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing()) return;
                    if (rel != null) showDialog(activity, rel);
                    else if (manual) Toast.makeText(activity, R.string.update_none, Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.w(TAG, "Update check failed", e);
                if (manual) activity.runOnUiThread(() ->
                        Toast.makeText(activity, R.string.update_failed, Toast.LENGTH_SHORT).show());
            }
        });
    }

    /** Call from onResume: continues the install after the user allowed unknown apps. */
    public static void resumePendingInstall(Activity activity) {
        File apk = sPendingApk;
        if (apk == null || !apk.isFile()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !activity.getPackageManager().canRequestPackageInstalls()) return;
        install(activity, apk);
    }

    private static void showDialog(Activity activity, Release rel) {
        String notes = rel.notes.length() > 600 ? rel.notes.substring(0, 600) + "…" : rel.notes;
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.update_available_title, rel.tag))
                .setMessage(activity.getString(R.string.update_available_message,
                        BuildConfig.VERSION_NAME, rel.name, notes))
                .setPositiveButton(R.string.update_install, (d, w) -> download(activity, rel))
                .setNegativeButton(R.string.update_later, null)
                .show();
    }

    @SuppressWarnings("deprecation")
    private static void download(Activity activity, Release rel) {
        ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle(R.string.update_downloading);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setMax(100);
        progress.setCancelable(false);
        progress.show();
        File dir = new File(activity.getCacheDir(), "updates");
        File out = new File(dir, "update.apk");
        PojavApplication.sExecutorService.execute(() -> {
            try {
                if (!dir.isDirectory() && !dir.mkdirs()) throw new java.io.IOException("Cannot create " + dir);
                File part = new File(dir, "update.apk.part");
                HttpURLConnection conn = (HttpURLConnection) new URL(rel.apkUrl).openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", DownloadUtils.USER_AGENT);
                int code = conn.getResponseCode();
                if (code != 200) throw new java.io.IOException("HTTP " + code);
                long total = conn.getContentLengthLong();
                try (InputStream in = conn.getInputStream(); OutputStream os = new FileOutputStream(part)) {
                    byte[] buf = new byte[64 * 1024];
                    long done = 0; int n, lastPct = -1;
                    while ((n = in.read(buf)) > 0) {
                        os.write(buf, 0, n);
                        done += n;
                        if (total > 0) {
                            int pct = (int) (done * 100 / total);
                            if (pct != lastPct) { lastPct = pct; activity.runOnUiThread(() -> progress.setProgress(pct)); }
                        }
                    }
                }
                if (total > 0 && part.length() != total) throw new java.io.IOException("Incomplete download");
                if (out.exists() && !out.delete()) throw new java.io.IOException("Cannot replace " + out);
                if (!part.renameTo(out)) throw new java.io.IOException("Cannot rename download");
                activity.runOnUiThread(() -> { progress.dismiss(); install(activity, out); });
            } catch (Exception e) {
                Log.w(TAG, "Update download failed", e);
                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(activity, R.string.update_failed, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private static void install(Activity activity, File apk) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !activity.getPackageManager().canRequestPackageInstalls()) {
            sPendingApk = apk;
            Toast.makeText(activity, R.string.update_allow_unknown_sources, Toast.LENGTH_LONG).show();
            activity.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + activity.getPackageName())));
            return;
        }
        sPendingApk = null;
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".updates", apk);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
