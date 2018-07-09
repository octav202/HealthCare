package com.simona.healthcare.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.simona.healthcare.R;
import com.simona.healthcare.playing.PlayBarFragment;
import com.simona.healthcare.utils.Utils;

public class SettingsFragment extends Fragment {

    private static final String TAG = "HEALTH_";

    private Context mContext;
    private Switch mProgramSwitch;
    private Switch mRepsSwitch;
    private Switch mBreakSwitch;
    private Spinner mLangSpinner;
    private SeekBar mPitchBar;

    public static SettingsFragment newInstance() {
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
        mBreakSwitch = view.findViewById(R.id.breakTTSSwitch);

        mProgramSwitch.setChecked(Utils.getProgramTTS(mContext));
        mRepsSwitch.setChecked(Utils.getRepsTTS(mContext));
        mBreakSwitch.setChecked(Utils.getBreakTTS(mContext));

        mLangSpinner = view.findViewById(R.id.langSpinner);
        mPitchBar = view.findViewById(R.id.pitchBar);

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

        mBreakSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setBreakTTS(mContext, isChecked);
            }
        });


        // Language Spinner Adapter
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.languages, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        mLangSpinner.setAdapter(adapter);

        // Display stored language
        String lang = Utils.getLanguageTTS(mContext);
        String[] languages = getResources().getStringArray(R.array.languages);
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(lang)) {
                mLangSpinner.setSelection(i);
                break;
            }
        }

        mLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = adapter.getItem(position).toString();

                // Store Language
                Utils.setLanguageTTS(mContext, language);

                // Apply language
                PlayBarFragment.getInstance().setLanguage(Utils.stringToLocale(language));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Pitch Bar
        int pitch = Math.round(Utils.getPitchTTS(mContext) * 10);
        mPitchBar.setProgress(Integer.valueOf(pitch));
        mPitchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Store Pitch
                Utils.setPitchTTS(mContext, progress);

                // Apply Pitch
                PlayBarFragment.getInstance().setPitch(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

}
