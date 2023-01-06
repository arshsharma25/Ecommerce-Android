package com.example.f21g4_minions.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.f21g4_minions.Buyers.MainActivity;
import com.example.f21g4_minions.Model.Products;
import com.example.f21g4_minions.R;
import com.example.f21g4_minions.ViewHolder.ItemViewHolder;
import com.example.f21g4_minions.databinding.ActivitySellerHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class SellerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivitySellerHomeBinding binding;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySellerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);


        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_add, R.id.navigation_notifications)
//                .build();


        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView = findViewById(R.id.seller_home_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("sid")
                        .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder productViewHolder, int i, @NonNull Products products) {
                productViewHolder.txtProductName.setText(products.getName());
                productViewHolder.txtProductDescription.setText(products.getDescription());
                productViewHolder.txtProductStatus.setText("State: " + products.getProductState());
                productViewHolder.txtProductPrice.setText("Price = " + products.getPrice() + "$");
                Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                productViewHolder.itemView.setOnClickListener(view -> {
                    final String productID = products.getPid();

                    CharSequence[] options1 = new CharSequence[]{
                            "Yes", "No"
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                    builder.setTitle("Do you want to delete this product?");
                    builder.setItems(options1, (dialogInterface, i1) -> {
                        if (i1 == 0) {
                            deleteProduct(productID);
                        }
                        if (i1 == 1) {
                            // TODO when seller clicks no to delete the item what action needs to be taken
                        }
                    });
                    builder.show();

                });

            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view, parent, false);
                return new ItemViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productID) {
        unverifiedProductsRef.child(productID)
                .removeValue()
                .addOnCompleteListener(task -> Toast.makeText(SellerHomeActivity.this, "The item has been removed successfully", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_home) {
//            mTextMessage.setText(R.string.title_home);
            Intent intentHome = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
            startActivity(intentHome);
            return true;


        } else if (id == R.id.navigation_add) {

//            mTextMessage.setText(R.string.title_add);
            Intent intentCate = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
            startActivity(intentCate);
            finish();


        } else if (id == R.id.navigation_logout) {
            final FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();

            Intent intentMain = new Intent(SellerHomeActivity.this, MainActivity.class);
            intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentMain);
            finish();
            return true;


        }

        return false;

    }

}