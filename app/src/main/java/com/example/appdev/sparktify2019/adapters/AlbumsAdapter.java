package com.example.appdev.sparktify2019.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appdev.sparktify2019.R;
import com.example.appdev.sparktify2019.activities.GenresActivity;
import com.example.appdev.sparktify2019.activities.SinglePlayerGameActivity;
import com.example.appdev.sparktify2019.pojos.Album;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;



public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {
    private final GenresActivity genresActivity;
    private Context mContext;
    private List<Album> albumList;
    static int totalSongs = 0;
    private static final String TAG = "AlbumsAdapter";
    FirebaseFirestore db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, songID;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            thumbnail =  view.findViewById(R.id.thumbnail);
            songID =  view.findViewById(R.id.song_ID);

            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    genresActivity.showProgressDialog("...");
                    // Generate Random songs
                    final String queryLink = "list_of_songs/genres/" + songID.getText().toString();
                    // Get total songs
                    db = FirebaseFirestore.getInstance();
                    db.collection(queryLink).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                totalSongs = task.getResult().size();

                                // Random total numbers of song
                                int randomIndex = (int) Math.floor(Math.random() * totalSongs) + 1;

                                // Start query using the songNumber
                                CollectionReference citiesRef = db.collection(queryLink);
                                Query query = citiesRef.whereEqualTo("songNumber", randomIndex);

                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() == 1) {
                                                // We only need 1 result otherwise throw a warning to fix Firestore songNumber
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    Intent intent = new Intent(view.getContext(), SinglePlayerGameActivity.class);
                                                    Bundle mBundle = new Bundle();

                                                    mBundle.putString("songID", document.getString("songID"));
                                                    mBundle.putString("answerCode", document.getString("answerCode"));
                                                    mBundle.putString("songTitle", document.getString("title"));

                                                    intent.putExtras(mBundle);
                                                    genresActivity.hideProgressDialog();
                                                    view.getContext().startActivity(intent);
                                                }
                                            } else {
                                                genresActivity.hideProgressDialog();
                                                Log.d(TAG, "Fix songNumber, there might be duplicate number ", task.getException());
                                            }

                                        } else {
                                            genresActivity.hideProgressDialog();
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                            }else{
                                genresActivity.hideProgressDialog();
                            }
                        }
                    });
                }
            });
        }
    }

    public AlbumsAdapter(Context mContext, List<Album> albumList, GenresActivity genresActivity) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.genresActivity = genresActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
        holder.songID.setText(album.getSongID());
    }


    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}

