package com.surfacetension.environfuse.betafeatures;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apptentive.android.sdk.Apptentive;
import com.surfacetension.environfuse.MainActivity;
import com.surfacetension.environfuse.R;

/**
 * Created by simonkenny on 04/05/15.
 */
public class BetaActivity extends FragmentActivity {

    private BetaControlFragment controlFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beta_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_container, new BetaContentLandingFragment())
                    .commit();
            controlFrag = new BetaControlFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.control_container, controlFrag)
                    .commit();
        }
    }



    // --- Fragments
    public static class BetaContentLandingFragment extends Fragment {

        public BetaContentLandingFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.beta_content_fragment_landing, container, false);

            final Context mContext = this.getActivity();

            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/cour.ttf");
            ((TextView)rootView.findViewById(R.id.text_app_title)).setTypeface(type);

            return rootView;
        }
    }

    public static class BetaContentExplanationFragment extends Fragment {

        public BetaContentExplanationFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.beta_content_fragment_explanation, container, false);

            final Context mContext = this.getActivity();

            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/cour.ttf");
            ((TextView)rootView.findViewById(R.id.text_explanation_title1)).setTypeface(type);
            ((TextView)rootView.findViewById(R.id.text_explanation_content1)).setTypeface(type);
            ((TextView)rootView.findViewById(R.id.text_explanation_title2)).setTypeface(type);
            ((TextView)rootView.findViewById(R.id.text_explanation_content2)).setTypeface(type);
            ((TextView)rootView.findViewById(R.id.text_explanation_title3)).setTypeface(type);
            ((TextView)rootView.findViewById(R.id.text_explanation_content3)).setTypeface(type);

            return rootView;
        }
    }

    public static class BetaControlFragment extends Fragment {

        private final int FRAG_LANDING = 0;
        private final int FRAG_EXPLANTION = 1;
        private int curFrag = FRAG_LANDING;

        public BetaControlFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.beta_control_fragment, container, false);

            final Context mContext = this.getActivity();

            ((com.gc.materialdesign.views.ButtonFlat)rootView.findViewById(R.id.button_next))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if( curFrag == FRAG_LANDING ) {
                                FragmentManager fm = getFragmentManager();
                                if (fm != null) {
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.setCustomAnimations(R.anim.enter_new, R.anim.enter_old, R.anim.pop_enter_new, R.anim.pop_enter_old);
                                    ft.replace(R.id.content_container, new BetaContentExplanationFragment());
                                    ft.commit();
                                }
                                curFrag = FRAG_EXPLANTION;
                                ((com.gc.materialdesign.views.ButtonFlat)view).setText("Okay");
                            } else if( curFrag == FRAG_EXPLANTION ) {
                                Intent intent = new Intent(mContext, MainActivity.class);
                                startActivity(intent);
                                // close this activity
                                ((Activity)mContext).finish();
                            }
                        }
                    });

            return rootView;
        }

        public void back() {
            if( curFrag == FRAG_EXPLANTION ) {
                FragmentManager fm = getFragmentManager();
                if (fm != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.exit_old, R.anim.exit_new, R.anim.pop_exit_old, R.anim.pop_exit_new);
                    ft.replace(R.id.content_container, new BetaContentLandingFragment());
                    ft.commit();
                }
                curFrag = FRAG_EXPLANTION;
                ((com.gc.materialdesign.views.ButtonFlat)getView().findViewById(R.id.button_next)).setText("Next");
                curFrag = FRAG_LANDING;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Apptentive.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Apptentive.onStop(this);
    }

    public void onBackPressed() {
        if( controlFrag != null ) {
            controlFrag.back();
        }
    }

}
