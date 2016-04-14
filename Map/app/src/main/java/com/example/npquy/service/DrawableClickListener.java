package com.example.npquy.service;

/**
 * Created by npquy on 3/28/2016.
 */
public interface DrawableClickListener {
    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}
