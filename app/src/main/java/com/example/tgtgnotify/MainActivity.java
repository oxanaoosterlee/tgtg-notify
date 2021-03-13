package com.example.tgtgnotify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.tgtgnotify.login.LoginActivity;

import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MainRepository mainRepository;
    private MainViewModel mainViewModel;
    private Button refreshButton;
    private Button logoutButton;
    private LinearLayout verticalLayout;
    //private ProgressBar progressSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TGTG", "Main activity");
        refreshButton = (Button) findViewById(R.id.button_refresh);
        logoutButton = (Button) findViewById(R.id.button_logout);
        verticalLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        //progressSpinner = (ProgressBar) findViewById(R.id.progressSpinner);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refresh();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });

        mainRepository = MainRepository.getInstance(this.getApplicationContext());
        mainViewModel = new MainViewModel(mainRepository);


        // Observe list of items data
        mainViewModel.getItems().observe(this,
                new Observer<List<Item>>() {
                    @Override
                    public void onChanged(List<Item> items) {
                        updateListUI(items);
                    }
                });


        // Refresh on startup
        refresh();

    }

    public void refresh() {
//        this.verticalLayout.setVisibility(View.GONE);
//        this.progressSpinner.setVisibility(View.VISIBLE);
        this.mainViewModel.refreshData();
    }

    public void logout() {
        Log.d("TGTG", "logout");
        this.mainViewModel.logout();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    // Update the list of 'favorite' items that can be selected.
    private void updateListUI(List<Item> items) {
        Set<String> selected_ids = this.mainRepository.getStoredItemIds();
        Log.d("TGTG", "Stored item ids: " + selected_ids.toString());

        // Reset the view
        this.verticalLayout.removeAllViews();

        // Fill with new items
        for (Item item : items) {
            CheckBox checkbox = new CheckBox(this);
            LinearLayout horizontalLayout = new LinearLayout(this);
            LinearLayout verticalTextLayout = new LinearLayout(this);

            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView title = new TextView(this);
            TextView details = new TextView(this);

            title.setText(item.getDisplayName());
            Log.d("TGTG", "Item display: " + item.getDisplayName());
            Log.d("TGTG", "Item store: " + item.getStore());
            Log.d("TGTG", "Item price: " + item.getPrice());
            details.setText(" | " + item.getPrice());


            verticalTextLayout.addView(title);
            verticalTextLayout.addView(details);

            horizontalLayout.addView(verticalTextLayout);
            horizontalLayout.addView(checkbox);

            // Set checkbox enabled when it is stored.
            if (selected_ids != null && selected_ids.contains(item.getItemId())){
                Log.d("TGTG", "Item id " + item.getItemId() + " is stored!");
                checkbox.setChecked(true);
            }

            // Call function when checkbox is checked.
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mainRepository.store_item_id(item.getItemId());
                        Log.d("TGTG", "Clicked: " + item.getDisplayName());
                    } else {
                        mainRepository.remove_item_id(item.getItemId());
                        Log.d("TGTG", "Unclicked: " + item.getDisplayName());
                        int a = 4;
                    }
                }
            });

            verticalLayout.addView(horizontalLayout);
        }

//        this.progressSpinner.setVisibility(View.GONE);
//        this.verticalLayout.setVisibility(View.VISIBLE);


    }

}
