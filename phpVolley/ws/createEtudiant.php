<?php
define('RACINE', 'C:/xampp/htdocs/phpVolley/');

use service\EtudiantService;
use classes\Etudiant;

// Require necessary class files
require '../classes/Etudiant.php';
require '../service/EtudiantService.php';

header('Content-Type: application/json');

// Create an instance of the service
$etudiantService = new EtudiantService();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    error_log(print_r($_POST, true));

    // Get the parameters safely with default values
    $nom = isset($_POST['nom']) ? $_POST['nom'] : null;
    $prenom = isset($_POST['prenom']) ? $_POST['prenom'] : null;
    $ville = isset($_POST['ville']) ? $_POST['ville'] : null;
    $sexe = isset($_POST['sexe']) ? $_POST['sexe'] : null;
    $imageData = isset($_POST['image']) ? $_POST['image'] : null; // Get the Base64 image data

    // Check for missing parameters
    if ($nom === null || $prenom === null || $ville === null || $sexe === null || $imageData === null) {
        echo json_encode(['status' => 'error', 'message' => 'Missing parameters']);
        exit;
    }

    // Decode the Base64 image data
    $imageData = base64_decode($imageData);
    if ($imageData === false) {
        echo json_encode(['status' => 'error', 'message' => 'Failed to decode image']);
        exit;
    }

    // Create a new Etudiant object
    $etudiant = new Etudiant(null, $nom, $prenom, $ville, $sexe, $imageData);

    // Insert into the database
    $etudiantService->create($etudiant);

    echo json_encode(['status' => 'success', 'message' => 'Etudiant added successfully']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
}

