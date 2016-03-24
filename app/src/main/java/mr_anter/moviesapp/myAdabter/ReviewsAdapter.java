package mr_anter.moviesapp.myAdabter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mr_anter.moviesapp.R;
import mr_anter.moviesapp.models.ReviewModel;


/**
 * Created by mostafa on 11/03/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<ReviewModel> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.author) TextView author;
        @Bind(R.id.txtUrl) TextView url;
        @Bind(R.id.txtReviewMsg) TextView content;

        public TextView getAuthor() {
            return author;
        }

        public TextView getUrl() {
            return url;
        }

        public TextView getContent() {
            return content;
        }

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });
            ButterKnife.bind(this, v);
        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public ReviewsAdapter(List<ReviewModel> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.reviews_item_forecast, viewGroup, false);

        return new ViewHolder(v);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if (mDataSet.get(position) != null) {
            viewHolder.getAuthor().setText(mDataSet.get(position).getAuthor());
            // Checking for null feed url
            if (mDataSet.get(position).getUrl() != null) {
                viewHolder.getUrl().setText(Html.fromHtml("<a href=\"" + mDataSet.get(position).getUrl() + "\">"
                        + mDataSet.get(position).getUrl() + "</a> "));

                // Making url clickable
                viewHolder.getUrl().setMovementMethod(LinkMovementMethod.getInstance());
                viewHolder.getUrl().setVisibility(View.VISIBLE);
            } else {
                // url is null, remove from the view
                viewHolder.getUrl().setVisibility(View.GONE);
            }

            //viewHolder.getUrl().setText(mDataSet.get(position).getUrl());
            viewHolder.getContent().setText(mDataSet.get(position).getContent());
            Log.d("test", mDataSet.get(position).getContent());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}