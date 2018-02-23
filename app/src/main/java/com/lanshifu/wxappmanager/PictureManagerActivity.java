package com.lanshifu.wxappmanager;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lanshifu.baselibrary.base.BaseActivity;
import com.lanshifu.baselibrary.log.LogHelper;
import com.lanshifu.wxappmanager.bean.Picture;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class PictureManagerActivity extends BaseActivity {


    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private BaseQuickAdapter<Picture, BaseViewHolder> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_manager;
    }

    @Override
    protected void initView() {
        setTitleText("图片查看和编辑");
        mAdapter = new BaseQuickAdapter<Picture, BaseViewHolder>(R.layout.item_picture, new ArrayList<Picture>()) {
            @Override
            protected void convert(BaseViewHolder helper, final Picture item) {

                helper.setText(R.id.tv_title,item.title);
                ImageView imageView = helper.getView(R.id.iv_pic);
                Glide.with(PictureManagerActivity.this).load(item.url).into(imageView);
                helper.getView(R.id.root).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showEditDialog(item);
                        return false;
                    }
                });
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        queryPictureList();

    }

    private void showEditDialog(final Picture item) {
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(new String[]{"修改标题", "删除"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            updataTitle(item);
                        }else if(which == 1){
                            showDeleteDialog(item);
                        }
                        dialog.dismiss();
                    }
                })
                .setTitle("操作")
                .show();
    }

    private void showDeleteDialog(Picture item) {
        item.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                showShortToast("删除成功");
                queryPictureList();
            }

            @Override
            public void onFailure(int i, String s) {
                showLongToast("删除失败 "+s);
            }
        });

    }

    private void updataTitle(final Picture item) {
        final EditText editText = new EditText(this);
        editText.setText(item.title);

        new AlertDialog.Builder(this)
                .setView(editText)
                .setTitle("修改图片标题")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        item.title = text;
                        item.update(PictureManagerActivity.this, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                showShortToast("修改成功");
                                dialog.dismiss();
                                queryPictureList();
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

    private void queryPictureList() {

        startProgressDialog();
        BmobQuery<Picture> bmobQuery = new BmobQuery<Picture>();
        bmobQuery.findObjects(this, new FindListener<Picture>() {
            @Override
            public void onSuccess(List<Picture> list) {
                mAdapter.replaceData(list);
                stopProgressDialog();

            }

            @Override
            public void onError(int i, String s) {
                showErrorToast(s);
                stopProgressDialog();
            }
        });
    }




    @OnClick(R.id.add)
    public void onViewClicked() {
        startActivity(AddPictureActivity.class);
    }
}
