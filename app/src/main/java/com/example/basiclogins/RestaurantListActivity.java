package com.example.basiclogins;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    private static final int MENU_DELETE = 10;
    private ListView listViewRestaurant;
    private FloatingActionButton floatingActionButtonRestaurantList;
    public static final String EXTRA_RESTAURANT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        wireWidgets();
        populateListView();
        floatingActionButtonRestaurantList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                startActivity(intent);
            }
        });

    }



    private void populateListView() {

        String ownerId = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "ownerId = '" + ownerId + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(Restaurant.class).find(new AsyncCallback<List<Restaurant>>(){
            @Override
            public void handleResponse(final List<Restaurant> restaurantList )
            {
                RestaurantAdapter adapter = new RestaurantAdapter(
                        RestaurantListActivity.this, android.R.layout.simple_list_item_1, restaurantList
                );

                listViewRestaurant.setAdapter(adapter);
                // all Restaurant instances have been found

                listViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent restaurantDetailIntent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                        restaurantDetailIntent.putExtra(EXTRA_RESTAURANT, restaurantList.get(position));
                        startActivity(restaurantDetailIntent);
                    }
                });
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "DELETE");
    }

    private void wireWidgets() {
        listViewRestaurant = findViewById(R.id.listview_restaurantlist);
        floatingActionButtonRestaurantList = findViewById(R.id.floatingActionButton_restaurantList);
        registerForContextMenu(listViewRestaurant);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case MENU_DELETE:
                //delete_item(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteRestaurant(Restaurant restaurant)
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
                                Toast.makeText(RestaurantListActivity.this, " deleted", Toast.LENGTH_SHORT).show();
                                // Contact has been deleted. The response is the
                                // time in milliseconds when the object was deleted
                            }
                            public void handleFault( BackendlessFault fault )
                            {
                                Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                                // an error has occurred, the error code can be
                                // retrieved with fault.getCode()
                            }
                        } );
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }

}
