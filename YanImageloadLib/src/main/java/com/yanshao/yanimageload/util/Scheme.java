package com.yanshao.yanimageload.util;

import android.util.Log;
import android.util.Patterns;

import java.util.Locale;

public enum Scheme {
    HTTP("http"), FILE("file"),PATH("path"),  CONTENT("content"), ASSETS("assets"), DRAWABLE("drawable"), UNKNOWN("");

    private String scheme;
    private String uriPrefix;

    Scheme(String scheme) {
        this.scheme = scheme;


    }

    /*  */

    /**
     * Defines scheme of incoming URI
     *
     * @param uri URI for scheme detection
     * @return Scheme of incoming URI
     */
    public static Scheme ofUri(String uri) {
        Scheme str = UNKNOWN;
        if (Patterns.WEB_URL.matcher(uri).matches()) {
            str = HTTP;
        }
        if (uri.substring(0, 4).equals(FILE.scheme)) {

            str = FILE;
        }
        if (uri.substring(0, 7).equals(CONTENT.scheme)) {

            str = CONTENT;
        }
        if (uri.substring(0, 6).equals(ASSETS.scheme)) {

            str = ASSETS;
        }
        if (uri.substring(0, 8).equals(DRAWABLE.scheme)) {

            str = DRAWABLE;
        }
        if (uri.substring(0, 1).equals("/")&&(uri.substring(uri.length()-4, uri.length()).equals(".jpg")||uri.substring(uri.length()-4, uri.length()).equals(".png"))) {

            str = PATH;
        }
        return str;
    }

 /*   public boolean belongsTo(String uri)
    {
        return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
    }

    *//**
     * Appends scheme to incoming path
     *//*
    public String wrap(String path)
    {
        return uriPrefix + path;
    }

    *//**
     * Removed scheme part ("scheme://") from incoming URI
     *//*
    public String crop(String uri)
    {
        if (!belongsTo(uri))
        {
            throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
        }
        return uri.substring(uriPrefix.length());
    }*/
}