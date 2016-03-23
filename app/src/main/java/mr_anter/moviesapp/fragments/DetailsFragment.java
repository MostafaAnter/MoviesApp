package mr_anter.moviesapp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import mr_anter.moviesapp.R;
import mr_anter.moviesapp.models.MoviesPojo;
import mr_anter.moviesapp.utils.SquaredImageView;

/**
 * Created by mostafa on 08/03/16.
 */
public class DetailsFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private static MoviesPojo moviesPojo;
    public DetailsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            moviesPojo = (MoviesPojo)getArguments().getSerializable(ARG_ITEM_ID);


        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        TextView tv = (TextView) view.findViewById(R.id.original_title);
        TextView tv1 = (TextView) view.findViewById(R.id.release_date);
        TextView tv3 = (TextView) view.findViewById(R.id.overview);
        SquaredImageView imageView = (SquaredImageView) view.findViewById(R.id.feedImage1);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);

        RatingBar ratingbar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingbar.setRating(Float.parseFloat(moviesPojo.getVote_average())/2);

        tv.setText(moviesPojo.getOriginal_title());
        tv1.setText(moviesPojo.getRelease_date());
        tv3.setText(moviesPojo.getOverview());


        // Feed image
        if (moviesPojo.getBackdrop_path() != null) {
            // show progressBar
            pb.setVisibility(View.VISIBLE);
            // Adapter re-use is automatically detected and the previous download canceled.
            Picasso.with(getActivity()).load(moviesPojo.getBackdrop_path())
                    .placeholder(R.drawable.rectangle)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (pb != null) {
                                pb.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "There is no trial video to show :(", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return view;
    }
}
