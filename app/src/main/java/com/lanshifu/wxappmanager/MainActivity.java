package com.lanshifu.wxappmanager;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.wxappmanager.bean.Notice;

import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        hideBackIcon();
        setTitleText(getResources().getString(R.string.app_name));
        Bmob.initialize(this,"cf06c95434e309a4a8932ec7c5d10f0e");

    }


    @OnClick({R.id.notice, R.id.picture, R.id.contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notice:
                queryOldNotice();
                break;
            case R.id.picture:
                startActivity(PictureManagerActivity.class);
                break;
            case R.id.contact:
                break;
        }
    }


    private void queryOldNotice() {
        startProgressDialog();
        BmobQuery<Notice> bmobQuery = new BmobQuery<Notice>();
        bmobQuery.getObject(this, "ETKX1119", new GetListener<Notice>() {
            @Override
            public void onSuccess(Notice notice) {
                stopProgressDialog();
                showEdittextNoticeDialog(notice);

            }



            @Override
            public void onFailure(int i, String s) {
                stopProgressDialog();
                LogHelper.e(s);
                showLongToast(s);
            }
        });


    }

    private void showEdittextNoticeDialog(final Notice notice) {

        final EditText editText = new EditText(this);
        editText.setText(notice.getTitle());

        new AlertDialog.Builder(this)
                .setView(editText)
                .setTitle("编辑广告语")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        notice.setTitle(text);
                        notice.update(MainActivity.this, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                showShortToast("修改成功");
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                showLongToast("修改失败 "+s);
                                LogHelper.e("lxb ->"+s);
                            }
                        });

                    }
                }).setNegativeButton("取消",null)
                .show();

    }
}
