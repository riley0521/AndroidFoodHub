package com.example.riley.androidfoodhub;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.riley.androidfoodhub.Common.Common;

import com.example.riley.androidfoodhub.Common.Config;
import com.example.riley.androidfoodhub.Database.Database;
import com.example.riley.androidfoodhub.Helper.RecyclerItemTouchHelper;
import com.example.riley.androidfoodhub.Interface.RecyclerItemTouchHelperListener;
import com.example.riley.androidfoodhub.Model.DataMessage;
import com.example.riley.androidfoodhub.Model.MyResponse;
import com.example.riley.androidfoodhub.Model.Order;
import com.example.riley.androidfoodhub.Model.Request;
import com.example.riley.androidfoodhub.Model.Token;
import com.example.riley.androidfoodhub.Remote.APIService;
import com.example.riley.androidfoodhub.Remote.IGoogleService;
import com.example.riley.androidfoodhub.ViewHolder.CartViewHolder;
import com.example.riley.androidfoodhub.ViewHolder.CartAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, RecyclerItemTouchHelperListener {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotal;
    double subTotal;
    public TextView txtTotalPrice;
    FButton btnPlace, btnCancel;

    List<Order> carts = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;

    RelativeLayout rootLayout;

    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    Place shipAddress;
    String address,comment, cDate;


    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private static final int LOCATION_REQUEST_CODE = 9999;
    private static final int PLAY_SERVICES_REQUEST = 9997;

    IGoogleService mGoogleMapService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        mGoogleMapService = Common.getGoogleMapAPI();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },LOCATION_REQUEST_CODE);
        }
        else {
            if(checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Restaurants").child(Common.restaurantSelected).child("Requests");

        mService = Common.getFCMService();

        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout = findViewById(R.id.rootLayout);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);
        btnCancel = (FButton)findViewById(R.id.btnCancelOrder);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carts.size() > 0)
                    CancelOrder();
                else
                    Toast.makeText(Cart.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carts.size() > 0) {
                    carts = new Database(Cart.this).getCarts(Common.currentUser.getPhone());
                    int total = 0;
                    for (Order order : carts) {
                        total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
                    }
                    Locale locale = new Locale("tl", "PH");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));
                    try {
                        showAlertDialog();
                    }catch (Exception e) {
                        Toast.makeText(Cart.this, "Please try again", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                else
                    Toast.makeText(Cart.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        cDate = new SimpleDateFormat("MMMM dd, yyyy",Locale.getDefault()).format(new Date());
        loadListFood();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
            }
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void CancelOrder() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Cancel Order");
        alertDialog.setMessage("Do you want to cancel order?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);

        txtTotal = order_address_comment.findViewById(R.id.total);
        String stringTotal = txtTotalPrice.getText().toString().replace("₱","")
                .replace(",","");
        subTotal = Double.parseDouble(stringTotal) + 75;
        Locale locale = new Locale("tl", "PH");
        final NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotal.setText(fmt.format(subTotal));




//        final MaterialEditText edtAddress = (MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final PlaceAutocompleteFragment edtAddress = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_moto);
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your address");
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);

        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shipAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR",status.getStatusMessage());
            }
        });
        final MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);
        DecimalFormat precision = new DecimalFormat("0.00");

        final RadioButton rbCOD = (RadioButton)order_address_comment.findViewById(R.id.rbCOD);
        final RadioButton rbPaypal = (RadioButton)order_address_comment.findViewById(R.id.rbPaypal);
        final RadioButton rbBalance = (RadioButton)order_address_comment.findViewById(R.id.rbBalance);
        rbBalance.setText("Load Balance ("+precision.format(Common.currentUser.getBalance())+")");

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        comment = edtComment.getText().toString();
                        if(shipAddress != null) {
                            address = shipAddress.getAddress().toString();
                        }
                        else {
                            Toast.makeText(Cart.this, "Please enter address", Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction()
                                    .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_moto))
                                    .commit();
                            return;
                        }


                            if (!rbCOD.isChecked() && !rbPaypal.isChecked() && !rbBalance.isChecked()) {
                                Toast.makeText(Cart.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                                getFragmentManager().beginTransaction()
                                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_moto))
                                        .commit();
                                return;
                            } else if (rbPaypal.isChecked()) {
                                String formatAmount = txtTotalPrice.getText().toString()
                                        .replace("₱", "")
                                        .replace(",", "");
                                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(subTotal),
                                        "PHP",
                                        "E.U.T App Order",
                                        PayPalPayment.PAYMENT_INTENT_SALE);
                                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                            } else if (rbCOD.isChecked()) {
                                Request request = new Request(
                                        cDate,
                                        Common.currentUser.getPhone(),
                                        Common.currentUser.getName(),
                                        address,
                                        String.valueOf(subTotal),
                                        "0",
                                        comment,
                                        String.format("'%s','%s'", shipAddress.getLatLng().latitude, shipAddress.getLatLng().longitude),
                                        "Cash On Delivery",
                                        "Unpaid",
                                        Common.restaurantSelected,
                                        carts
                                );
                                String order_number = String.valueOf(System.currentTimeMillis());
                                requests.child(order_number)
                                        .setValue(request);
                                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                                sendNotification(order_number);
                                Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();

                            } else if (rbBalance.isChecked()) {
                                double amount = 0;
                                try {
                                    Locale locale = new Locale("tl","PH");
                                    amount = Common.formatCurrency(txtTotal.getText().toString(), locale).doubleValue();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (java.text.ParseException e) {
                                    Toast.makeText(Cart.this, "Could not process your load balance", Toast.LENGTH_SHORT).show();

                                }

                                if (Common.currentUser.getBalance() >= amount) {
                                    Request request = new Request(
                                            cDate,
                                            Common.currentUser.getPhone(),
                                            Common.currentUser.getName(),
                                            address,
                                            String.valueOf(subTotal),
                                            "0",
                                            comment,
                                            String.format("'%s','%s'", shipAddress.getLatLng().latitude, shipAddress.getLatLng().longitude),
                                            "Load Balance",
                                            "Paid",
                                            Common.restaurantSelected,
                                            carts
                                    );
                                    final String order_number = String.valueOf(System.currentTimeMillis());
                                    requests.child(order_number)
                                            .setValue(request);
                                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());


                                    double balance = Common.currentUser.getBalance() - amount;
                                    Map<String, Object> update_balance = new HashMap<>();
                                    update_balance.put("balance", balance);

                                    FirebaseDatabase.getInstance()
                                            .getReference("User")
                                            .child(Common.currentUser.getPhone())
                                            .updateChildren(update_balance)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance()
                                                                .getReference("User")
                                                                .child(Common.currentUser.getPhone())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        sendNotification(order_number);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(Cart.this, "Your balance is not enough, please choose other payment method", Toast.LENGTH_SHORT).show();
                                }
                            }

                            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_moto)).commit();
                            finish();

                    }

                });




        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_moto)).commit();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null) {
                    try{
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                cDate,
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                address,
                                String.valueOf(subTotal),
                                "0",
                                comment,
                                String.format("'%s','%s'",shipAddress.getLatLng().latitude,shipAddress.getLatLng().longitude),
                                "Paypal",
                                jsonObject.getJSONObject("response").getString("state"),
                                Common.restaurantSelected,
                                carts
                        );
                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(request);
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());

                        sendNotification(order_number);
                        Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_moto)).commit();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this, "Invalid payment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
            break;
        }
    }

    private void sendNotification(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()) {
                    Token serverToken = postSnapShot.getValue(Token.class);
//                    Notification notification = new Notification(Common.currentUser.getPhone(), "You have new order "+order_number);
//                    Sender content = new Sender(serverToken.getToken(),notification);

                    Map<String,String> dataSend = new HashMap<>();
                    dataSend.put("Title",Common.currentUser.getPhone());
                    dataSend.put("Message", "You have new order "+order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(),dataSend);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> responses) {

                                        if (responses.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Ordering Failed", Toast.LENGTH_SHORT).show();
                                        }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        carts = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(carts,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        int total = 0;
        for(Order order:carts) {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
            Locale locale = new Locale("tl","PH");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));

    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        carts.remove(position);
        new Database(this).cleanCart(Common.currentUser.getPhone());
        for(Order item:carts)
            new Database(this).addToCart(item);

        loadListFood();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        new Database(this).cleanCart();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            Log.d("LOCATION","Your location :"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
        }
        else
        {
            Log.d("ERROR","Couldnt get your location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder) {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.currentUser.getPhone());

            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for(Order item:orders) {
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
            }
                Locale locale = new Locale("tl", "PH");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                txtTotalPrice.setText(fmt.format(total));


                Snackbar snackbar = Snackbar.make(rootLayout,name + " was remove from cart",Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter.restoreItem(deleteItem,deleteIndex);
                        new Database(getBaseContext()).addToCart(deleteItem);

                        int total = 0;
                        List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                        for(Order item:orders) {
                            total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                        }
                            Locale locale = new Locale("tl", "PH");
                            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                            txtTotalPrice.setText(fmt.format(total));

                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
        }

}
}
