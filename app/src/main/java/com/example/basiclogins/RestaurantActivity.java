package com.example.basiclogins;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.List;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextCuisine;
    private EditText editTextAddress;
    private Restaurant restaurant;
    private RatingBar ratingBar;
    private EditText editTextWebsite;
    private Spinner spinnerPrice;
    private Button save;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        wireWidgets();
        prefillFields();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToBackendless();
            }
        });
    }

    public void deleteRestaurant()
    {
        Backendless.Persistence.save( restaurant, new AsyncCallback<Restaurant>()
        {
            public void handleResponse( Restaurant savedRestaurant )
            {
                Backendless.Persistence.of( Restaurant.class ).remove( savedRestaurant,
                        new AsyncCallback<Long>()
                        {
                            public void handleResponse( Long response )
                            {
                                Toast.makeText(RestaurantActivity.this, restaurant.getName() + " deleted", Toast.LENGTH_SHORT).show();
                                // Contact has been deleted. The response is the
                                // time in milliseconds when the object was deleted
                            }
                            public void handleFault( BackendlessFault fault )
                            {
                                Toast.makeText(RestaurantActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                                // an error has occurred, the error code can be
                                // retrieved with fault.getCode()
                            }
                        } );
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(RestaurantActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }

    private void onContextItemSelected(MenuItem delete){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) delete.getMenuInfo();
            int index = info.position;
            adapter.notifyDataSetChanged();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurantactivitymenu, menu);
    }


    private void wireWidgets() {
        save = findViewById(R.id.button_restaurantactivity_save);
        editTextName = findViewById(R.id.editText_restaurantactivity_name);
        editTextWebsite = findViewById(R.id.editText_restaurantactivity_website);
        editTextCuisine = findViewById(R.id.editText_restaurantactivity_cuisine);
        editTextAddress = findViewById(R.id.editText_restaurantactivity_address);
        ratingBar = findViewById(R.id.ratingBar_restaurantactivity);
        spinnerPrice = findViewById(R.id.spinner_restaurantactivity_price);

    }

    private void saveToBackendless() {
        restaurant = new Restaurant();
        String name = editTextName.getText().toString();
        String address = editTextAddress.getText().toString();
        String cuisine = editTextCuisine.getText().toString();
        int price = spinnerPrice.getSelectedItem().toString().length();
        double rating = ratingBar.getRating();
        String website = editTextWebsite.getText().toString();
        if(restaurant != null){
            restaurant.setName(name);
            restaurant.setAddress(address);
            restaurant.setWebsiteLink(website);
            restaurant.setCuisine(cuisine);
            restaurant.setPrice(price);
            restaurant.setRating(rating);
        }


        Backendless.Persistence.save( restaurant, new AsyncCallback<Restaurant>() {
            public void handleResponse(Restaurant response) {
                Toast.makeText(RestaurantActivity.this, response.getName() + " saved", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RestaurantActivity.this, RestaurantListActivity.class);
                startActivity(intent);
                // new Contact instance has been save
            }
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(RestaurantActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });

    }

    private void prefillFields() {
        Intent restaurantIntent = getIntent();
        Restaurant restaurant = restaurantIntent.getParcelableExtra(RestaurantListActivity.EXTRA_RESTAURANT);
        if(restaurant != null) {
            editTextName.setText(restaurant.getName());
            editTextAddress.setText(restaurant.getAddress());
            editTextWebsite.setText(restaurant.getWebsiteLink());
            editTextCuisine.setText(restaurant.getCuisine());
            ratingBar.setRating((float)restaurant.getRating(ratingBar));
            spinnerPrice.setSelection(restaurant.getPrice() - 1);
        }
    }
}
