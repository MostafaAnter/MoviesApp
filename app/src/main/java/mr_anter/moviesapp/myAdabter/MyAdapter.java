package mr_anter.moviesapp.myAdabter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mr_anter.moviesapp.R;
import mr_anter.moviesapp.activities.DetailsActivity;
import mr_anter.moviesapp.activities.MainActivity;
import mr_anter.moviesapp.fragments.DetailsFragment;
import mr_anter.moviesapp.fragments.ItemsFragment;
import mr_anter.moviesapp.models.FavoriteModel;
import mr_anter.moviesapp.models.MoviesPojo;
import mr_anter.moviesapp.store.FavoriteStore;
import mr_anter.moviesapp.utils.SquaredImageView;


/**
 * Created by mostafa on 11/03/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;
    private List<MoviesPojo> mDataSet;

    // belong like button animations
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();
    // put control on one item selected
    private int lastCheckedPosition = -1;



    /**
     * Initialize the constructor of the Adapter.
     *
     * @param mDataSet String[] containing the data to populate views to be used by RecyclerView.
     * @param mContext Context hold context
     */
    public MyAdapter(Context mContext, List<MoviesPojo> mDataSet) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }
    /**
     * Provide a reference to the type of views (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mainTitel;
        private final TextView timeStamp;
        private final TextView textStatusMsg;
        private final SquaredImageView imageView;

        private final Button favorite;
        private final ProgressBar mProgress;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    if (MainActivity.mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(DetailsFragment.ARG_ITEM_ID, getPosition() + "");
                        DetailsFragment fragment = new DetailsFragment();
                        fragment.setArguments(arguments);
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(DetailsFragment.ARG_ITEM_ID, getPosition());

                        context.startActivity(intent);
                    }
                }
            });
            mainTitel = (TextView) v.findViewById(R.id.main_title);
            timeStamp = (TextView) v.findViewById(R.id.timestamp);
            textStatusMsg = (TextView) v.findViewById(R.id.txtStatusMsg);
            imageView = (SquaredImageView) v.findViewById(R.id.feedImage1);
            favorite = (Button) v.findViewById(R.id.favorite_button);
            mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        public TextView getMainTitel() {
            return mainTitel;
        }

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getTextStatusMsg() {
            return textStatusMsg;
        }

        public SquaredImageView getImageView() {
            return imageView;
        }


        public Button getFavorite() {
            return favorite;
        }

        public ProgressBar getProgressBar(){
            return mProgress;
        }

    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_forecast, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getMainTitel().setText(mDataSet.get(position).getOriginal_title());
        viewHolder.getTimeStamp().setText(mDataSet.get(position).getRelease_date());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(mDataSet.get(position).getOverview())) {
            viewHolder.getTextStatusMsg().setText(mDataSet.get(position).getOverview());
            viewHolder.getTextStatusMsg().setVisibility(View.VISIBLE);
            if (ItemsFragment.type == 1) {
                viewHolder.getTextStatusMsg().setVisibility(View.GONE);
            }

        } else {
            // status is empty, remove from view
            viewHolder.getTextStatusMsg().setVisibility(View.GONE);
        }


        // Feed image
        if (mDataSet.get(position).getPoster_path() != null) {
            // show progressBar
            viewHolder.getProgressBar().setVisibility(View.VISIBLE);
            // Adapter re-use is automatically detected and the previous download canceled.
            Picasso.with(mContext).load(mDataSet.get(position).getPoster_path())
                    .placeholder(R.drawable.rectangle)
                    .into(viewHolder.getImageView(), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (viewHolder.getProgressBar() != null) {
                                viewHolder.getProgressBar().setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
        else {
            viewHolder.getImageView().setVisibility(View.GONE);
        }

        // like button
        viewHolder.getFavorite().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastCheckedPosition = position;
                notifyItemRangeChanged(0, mDataSet.size());

                if (!likedPositions.contains(position)) {
                    likedPositions.add(position);
                    updateHeartButton(viewHolder, true);
                }
                // add to my database
                addItem(position);

            }
        });
        if(position == lastCheckedPosition){
            viewHolder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_24dp);
        }else if (new FavoriteStore(mContext).findItem(mDataSet.get(position).getId(),
                mDataSet.get(position).getOriginal_title())){
            // this item is in my database
            viewHolder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_24dp);
        }else {
            viewHolder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_outline_24dp);
        }



    }

    private void addItem(int position) {
        //add item to favorite
        FavoriteModel item = new FavoriteModel();
        item.setTitleKey(mDataSet.get(position).getOriginal_title());
        item.setIdValue(mDataSet.get(position).getId());
        new FavoriteStore(mContext).update(item);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // like button effect
    private void updateHeartButton(final ViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.getFavorite(), "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_24dp);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // do something
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_24dp);
            } else {
                holder.getFavorite().setBackgroundResource(R.drawable.ic_favorite_outline_24dp);            }
        }
    }



}
