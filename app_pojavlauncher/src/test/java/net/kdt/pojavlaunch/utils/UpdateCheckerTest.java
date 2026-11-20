package net.kdt.pojavlaunch.utils;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.junit.Test;

public class UpdateCheckerTest {
    @Test
    public void parsesVersions() {
        assertEquals(2000L, UpdateChecker.parseVersion("v0.2.0"));
        assertEquals(1_002_003L, UpdateChecker.parseVersion("1.2.3-debug"));
        assertEquals(-1L, UpdateChecker.parseVersion("nightly"));
        assertTrue(UpdateChecker.parseVersion("v0.10.0") > UpdateChecker.parseVersion("v0.9.9"));
    }

    private static String rel(String tag, boolean draft, String... assets) {
        StringBuilder a = new StringBuilder();
        for (String n : assets) {
            if (a.length() > 0) a.append(',');
            a.append("{\"name\":\"").append(n).append("\",\"browser_download_url\":\"https://x/").append(n).append("\"}");
        }
        return "{\"tag_name\":\"" + tag + "\",\"draft\":" + draft + ",\"assets\":[" + a + "]}";
    }

    @Test
    public void picksNewestReleaseWithApk() throws Exception {
        JSONArray arr = new JSONArray("[" + String.join(",",
                rel("v0.4.0", true, "a-debug.apk"),
                rel("v0.3.0", false, "notes.txt"),
                rel("v0.2.1", false, "PocketCraft-0.2.1-release.apk", "PocketCraft-0.2.1-debug.apk"),
                rel("v0.1.1", false, "old-debug.apk")) + "]");
        UpdateChecker.Release r = UpdateChecker.pickNewer(arr, "0.2.0", true);
        assertNotNull(r);
        assertEquals("v0.2.1", r.tag);
        assertEquals("https://x/PocketCraft-0.2.1-debug.apk", r.apkUrl);
    }

    @Test
    public void noUpdateWhenCurrentIsNewest() throws Exception {
        JSONArray arr = new JSONArray("[" + rel("v0.2.0", false, "a-debug.apk") + "," + rel("v0.1.1", false, "b-debug.apk") + "]");
        assertNull(UpdateChecker.pickNewer(arr, "0.2.0", true));
    }
}
