package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class PullFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private PullToRefreshLayout mPullToRefreshLayout;
    Activity activity;


    // TODO: Rename and change types and number of parameters
    public static PullFragment newInstance(int position) {
        PullFragment fragment = new PullFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    public PullFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        // Now find the PullToRefreshLayout to setup
        /*mPullToRefreshLayout = (PullToRefreshLayout) activity.findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(activity)
                // Mark All Children as pullable
                .allChildrenArePullable()
                 // Set the OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        Toast toast = Toast.makeText(getActivity(), "This works", Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                 // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pull, container, false);

        mPullToRefreshLayout = (PullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(activity)
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        Toast toast = Toast.makeText(getActivity(), "This works", Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        return rootView;
        /*TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;*/
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


}
