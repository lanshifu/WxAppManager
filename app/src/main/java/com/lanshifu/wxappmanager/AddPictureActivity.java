package com.lanshifu.wxappmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.wxappmanager.bean.Picture;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
        new RxPermissions(this)
                .request(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {

                    }
                });

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

        compressPicture();


    }

    private void compressPicture() {
        Luban.with(mContext)
                .load(mSelectedPic)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        showShortToast("压缩成功");
                        uploadPicture(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showShortToast("图片压缩失败");
                    LogHelper.e("lxb ->"+e.getMessage());
                    }
                }).launch();
    }


    private void uploadPicture(File file) {

        final BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                String url = bmobFile.getFileUrl(AddPictureActivity.this);

                Picture picture = new Picture();
                picture.title = mEditText.getText().toString();
                picture.url = url;
                picture.save(AddPictureActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        LogHelper.d("lxb ->提交成功");
                        showShortToast("提交成功");
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        showShortToast("提交失败 "+s);
                        LogHelper.e("lxb ->提交失败 "+s);
                    }
                });

            }

            @Override
            public void onFailure(int i, String s) {
                showShortToast("上传图片失败 "+s);
            }
        });
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
