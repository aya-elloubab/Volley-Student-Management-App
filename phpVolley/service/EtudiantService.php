<?php
namespace service;


use IDao;
use PDO;

use connexion\Connexion;
use classes\Etudiant;


include_once '../classes/Etudiant.php';
include_once '../connexion/Connexion.php';
include_once '../dao/IDao.php';

class EtudiantService implements IDao {

    private $connexion;

    function __construct() {
        $this->connexion = new Connexion();
    }

    // Create a new Etudiant
    public function create($etudiant) {
        // Set the response header to JSON
        header('Content-Type: application/json');
        
        try {
            $query = "INSERT INTO etudiant (`nom`, `prenom`, `ville`, `sexe`, `image`) "
                . "VALUES (:nom, :prenom, :ville, :sexe, :image)";
            $stmt = $this->connexion->getConnexion()->prepare($query);

            $stmt->bindValue(':nom', $etudiant->getNom());
            $stmt->bindValue(':prenom', $etudiant->getPrenom());
            $stmt->bindValue(':ville', $etudiant->getVille());
            $stmt->bindValue(':sexe', $etudiant->getSexe());
            
            $imageData = $etudiant->getImage();
            if ($imageData !== null) {
                $stmt->bindValue(':image', $imageData, PDO::PARAM_LOB);
            } else {
                $stmt->bindValue(':image', null, PDO::PARAM_LOB);
            }

            if ($stmt->execute()) {
                echo json_encode([
                    "status" => "success",
                    "message" => "Etudiant added successfully"
                ]);
            } else {
                echo json_encode([
                    "status" => "error",
                    "message" => "Failed to add Etudiant: " . implode(", ", $stmt->errorInfo())
                ]);
            }
        } catch (\Exception $e) {
            echo json_encode([
                "status" => "error",
                "message" => "Error: " . $e->getMessage()
            ]);
        }
        exit;
    }

    public function findAll() {
        $etds = array();
        $query = "SELECT * FROM etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();

        // Check how many records are fetched
        if ($req->rowCount() === 0) {
            error_log("No records found in 'etudiant' table.");
            return [];
        }

        while ($e = $req->fetch(PDO::FETCH_OBJ)) {
            // Encode image as Base64 if present
            $imageBase64 = $e->image !== null ? base64_encode($e->image) : null;
            $etds[] = new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $imageBase64);
        }

        return $etds;
    }


    // Find Etudiant by ID
    public function findById($id) {
        $query = "SELECT * FROM etudiant WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->bindParam(':id', $id, PDO::PARAM_INT);
        $req->execute();

        if ($e = $req->fetch(PDO::FETCH_OBJ)) {
            // Encode image as Base64 if present
            $imageBase64 = $e->image !== null ? base64_encode($e->image) : null;
            return new Etudiant($e->id, $e->nom, $e->prenom, $e->ville, $e->sexe, $imageBase64);
        }

        return null; // Return null if no record is found
    }

    // Update an existing Etudiant
// Update an existing Etudiant
    function update($etudiant) {
        // Check if 'id' is set
        if (!$etudiant->getId()) {
            header('Content-type: application/json');
            echo json_encode([
                "message" => "ID parameter is missing.",
                "status" => "error"
            ]);
            return;
        }

        $id = $etudiant->getId();
        $setClauses = [];
        $params = [':id' => $id];

        // Check and add parameters if they are set
        if ($etudiant->getNom()) {
            $setClauses[] = "nom = :nom";
            $params[':nom'] = $etudiant->getNom();
        }
        if ($etudiant->getPrenom()) {
            $setClauses[] = "prenom = :prenom";
            $params[':prenom'] = $etudiant->getPrenom();
        }
        if ($etudiant->getVille()) {
            $setClauses[] = "ville = :ville";
            $params[':ville'] = $etudiant->getVille();
        }
        if ($etudiant->getSexe()) {
            $setClauses[] = "sexe = :sexe";
            $params[':sexe'] = $etudiant->getSexe();
        }
        if ($etudiant->getImage()) {
            $setClauses[] = "image = :image";
            $params[':image'] = $etudiant->getImage();
        }

        // If no fields to update, return an error message
        if (empty($setClauses)) {
            header('Content-type: application/json');
            echo json_encode([
                "message" => "No parameters to update.",
                "status" => "error"
            ]);
            return;
        }

        $setPart = implode(", ", $setClauses);
        $query = "UPDATE etudiant SET $setPart WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);

        foreach ($params as $key => $value) {
            $req->bindValue($key, $value);
        }

        if (!$req->execute()) {
            die('Error executing SQL query: ' . implode(", ", $req->errorInfo()));
        }

        header('Content-type: application/json');
        echo json_encode([
            "message" => "Student with ID $id has been updated.",
            "status" => "success"
        ]);
    }


    public function findAllApi() {
        $query = "SELECT * FROM etudiant";
        $req = $this->connexion->getConnexion()->prepare($query);
        $req->execute();

        // Fetch all data
        $data = $req->fetchAll(PDO::FETCH_ASSOC);

        // Check if data is fetched
        if (!$data) {
            error_log("No data found");
            return [];
        }

        // Convert images from BLOB to Base64
        foreach ($data as &$etudiant) {
            if (isset($etudiant['image']) && !empty($etudiant['image'])) {
                $etudiant['image'] = base64_encode($etudiant['image']); // Convert BLOB to Base64
            } else {
                $etudiant['image'] = null; // Set to null if no image exists
            }
        }

        return $data; // Return the processed data
    }




    // Delete an Etudiant by ID
    public function delete($etudiant) {
        $query = "DELETE FROM etudiant WHERE id = :id";
        $req = $this->connexion->getConnexion()->prepare($query);

        // Store the ID in a variable
        $id = $etudiant->getId();

        // Bind the variable to the parameter
        $req->bindParam(':id', $id, PDO::PARAM_INT);

        // Execute the delete query
        if (!$req->execute()) {
            die('Error executing SQL query: ' . implode(", ", $req->errorInfo()));
        }
    }

}
