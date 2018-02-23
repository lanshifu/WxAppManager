package com.lanshifu.wxappmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.wxappmanager.bean.Picture;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPictureActivity extends BaseActivity {


    private static final int REQUEST_CODE_CHOOSE_PIC = 100;
    @Bind(R.id.editText)
    EditText mEditText;
    @Bind(R.id.btn_selectPic)
    Button mBtnSelectPic;
    @Bind(R.id.iv_preview)
    ImageView mIvPreview;
    @Bind(R.id.btn_commit)
    Button mBtnCommit;
    private String mUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_picture;
    }

    @Override
    protected void initView() {
        setTitleText("上传照片");

    }


    @OnClick({R.id.btn_selectPic, R.id.iv_preview, R.id.btn_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_selectPic:
                selectPic();
                break;
            case R.id.iv_preview:
                break;
            case R.id.btn_commit:
                commit();
                break;
        }
    }

    private void commit() {
        if (TextUtils.isEmpty(mEditText.getText().toString())){
            showShortToast("图标标题不能为空");
            return;
        }
        if (mUrl == null){
            showShortToast("请选择图片");
            return;
        }

        Picture picture = new Picture()


    }

    private void selectPic() {
        Matisse.from((BaseActivity) mContext)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .capture(true)//是否可以拍照
                .captureStrategy(new CaptureStrategy(true, "com.lanshifu.wxappmanager.fileprovider"))
                .countable(true)//是否显示数字
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f) ////图片缩放比例
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE_PIC);
    }

    private List<String> mSelectedPic;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_CHOOSE_PIC){
                mSelectedPic = Matisse.obtainPathResult(data);
                if (mSelectedPic.size() == 1){
                    mUrl = mSelectedPic.get(0);
                    Glide.with(this).load(mUrl).into(mIvPreview);
                }
            }
        }

    }

}
