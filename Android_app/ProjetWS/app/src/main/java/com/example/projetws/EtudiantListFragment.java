package com.example.projetws;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EtudiantListFragment extends Fragment {

    private static final String TAG = "EtudiantListFragment";
    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiants;
    private RequestQueue requestQueue;
    //private static final String LOAD_URL = "http://192.168.1.8/php_volley/ws/loadEtudiant.php";
    private static final String LOAD_URL = "http://10.0.2.2/phpVolley/ws/loadEtudiant.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etudiant_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        etudiants = new ArrayList<>(); // Initialize the student list

        adapter = new EtudiantAdapter(etudiants, getContext());
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());
        adapter.attachSwipeToDelete(recyclerView);

        // Load data from the server
        loadEtudiants();

        // Set up search functionality
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.app_bar_search);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);  // Filter the adapter data
                return true;
            }
        });

        return view;
    }

    private void loadEtudiants() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);
                        try {
                            // Parse the JSON array
                            Type listType = new TypeToken<List<Etudiant>>() {}.getType();
                            List<Etudiant> loadedEtudiants = new Gson().fromJson(response, listType);

                            if (loadedEtudiants == null || loadedEtudiants.isEmpty()) {
                                throw new Exception("Parsed student list is null or empty");
                            }

                            for (Etudiant etudiant : loadedEtudiants) {
                                Log.d("loaded ", etudiant.toString());
                            }

                            etudiants.clear();
                            etudiants.addAll(loadedEtudiants);
                            adapter.updateEtudiants(loadedEtudiants);
                            Log.d("loaded: ", ""+loadedEtudiants);

                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "JSON syntax error: " + e.getMessage(), e);
                            Toast.makeText(getActivity(), "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error loading students", e);
                            Toast.makeText(getActivity(), "Error loading students", Toast.LENGTH_SHORT).show();
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.getMessage(), error);
                        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(stringRequest);
    }
}
