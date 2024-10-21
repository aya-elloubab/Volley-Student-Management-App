<?php

use service\EtudiantService;
require '../classes/Etudiant.php';
use classes\Etudiant;


if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    update();
}

function update() {
    if (!isset($_POST['id'])) {
        header('Content-type: application/json');
        echo json_encode([
            "message" => "Missing required parameters.",
            "status" => "error"
        ]);
        return;
    }

    $id = $_POST['id'];
    $nom = isset($_POST['nom']) ? $_POST['nom'] : null;
    $prenom = isset($_POST['prenom']) ? $_POST['prenom'] : null;
    $ville = isset($_POST['ville']) ? $_POST['ville'] : null;
    $sexe = isset($_POST['sexe']) ? $_POST['sexe'] : null;
    $imageBase64 = isset($_POST['imageBase64']) ? $_POST['imageBase64'] : null;
    $decodedImage = $imageBase64 ? base64_decode($imageBase64) : null;

    $es = new EtudiantService();

    // Create a new Etudiant instance with the provided data
    $etudiantToUpdate = new Etudiant($id, $nom, $prenom, $ville, $sexe, $decodedImage);

    // Call the update method in EtudiantService
    $es->update($etudiantToUpdate);
}
