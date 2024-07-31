package com.example.simplymov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplymov.models.Order;
import com.example.simplymov.models.OrderStatus;
import com.example.simplymov.models.TruckSize;
import com.example.simplymov.models.UserModel;
import com.example.simplymov.util.FirebaseUtil;
import com.example.simplymov.util.MapUtil;
import com.example.simplymov.util.UserPreferences;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PackersServiceActivity extends AppCompatActivity {
    CheckBox cbOther, cbFragile, cbFurniture, cbOffice, cbPlants, cbElectronics, cbAppliances, cbMachine, cbArtWorks, cbPets;
    EditText edMoreDescription, edFrom, edTo, edTime;
    LinearLayout smallTruck, mediumTruck, largeTruck;
    Button bookBtn;
    ImageView backBtn;
    TruckSize checkedTruckSize;
    ProgressDialog progressDialog;
    UserModel userModel = UserPreferences.getUser();
    Calendar calendar;
    private int year, month, day, hour, minute;
    Timestamp timestamp;
    FirebaseFirestore db;
    double price = 0;
    String from, to;
    ArrayList<String> itemType;
    String truckSize, itemTypeStr;
    double kms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packers_service);
        initView();

        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        cbOther.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                edMoreDescription.setVisibility(View.VISIBLE);
            } else {
                edMoreDescription.setVisibility(View.GONE);
            }
        });

        backBtn.setOnClickListener(view -> onBackPressed());

        edTime.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PackersServiceActivity.this, (view1, year, monthOfYear, dayOfMonth) -> {
                // Set selected date to Calendar
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Show TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PackersServiceActivity.this, (view11, hourOfDay, minute) -> {
                    // Set selected time to Calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Display selected date and time
                    updateDateTime();
                }, hour, minute, false);
                timePickerDialog.show();
            }, year, month, day);
            datePickerDialog.show();
        });

        bookBtn.setOnClickListener(view -> {
            if (checkedTruckSize == null) {
                Toast.makeText(PackersServiceActivity.this, "Please select truck size", Toast.LENGTH_SHORT).show();
                return;
            }

            itemType = new ArrayList<>();
            from = edFrom.getText().toString();
            to = edTo.getText().toString();


            LatLng pickup = MapUtil.geocodeAddress(PackersServiceActivity.this, from);
            if (pickup == null) {
                Toast.makeText(PackersServiceActivity.this, "Modify the pickup location", Toast.LENGTH_LONG).show();
                return;
            }
            LatLng dropOff = MapUtil.geocodeAddress(PackersServiceActivity.this, to);
            if (dropOff == null) {
                Toast.makeText(PackersServiceActivity.this, "Modify the drop off location", Toast.LENGTH_LONG).show();
                return;
            }

            kms = MapUtil.calculateDistance(pickup.latitude, pickup.longitude, dropOff.latitude, dropOff.longitude);

            if (kms < 2) {
                Toast.makeText(this, "Enter correct pickup and drop off location", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkedTruckSize == TruckSize.MEDIUM) {
                price += 250;
            } else if (checkedTruckSize == TruckSize.LARGE) {
                price += 500;
            }

            price = price + (25 * kms);

            if (userModel.getBalance() < price) {
                Toast.makeText(this, "Not enough money in wallet", Toast.LENGTH_LONG).show();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return;
            }



            truckSize = checkedTruckSize.toString();


            if (cbFragile.isChecked()) itemType.add("fragile");
            if (cbFurniture.isChecked()) itemType.add("furniture");
            if (cbOffice.isChecked()) itemType.add("office");
            if (cbPlants.isChecked()) itemType.add("plants");
            if (cbElectronics.isChecked()) itemType.add("electronics");
            if (cbAppliances.isChecked()) itemType.add("appliances");
            if (cbMachine.isChecked()) itemType.add("machine");
            if (cbArtWorks.isChecked()) itemType.add("art works");
            if (cbPets.isChecked()) itemType.add("pets");

            itemTypeStr = "";

            for (String item : itemType) {
                itemTypeStr = itemTypeStr.concat(item + ", ");
            }

            showCustomDialog();
        });
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.book_confirmation_dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        TextView positiveButton = dialogView.findViewById(R.id.bookBtn);
        TextView negativeButton = dialogView.findViewById(R.id.cancelBtn);

        TextView orderDate = dialogView.findViewById(R.id.bookOrderDate);
        TextView orderTime = dialogView.findViewById(R.id.bookOrderTime);
        TextView orderPickup = dialogView.findViewById(R.id.bookPickup);
        TextView orderDropOff = dialogView.findViewById(R.id.bookDropOff);
        TextView orderPrice = dialogView.findViewById(R.id.bookOrderPrice);
        TextView orderSize = dialogView.findViewById(R.id.bookOrderSize);
        TextView orderKms = dialogView.findViewById(R.id.bookKms);

        orderDate.setText(FirebaseUtil.timestampToDateString(timestamp));
        orderTime.setText(FirebaseUtil.timestampToTimeString(timestamp));

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String priceStr = "ETB " + decimalFormat.format(price);
        orderPrice.setText(priceStr);

        orderPickup.setText(from);
        orderDropOff.setText(to);
        orderSize.setText(truckSize);

        String kmsStr = decimalFormat.format(kms) + " Kms";
        orderKms.setText(kmsStr);

        positiveButton.setOnClickListener(v -> {
            bookOrder();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void bookOrder() {
        progressDialog = new ProgressDialog(PackersServiceActivity.this);
        progressDialog.setTitle("Booking Order...");
        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userModel.getUserId());
        data.put("from", from);
        data.put("to", to);
        data.put("itemType", itemTypeStr);
        data.put("size", truckSize);
        data.put("dateTime", timestamp);
        data.put("kms", kms);
        data.put("price", price);
        data.put("status", OrderStatus.RECEIVED);
        data.put("includesPacking", false);
        data.put("rate", 0);

        double finalPrice = -1 * price;
        userModel.setBalance(userModel.getBalance() - price);
        UserPreferences.setUser(userModel);

        db.collection("orders").add(data).addOnSuccessListener(documentReference -> {
            String orderId = documentReference.getId();

            db.collection("orders").document(orderId).update("orderId", orderId);

            db.collection("users").document(userModel.getUserId()).update("balance", FieldValue.increment(finalPrice));
            db.collection("users").document(userModel.getUserId()).update("numOfOrders", FieldValue.increment(1));
            db.collection("users").document(userModel.getUserId()).update("totalKms", FieldValue.increment(kms));

            userModel.setNumOfOrders(userModel.getNumOfOrders() + 1);
            userModel.setTotalKms(userModel.getTotalKms() + kms);

            UserPreferences.setUser(userModel);

            progressDialog.dismiss();

            Toast.makeText(PackersServiceActivity.this, "Order added successfully!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(PackersServiceActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void updateDateTime() {
        String dateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar).toString();
        timestamp = new Timestamp(calendar.getTime());
        edTime.setText(dateTime);
    }

    @SuppressLint("NonConstantResourceId")
    public void onOptionsClick(View view) {
        switch (view.getId()) {
            case R.id.smallTruck:
                checkedTruckSize = TruckSize.SMALL;
                smallTruck.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_truck_size));
                mediumTruck.setBackground(null);
                largeTruck.setBackground(null);
                break;
            case R.id.mediumTrack:
                checkedTruckSize = TruckSize.MEDIUM;
                smallTruck.setBackground(null);
                mediumTruck.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_truck_size));
                largeTruck.setBackground(null);
                break;
            case R.id.largeTruck:
                checkedTruckSize = TruckSize.LARGE;
                smallTruck.setBackground(null);
                mediumTruck.setBackground(null);
                largeTruck.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_truck_size));
                break;
        }
    }

    private void initView() {
        cbOther = findViewById(R.id.cbOther);
        edMoreDescription = findViewById(R.id.edMoreDescription);
        edFrom = findViewById(R.id.edMoveNowFrom);
        edTo = findViewById(R.id.edMoveNowTo);
        smallTruck = findViewById(R.id.smallTruck);
        mediumTruck = findViewById(R.id.mediumTrack);
        largeTruck = findViewById(R.id.largeTruck);
        bookBtn = findViewById(R.id.moveNowBookBtn);
        cbAppliances = findViewById(R.id.cbAppliances);
        cbFragile = findViewById(R.id.cbFragile);
        cbArtWorks = findViewById(R.id.cbArtWorks);
        cbElectronics = findViewById(R.id.cbElectronics);
        cbFurniture = findViewById(R.id.cbFurniture);
        cbMachine = findViewById(R.id.cbMachine);
        cbOffice = findViewById(R.id.cbOffice);
        cbPets = findViewById(R.id.cbPets);
        cbPlants = findViewById(R.id.cbPlants);
        backBtn = findViewById(R.id.packersBackBtn);
        edTime = findViewById(R.id.editTextTime);
    }
}