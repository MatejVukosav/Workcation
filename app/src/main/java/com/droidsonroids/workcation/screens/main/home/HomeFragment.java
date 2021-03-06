package com.droidsonroids.workcation.screens.main.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidsonroids.workcation.R;
import com.droidsonroids.workcation.common.mvp.MvpFragment;
import com.droidsonroids.workcation.databinding.FragmentHomeBinding;
import com.droidsonroids.workcation.screens.main.MainActivity;

public class HomeFragment extends MvpFragment<HomeView, HomePresenter> implements HomeView {
    public static final String TAG = HomeFragment.class.getSimpleName();

    FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        binding = DataBindingUtil.inflate( inflater, getLayout(), container, false );
        return binding.getRoot();
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenterImpl();
    }

    @Override
    public void onBackPressed() {
        ( (MainActivity) getActivity() ).superOnBackPressed();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_home;
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onViewCreated( final View view, @Nullable final Bundle savedInstanceState ) {
        super.onViewCreated( view, savedInstanceState );
    }
}
