package com.simona.healthcare.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.simona.healthcare.R;
import com.simona.healthcare.utils.Utils;

public class SettingsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private Context mContext;
    private Switch mProgramSwitch;
    private Switch mRepsSwitch;

    public static SettingsFragment newInstance(int selectedId) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        mProgramSwitch = view.findViewById(R.id.programTTSSwitch);
        mRepsSwitch = view.findViewById(R.id.repsTTSSwitch);

        mProgramSwitch.setChecked(Utils.getProgramTTS(mContext));
        mRepsSwitch.setChecked(Utils.getRepsTTS(mContext));

        mProgramSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setProgramTTS(mContext,isChecked);
            }
        });

        mRepsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setRepTTS(mContext, isChecked);
            }
        });

        return view;
    }

}
