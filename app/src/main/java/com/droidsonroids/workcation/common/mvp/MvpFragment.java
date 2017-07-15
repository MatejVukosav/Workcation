package com.droidsonroids.workcation.common.mvp;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

public abstract class MvpFragment<
        V extends MvpView,
        P extends MvpPresenter> extends Fragment implements MvpView {

    protected V view;
    protected P presenter;

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
        if ( presenter == null )
            presenter = createPresenter();
        //noinspection unchecked
        presenter.attachView( this );
    }

    protected abstract P createPresenter();

    public abstract void onBackPressed();

    @LayoutRes
    public abstract int getLayout();

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }
}
