// Generated by view binder compiler. Do not edit!
package roberto.garzone.youtubereviews.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import roberto.garzone.youtubereviews.R;

public final class ReviewsListLayoutBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ListView reviewsList;

  @NonNull
  public final Button reviewsListBackButton;

  @NonNull
  public final ConstraintLayout reviewsListLayout;

  @NonNull
  public final RatingBar reviewsListRating;

  @NonNull
  public final TextView reviewsListRatingText;

  @NonNull
  public final TextView reviewsListSongName;

  @NonNull
  public final Toolbar reviewsListToolbar;

  private ReviewsListLayoutBinding(@NonNull ConstraintLayout rootView,
      @NonNull ListView reviewsList, @NonNull Button reviewsListBackButton,
      @NonNull ConstraintLayout reviewsListLayout, @NonNull RatingBar reviewsListRating,
      @NonNull TextView reviewsListRatingText, @NonNull TextView reviewsListSongName,
      @NonNull Toolbar reviewsListToolbar) {
    this.rootView = rootView;
    this.reviewsList = reviewsList;
    this.reviewsListBackButton = reviewsListBackButton;
    this.reviewsListLayout = reviewsListLayout;
    this.reviewsListRating = reviewsListRating;
    this.reviewsListRatingText = reviewsListRatingText;
    this.reviewsListSongName = reviewsListSongName;
    this.reviewsListToolbar = reviewsListToolbar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ReviewsListLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ReviewsListLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.reviews_list_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ReviewsListLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.reviews_list;
      ListView reviewsList = ViewBindings.findChildViewById(rootView, id);
      if (reviewsList == null) {
        break missingId;
      }

      id = R.id.reviews_list_back_button;
      Button reviewsListBackButton = ViewBindings.findChildViewById(rootView, id);
      if (reviewsListBackButton == null) {
        break missingId;
      }

      ConstraintLayout reviewsListLayout = (ConstraintLayout) rootView;

      id = R.id.reviews_list_rating;
      RatingBar reviewsListRating = ViewBindings.findChildViewById(rootView, id);
      if (reviewsListRating == null) {
        break missingId;
      }

      id = R.id.reviews_list_rating_text;
      TextView reviewsListRatingText = ViewBindings.findChildViewById(rootView, id);
      if (reviewsListRatingText == null) {
        break missingId;
      }

      id = R.id.reviews_list_song_name;
      TextView reviewsListSongName = ViewBindings.findChildViewById(rootView, id);
      if (reviewsListSongName == null) {
        break missingId;
      }

      id = R.id.reviews_list_toolbar;
      Toolbar reviewsListToolbar = ViewBindings.findChildViewById(rootView, id);
      if (reviewsListToolbar == null) {
        break missingId;
      }

      return new ReviewsListLayoutBinding((ConstraintLayout) rootView, reviewsList,
          reviewsListBackButton, reviewsListLayout, reviewsListRating, reviewsListRatingText,
          reviewsListSongName, reviewsListToolbar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
