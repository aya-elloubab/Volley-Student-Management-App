package com.example.projetws.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide; // Add Glide or any image loading library
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.projetws.R;
import com.example.projetws.beans.Etudiant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> implements Filterable{
    private static final String TAG = "EtudiantAdapter";
    private List<Etudiant> etudiants;
    private List<Etudiant> etudiantsFiltered;
    private Context context;
    private NewFilter mfilter;
    private RequestQueue requestQueue;
    private static final String DELETE_URL = "http://10.0.2.2/phpVolley/ws/deleteEtudiant.php";

    public EtudiantAdapter(List<Etudiant> etudiants, Context context) {
        this.etudiants=etudiants;
        this.context = context;
        this.etudiantsFiltered = new ArrayList<>(etudiants);
        this.mfilter = new NewFilter(this);
        requestQueue = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.etudiant_item, parent, false);
        Log.d(TAG,"etdfilt"+ this.etudiantsFiltered);
        return new EtudiantViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiantsFiltered.get(position);
        holder.bind(etudiant, holder);
        holder.itemView.setOnClickListener(v -> {
            showEditDialog(etudiant, position);
        });

    }
    private void showEditDialog(Etudiant etudiant, int position) {
        // Créer un layout custom pour la boîte de dialogue
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);

        // Récupérer les EditTexts
        EditText editNom = dialogView.findViewById(R.id.edit_nom);
        EditText editPrenom = dialogView.findViewById(R.id.edit_prenom);
        EditText editVille = dialogView.findViewById(R.id.edit_ville);

        // Remplir les EditText avec les informations actuelles de l'étudiant
        editNom.setText(etudiant.getNom());
        editPrenom.setText(etudiant.getPrenom());
        editVille.setText(etudiant.getVille());

        // Construire la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modifier l'étudiant");
        builder.setView(dialogView);

        // Bouton de validation
        builder.setPositiveButton("Valider", (dialog, which) -> {
            // Mettre à jour l'objet étudiant avec les nouvelles informations
            etudiant.setNom(editNom.getText().toString());
            etudiant.setPrenom(editPrenom.getText().toString());
            etudiant.setVille(editVille.getText().toString());

            // Appeler la méthode pour envoyer la mise à jour au serveur
            updateEtudiant(etudiant, position);

            // Actualiser la vue
            notifyItemChanged(position);
        });

        // Bouton d'annulation
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        // Afficher la boîte de dialogue
        builder.create().show();
    }
    private void updateEtudiant(Etudiant etudiant, int position) {
        String updateUrl = "http://10.0.2.2/phpVolley/ws/updateEtudiant.php"; // URL de l'API de mise à jour

        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Logique après la réponse du serveur
                        Toast.makeText(context, "Étudiant mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Erreur lors de la mise à jour: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                params.put("nom", etudiant.getNom());
                params.put("prenom", etudiant.getPrenom());
                params.put("ville", etudiant.getVille());
                return params;
            }
        };

        // Ajouter la requête à la file d'attente
        requestQueue.add(stringRequest);
    }

    private void setPlaceholderImage(ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(R.drawable.ic_launcher_background)
                .into(imageView);
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "Item count: " + etudiantsFiltered.size());
        return etudiantsFiltered.size();
    }
    @Override
    public Filter getFilter() {
        if (mfilter == null) {
            mfilter = new NewFilter(this);  // Initialize mfilter if it's null
        }
        return mfilter;
    }
    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView ville;
        ImageView img;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            ville = itemView.findViewById(R.id.etudiant_ville);
            img = itemView.findViewById(R.id.etudiant_img);
        }
        void bind(Etudiant etudiant, EtudiantViewHolder holder) {
            name.setText(etudiant.getNom()+" "+etudiant.getPrenom());
            ville.setText(etudiant.getVille());


            // Load and set the image
            String base64Image = etudiant.getImg();
            Log.d("EtudiantAdapter", "Image data: " + base64Image);

            if (base64Image != null && !base64Image.isEmpty()) {
                try {
                    // Decode Base64 string to byte array
                    byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);

                    // Convert byte array to Bitmap
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    // Use Glide to load the decoded Bitmap
                    Glide.with(holder.itemView.getContext())
                            .load(decodedBitmap)
                            .into(holder.img);
                } catch (IllegalArgumentException e) {
                    Log.e("EtudiantAdapter", "Invalid Base64 image data", e);
                    holder.img.setImageResource(R.drawable.ic_launcher_background); // Default image if decoding fails
                }
            } else {
                holder.img.setImageResource(R.drawable.ic_launcher_background); // Default image if no Base64 string
            }
        }
    }

    public void updateEtudiants(List<Etudiant> newList) {
        this.etudiantsFiltered = new ArrayList<>(newList);
        this.etudiants.clear();
        this.etudiants.addAll(newList);
        notifyDataSetChanged();
    }

    public class NewFilter extends Filter {
        public RecyclerView.Adapter mAdapter;

        public NewFilter(RecyclerView.Adapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Etudiant> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // If the search query is empty, show the full list
                filteredList.addAll(etudiants);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Etudiant etudiant : etudiants) { // Use original list
                    if (etudiant.getNom().toLowerCase().startsWith(filterPattern) ||
                            etudiant.getPrenom().toLowerCase().startsWith(filterPattern) ||
                            etudiant.getVille().toLowerCase().startsWith(filterPattern) ||
                            etudiant.getSexe().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(etudiant);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            etudiantsFiltered = (List<Etudiant>) results.values;
            notifyDataSetChanged(); // Notify the adapter that the data has changed
        }


    }
    public void attachSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Etudiant etudiantToDelete = etudiants.get(position);
                    showDeleteConfirmationDialog(etudiantToDelete, position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // Ajoutez ici des effets visuels lors du glissement, si vous le souhaitez
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showDeleteConfirmationDialog(Etudiant etudiant, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmer la suppression (édition swipe)");
        builder.setMessage("Voulez-vous vraiment supprimer cet étudiant ?");
        builder.setPositiveButton("Oui", (dialog, which) -> {
            deleteEtudiant(etudiant, position);  // Modified to include position
        });
        builder.setNegativeButton("Non", (dialog, which) -> {
            notifyItemChanged(position);
        });
        builder.setOnCancelListener(dialog -> {
            notifyItemChanged(position);
        });
        builder.show();
    }

    private void deleteEtudiant(Etudiant etudiant, int position) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("DeleteEtudiant", "Response: " + response);
                        // Remove from both lists
                        etudiants.remove(etudiant);
                        etudiantsFiltered.remove(etudiant);
                        notifyDataSetChanged();  // Refresh the entire list
                        Toast.makeText(context, "Etudiant supprimé avec succès", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("DeleteEtudiant", "Volley error: " + error.getMessage(), error);
                        Toast.makeText(context, "Erreur: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        // Restore the item view since deletion failed
                        notifyItemChanged(position);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
