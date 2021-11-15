package com.example.grandinstrument.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class LeftSideBarItem {
    private TextView tv_Text;
    private ImageView iv_Icon;
    private int index;
    private String text;
    private int image;
    private Fragment fragment;
    private LinearLayout linearLayout;

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public LeftSideBarItem(TextView tv_Text, ImageView iv_Icon, int index, String text, int image, LinearLayout linearLayout, Fragment fragment) {
        this.tv_Text = tv_Text;
        this.iv_Icon = iv_Icon;
        this.index = index;
        this.text = text;
        this.image = image;
        this.fragment = fragment;
        this.linearLayout = linearLayout;
    }

    public TextView getTv_Text() {
        return tv_Text;
    }

    public void setTv_Text(TextView tv_Text) {
        this.tv_Text = tv_Text;
    }

    public ImageView getIv_Icon() {
        return iv_Icon;
    }

    public void setIv_Icon(ImageView iv_Icon) {
        this.iv_Icon = iv_Icon;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
