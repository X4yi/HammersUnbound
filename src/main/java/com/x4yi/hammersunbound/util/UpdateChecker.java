package com.x4yi.hammersunbound.util;

import com.x4yi.hammersunbound.HammersUnbound;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UpdateChecker {

    public static boolean updateAvailable = false;
    public static String latestVersion = HammersUnbound.VERSION;
    public static String latestUrl = "https://github.com/X4yi/HammersUnbound/releases";
    public static String latestType = "Release";

    public static class CachedRelease {
        public final String version;
        public final String body;
        public final boolean isLocal;

        public CachedRelease(String version, String body, boolean isLocal) {
            this.version = version;
            this.body = body;
            this.isLocal = isLocal;
        }
    }

    public static final List<CachedRelease> cachedReleases = new ArrayList<>();
    public static boolean hasChecked = false;
    public static String checkStatus = "";
    public static boolean isChecking = false;

    public static void loadLocalChangelog() {
        synchronized (cachedReleases) {
            for (CachedRelease release : cachedReleases) {
                if (release.isLocal && release.version.equalsIgnoreCase(HammersUnbound.VERSION)) {
                    return;
                }
            }
            try {
                String filename = "assets/hammersunbound/changelogs/" + HammersUnbound.VERSION + ".md";
                InputStream stream = UpdateChecker.class.getClassLoader().getResourceAsStream(filename);
                if (stream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    cachedReleases.add(new CachedRelease(HammersUnbound.VERSION, sb.toString(), true));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void check() {
        loadLocalChangelog();
        if (hasChecked || isChecking) return;
        isChecking = true;
        checkStatus = "Connecting...";

        new Thread(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/X4yi/HammersUnbound/releases");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "HammersUnboundMod");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JsonArray array = new JsonParser().parse(sb.toString()).getAsJsonArray();
                    String newest = HammersUnbound.VERSION;
                    String newestUrl = latestUrl;
                    String newestType = "Release";

                    synchronized (cachedReleases) {
                        List<CachedRelease> temp = new ArrayList<>();
                        for (JsonElement elem : array) {
                            JsonObject obj = elem.getAsJsonObject();
                            String tag = obj.get("tag_name").getAsString();
                            String body = obj.get("body").getAsString();
                            String releaseUrl = obj.get("html_url").getAsString();

                            temp.add(new CachedRelease(tag, body, false));

                            if (isNewer(tag, newest)) {
                                newest = tag;
                                newestUrl = releaseUrl;
                                newestType = isBeta(tag) ? "Beta" : "Release";
                            }
                        }

                        boolean localVersionInRemote = false;
                        for (CachedRelease remote : temp) {
                            if (remote.version.equalsIgnoreCase(HammersUnbound.VERSION)) {
                                localVersionInRemote = true;
                                break;
                            }
                        }

                        cachedReleases.clear();
                        cachedReleases.addAll(temp);

                        if (!localVersionInRemote) {
                            loadLocalChangelog();
                        }
                    }

                    if (!newest.equalsIgnoreCase(HammersUnbound.VERSION)) {
                        latestVersion = newest;
                        latestUrl = newestUrl;
                        latestType = newestType;
                        updateAvailable = true;
                    }
                    hasChecked = true;
                    checkStatus = "Synced!";
                } else {
                    checkStatus = "Sync Error (" + conn.getResponseCode() + ")";
                }
            } catch (Exception e) {
                e.printStackTrace();
                checkStatus = "Connection Failed";
            } finally {
                isChecking = false;
            }
        }).start();
    }

    public static boolean isBeta(String version) {
        String clean = version.toLowerCase().replaceAll("^r", "").trim();
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewer(String remote, String local) {
        if (remote == null || local == null) return false;
        if (remote.equalsIgnoreCase(local)) return false;

        String rClean = remote.toLowerCase().replaceAll("^r", "").trim();
        String lClean = local.toLowerCase().replaceAll("^r", "").trim();

        // Extract semantic parts and suffix parts
        String rSem = rClean;
        String rSuff = "";
        for (int i = 0; i < rClean.length(); i++) {
            char c = rClean.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                rSem = rClean.substring(0, i);
                rSuff = rClean.substring(i);
                break;
            }
        }

        String lSem = lClean;
        String lSuff = "";
        for (int i = 0; i < lClean.length(); i++) {
            char c = lClean.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                lSem = lClean.substring(0, i);
                lSuff = lClean.substring(i);
                break;
            }
        }

        String[] rParts = rSem.split("\\.");
        String[] lParts = lSem.split("\\.");

        int maxLength = Math.max(rParts.length, lParts.length);
        for (int i = 0; i < maxLength; i++) {
            int rVal = i < rParts.length && !rParts[i].isEmpty() ? Integer.parseInt(rParts[i]) : 0;
            int lVal = i < lParts.length && !lParts[i].isEmpty() ? Integer.parseInt(lParts[i]) : 0;

            if (rVal > lVal) return true;
            if (rVal < lVal) return false;
        }

        // Semantic parts match. Check suffix (release > beta)
        boolean rIsBeta = !rSuff.isEmpty();
        boolean lIsBeta = !lSuff.isEmpty();

        if (!rIsBeta && lIsBeta) return true;
        if (rIsBeta && !lIsBeta) return false;

        if (rIsBeta && lIsBeta) {
            return rSuff.compareTo(lSuff) > 0;
        }

        return false;
    }
}
