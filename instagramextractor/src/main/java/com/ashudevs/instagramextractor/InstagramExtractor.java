package com.ashudevs.instagramextractor;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public abstract class InstagramExtractor extends AsyncTask<Void, Integer, InstagramFile> {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36";
    Context mContext;
    String url;


    protected abstract void onExtractionComplete(InstagramFile vimeoFile);

    protected abstract void onExtractionFail(String Error);

    private String parseHtml(String url) {
        StringBuilder streamMap = new StringBuilder();
        try {
            URL getUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) getUrl.openConnection();
            BufferedReader reader = null;
            urlConnection.setRequestProperty("User-Agent", USER_AGENT);

            try {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    streamMap.append(line);
                }
            } catch (Exception E) {
                E.printStackTrace();
                if (reader != null)
                    reader.close();
                urlConnection.disconnect();
                onCancelled();
            } finally {
                if (reader != null)
                    reader.close();
                urlConnection.disconnect();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }

        return streamMap.toString();
    }


    public void Extractor(Context mContext, String url) {
        this.mContext = mContext;
        this.url = url;
        this.execute();
    }

    @Override
    protected void onPostExecute(InstagramFile instagramFile) {
        super.onPostExecute(instagramFile);

        if (instagramFile != null) {
            onExtractionComplete(instagramFile);
        } else {
            onExtractionFail("Somthing Wrong..!!");
        }
    }

    @Override
    protected InstagramFile doInBackground(Void... voids) {
        InstagramFile Ff = new InstagramFile();
        String streamMap = parseHtml(url);
        String result = "";
        String filename = "";
        try {


            Pattern metaTAGVideoSRC = Pattern.compile("<meta property=\"og:video\"(.+?)\" />");
            Matcher metaTAGVideoSRCPatternMatcher = metaTAGVideoSRC.matcher(streamMap);

            Pattern metaTAGTitle = Pattern.compile("<meta property=\"og:title\"(.+?)\" />");
            Matcher metaTAGTitleMatcher = metaTAGTitle.matcher(streamMap);

            Pattern metaTAGDescription = Pattern.compile("<meta property=\"og:description\"(.+?)\" />");
            Matcher metaTAGDescriptionMatcher = metaTAGDescription.matcher(streamMap);

            Pattern metaTAGType = Pattern.compile("<meta property=\"og:video:type\"(.+?)\" />");
            Matcher metaTAGTypeMatcher = metaTAGType.matcher(streamMap);


            if (metaTAGVideoSRCPatternMatcher.find()) {
                String metaTAG = streamMap.substring(metaTAGVideoSRCPatternMatcher.start(), metaTAGVideoSRCPatternMatcher.end());
                Pattern srcFind = Pattern.compile("content=\"(.+?)\"");
                Matcher srcFindMatcher = srcFind.matcher(metaTAG);
                if (srcFindMatcher.find()) {
                    String src = metaTAG.substring(srcFindMatcher.start(), srcFindMatcher.end()).replace("content=", "").replace("\"", "");
                    Ff.setUrl(src.replace("&amp;", "&"));

                    HttpsURLConnection openUrl = (HttpsURLConnection) new URL(src).openConnection();
                    openUrl.connect();
                    long x = openUrl.getContentLength();
                    long fileSizeInKB = x / 1024;
                    long fileSizeInMB = fileSizeInKB / 1024;
                    Ff.setSize((fileSizeInMB > 1) ? fileSizeInMB + " MB" : fileSizeInKB + " KB");
                    openUrl.disconnect();
                }
            } else {
                return null;
            }
            if (metaTAGTitleMatcher.find()) {
                String author = streamMap.substring(metaTAGTitleMatcher.start(), metaTAGTitleMatcher.end());
                Log.e("Extractor", "AUTHOR :: " + author);

                author = author.replace("<meta property=\"og:title\" content=\"", "").replace("\" />", "");

                Ff.setAuthor(author);
            } else {
                Ff.setAuthor("instadescription");
            }

            if (metaTAGDescriptionMatcher.find()) {
                String name = streamMap.substring(metaTAGDescriptionMatcher.start(), metaTAGDescriptionMatcher.end());

                Log.e("Extractor", "FILENAME :: " + name);


                name = name.replace("<meta property=\"og:description\" content=\"", "").replace("\" />", "");
                if(name.length() >50)
                {
                    Ff.setFilename(name.substring(0,50));
                }
                else {
                    Ff.setFilename(name);
                }
            } else {
                Ff.setFilename("isntadescription");
            }

            if (metaTAGTypeMatcher.find()) {
                String ext = streamMap.substring(metaTAGTypeMatcher.start(), metaTAGTypeMatcher.end());
                Log.e("Extractor", "EXT :: " + ext);

                ext = ext.replace("<meta property=\"og:video:type\" content=\"", "").replace("\" />", "").replace("video/", "");

                Ff.setExt(ext);
            } else {
                Ff.setExt("mp4");
            }

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(Ff.getUrl(), new HashMap<String, String>());
            Ff.setDuration(Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        } catch (Exception E) {

        }
        return Ff;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onExtractionFail("Somthing Wrong....!!");
    }
}
