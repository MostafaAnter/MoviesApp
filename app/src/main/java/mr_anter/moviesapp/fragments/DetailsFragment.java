package mr_anter.moviesapp.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mr_anter.moviesapp.R;
import mr_anter.moviesapp.app.AppController;
import mr_anter.moviesapp.constants.Constants;
import mr_anter.moviesapp.models.FavoriteModel;
import mr_anter.moviesapp.models.MoviesPojo;
import mr_anter.moviesapp.models.ReviewModel;
import mr_anter.moviesapp.myAdabter.ReviewsAdapter;
import mr_anter.moviesapp.myAdabter.TrailersAdapter;
import mr_anter.moviesapp.parser.JsonParser;
import mr_anter.moviesapp.store.FavoriteStore;
import mr_anter.moviesapp.utils.SquaredImageView;

/**
 * Created by mostafa on 08/03/16.
 */
public class DetailsFragment extends Fragment  implements View.OnClickListener {
    public static final String ARG_ITEM_ID = "item_id";
    private static MoviesPojo moviesPojo;
    private static String movie_trial_id;


    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 3;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.favorite_button:
                if (!new FavoriteStore(getActivity()).findItem(moviesPojo.getId(),
                        moviesPojo.getOriginal_title())){
                    // this item is in my database
                    addItemToFav();
                    favoriteImage.setBackgroundResource(R.drawable.ic_favorite_24dp);
                }else {
                    removeItemFromFav();
                    favoriteImage.setBackgroundResource(R.drawable.ic_favorite_outline_24dp);
                }
                break;
        }
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected ReviewsAdapter mAdapter;
    protected List<ReviewModel> mDataset;
    protected RecyclerView.LayoutManager mLayoutManager;

    // for trailers
    protected RecyclerView mTrailersRecyclerView;
    protected TrailersAdapter mTrailersAdapter;
    protected List<String> mTrailersIDsSet;
    public DetailsFragment(){

    }

    private ImageView favoriteImage;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize dataSet
        mDataset = new ArrayList<>();
        mTrailersIDsSet = new ArrayList<>();

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

        // populate recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // initialize adapter
        mAdapter = new ReviewsAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        // populate trailers recycler view
        mTrailersRecyclerView = (RecyclerView) view.findViewById(R.id.trailersRecyclerView);
        mTrailersRecyclerView.setHasFixedSize(true);
        // initialize adapter
        mTrailersAdapter = new TrailersAdapter(getActivity(), mTrailersIDsSet);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);

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


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
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
                if (movie_trial_id == null)
                Snackbar.make(view, "There is no trial video to show :(", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                watchYoutubeVideo(movie_trial_id);
            }
        });

        // initiate favorite image
        favoriteImage = (ImageView) view.findViewById(R.id.favorite_button);
        favoriteImage.setOnClickListener(this);
        // check if movie in favorite
        if (new FavoriteStore(getActivity()).findItem(moviesPojo.getId(),
                moviesPojo.getOriginal_title())){
            // this item is in my database
            favoriteImage.setBackgroundResource(R.drawable.ic_favorite_24dp);
        }else {
            favoriteImage.setBackgroundResource(R.drawable.ic_favorite_outline_24dp);
        }




        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // get movie trial id
        initiateReviewAndTrial(0, moviesPojo.getId());
        // get movie reviews id
        initiateReviewAndTrial(1, moviesPojo.getId());
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        if (mTrailersRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mTrailersRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);

        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailersRecyclerView.scrollToPosition(scrollPosition);
    }

    // to call youtube provider :) by intent
    public void watchYoutubeVideo(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

    private void initiateReviewAndTrial(final int index, String idOfMovie) {
        String Url = "";
        switch (index){
            case 0:
                Url = Constants.VEDIO_TERIAL_REVIEW + idOfMovie + Constants.VEDIO_TERIAL;
                break;
            case 1:
                Url = Constants.VEDIO_TERIAL_REVIEW + idOfMovie + Constants.VEDIO_REVIEW;
                break;
            default:
                Url = Constants.VEDIO_TERIAL_REVIEW + idOfMovie + Constants.VEDIO_TERIAL;
        }
        /**
         * Execute the background task, which uses {@link AsyncTask} to load the data.
         */
        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(Url);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                if(index == 0){
                    // retrieve trial id
                    movie_trial_id = JsonParser.parseTrailer(data).get(0);
                    // manipulate new lis
                    if (mTrailersIDsSet != null){
                        mTrailersIDsSet.clear();
                        mTrailersAdapter.notifyDataSetChanged();
                    }
                    Iterator iterator = JsonParser.parseTrailer(data).iterator();
                    while (iterator.hasNext()){
                        String trailerID = (String) iterator.next();
                        mTrailersIDsSet.add(trailerID);
                        mTrailersAdapter.notifyItemInserted(mTrailersIDsSet.size() - 1);
                    }


                }else {
                    // retrieve review
                    clearDataSet();
                    Iterator iterator = JsonParser.parseJsonReview(data).iterator();
                    while (iterator.hasNext()){
                        ReviewModel reviewModel = (ReviewModel)iterator.next();
                        mDataset.add(reviewModel);
                        mAdapter.notifyItemInserted(mDataset.size() - 1);
                    }

                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(index == 0){
                    // retrieve trial id
                    movie_trial_id = JsonParser.parseTrailer(response).get(0);
                    // manipulate new list
                    // manipulate new lis
                    if (mTrailersIDsSet != null){
                        mTrailersIDsSet.clear();
                        mTrailersAdapter.notifyDataSetChanged();
                    }
                    Iterator iterator = JsonParser.parseTrailer(response).iterator();
                    while (iterator.hasNext()){
                        String trailerID = (String) iterator.next();
                        mTrailersIDsSet.add(trailerID);
                        mTrailersAdapter.notifyItemInserted(mTrailersIDsSet.size() - 1);
                    }


                }else {
                    // retrieve review
                    clearDataSet();
                    Iterator iterator = JsonParser.parseJsonReview(response).iterator();
                    while (iterator.hasNext()){
                        ReviewModel reviewModel = (ReviewModel)iterator.next();
                        mDataset.add(reviewModel);
                        mAdapter.notifyItemInserted(mDataset.size() - 1);
                    }

                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(strReq);

    }

    private void clearDataSet() {
        if (mDataset != null){
            mDataset.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void addItemToFav() {
        //add item to favorite
        FavoriteModel item = new FavoriteModel();
        item.setTitleKey(moviesPojo.getOriginal_title());
        item.setIdValue(moviesPojo.getId());
        new FavoriteStore(getActivity()).update(item);
    }

    private void removeItemFromFav(){
        FavoriteModel item = new FavoriteModel();
        item.setTitleKey(moviesPojo.getOriginal_title());
        item.setIdValue(moviesPojo.getId());
        new FavoriteStore(getActivity()).remove(item);
    }
}
