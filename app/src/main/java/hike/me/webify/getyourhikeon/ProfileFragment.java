package hike.me.webify.getyourhikeon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileFragment extends android.support.v4.app.Fragment {
    private View view;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private TextView name, email, contact;
    private ImageView imageView;
    private Spinner current, previous;
    private ArrayList<String> current_treks;
    private JSONArray result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();
        imageView = view.findViewById(R.id.profile);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        contact = view.findViewById(R.id.contact);
        current = view.findViewById(R.id.current);
        //previous=(Spinner) view.findViewById(R.id.previous);
        current_treks = new ArrayList<String>();
        getData();
        getProfilePic();
        getHikes();
        current.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, getHikes()));
        current.setSelection(0);

//        contact=(TextView) view.findViewById(R.id.contact);
        //Picasso.with(context).load(R.drawable.user).resize(150,150).centerCrop().into(imageView);
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
//        contact.setText(user.getPhoneNumber());
//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        ImageView edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click action
                Intent intent = new Intent(context, EditProfile.class);
                startActivity(intent);
            }
        });
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Click action
//                Intent intent = new Intent(context, EditProfile.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void getData() {
        //Creating a string request
        StringRequest stringRequest = new StringRequest("http://getyourhike.esy.es/current_treks_list.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("result");

                            //Calling method gethike to get the hikes from the JSON Array
                            //getHikes(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Toast.makeText(context,"Please Check Your Internet Connection And Restart",Toast.LENGTH_LONG).show();
                    }
                });
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                //Creating parameters
//                Map<String, String> params = new Hashtable<String, String>();
//
//                //Adding parameters
//                params.put("uid",user.getUid());
//
//                //returning parameters
//                return params;
//            }
//        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getProfilePic() {
        //Creating a string request
        StringRequest stringRequest = new StringRequest("http://getyourhike.esy.es/load_user_info.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("result");

                            //Calling method gethike to get the hikes from the JSON Array
                            for (int i = 0; i < result.length(); i++) {
                                try {
                                    //Getting json object
                                    JSONObject json = result.getJSONObject(i);
                                    //Adding the name of the student to array list

                                    if (user.getUid().equalsIgnoreCase(json.getString("id"))) {
                                        Picasso.with(context).load(json.getString("displayPicture")).resize(150, 150).centerCrop().into(imageView);
                                        contact.setText(json.getString("contact"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(context,"Please Check Your Internet Connection And Restart",Toast.LENGTH_LONG).show();
                    }
                });
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private ArrayList<String> getHikes() {
        DatabaseReference db = null;
        db = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> trips = new ArrayList<>();
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                fetchdata(dataSnapshot, trips);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return trips;
    }
    //this is a test commnent

    private void fetchdata(DataSnapshot dataSnapshot, ArrayList<String> trips) {
        trips.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            String location = ds.getValue(BookData.class).getLocation();
            trips.add(location);
        }
    }
}
