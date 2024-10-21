package com.example.projetws;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddEtudiantFragment extends Fragment implements View.OnClickListener {
    private EditText nom;
    private static final String TAG = "AddTag";
    private EditText prenom;
    private RadioButton m;
    private Spinner ville;
    private RadioButton f;
    private Button add, uploadImage;
    private ImageView imageView;
    private Bitmap selectedImage;
    private static final int IMAGE_PICK_CODE = 1000;
    RequestQueue requestQueue;
    String insertUrl = "http://10.0.2.2/phpVolley/ws/createEtudiant.php";

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_etudiant, container, false);

        nom = view.findViewById(R.id.nom);
        prenom = view.findViewById(R.id.prenom);
        ville = view.findViewById(R.id.ville);
        add = view.findViewById(R.id.add);
        m = view.findViewById(R.id.m);
        f = view.findViewById(R.id.f);
        uploadImage = view.findViewById(R.id.uploadImage);
        imageView = view.findViewById(R.id.imageView);

        add.setOnClickListener(this);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });

        return view;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedImage); // Set the selected image in the ImageView
                Log.d(TAG, "Image selected successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View v) {
        if (v == add) {
            requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to add Etudiant", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(getActivity(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        Log.e("AddTag", "Error: " + error.getMessage() + ", Status Code: " + error.networkResponse.statusCode);
                        String errorData = new String(error.networkResponse.data);
                        Log.e("AddTag", "Error response: " + errorData);
                    } else {
                        Log.e("AddTag", "Error: " + error);
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String sexe = m.isChecked() ? "homme" : "femme";
                    HashMap<String, String> params = new HashMap<>();
                    params.put("nom", nom.getText().toString());
                    params.put("prenom", prenom.getText().toString());
                    params.put("ville", ville.getSelectedItem().toString());
                    params.put("sexe", sexe);

                    if (selectedImage != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        params.put("image", encodedImage); // Adding the image to the request parameters
                    }

                    return params;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    1000000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(request);
        }
    }
}
