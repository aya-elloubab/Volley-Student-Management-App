<?php

use service\EtudiantService;
use classes\Etudiant;

// Require necessary class files
require '../classes/Etudiant.php';


if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE.'/service/EtudiantService.php';
    delete();
}

function delete() {
    if (!isset($_POST['id'])) {
        // Handle the case where 'id' is not set
        header('Content-type: application/json');
        echo json_encode([
            "message" => "ID parameter is missing.",
            "status" => "error"
        ]);
        return;
    }

    $id = $_POST['id']; // Extract the student ID from the request
    $es = new EtudiantService();

    // Create an Etudiant instance with the ID
    $etudiantToDelete = new Etudiant($id, '', '', '', '', '');

    // Call delete method in EtudiantService
    $es->delete($etudiantToDelete); // Ensure delete method handles the Etudiant instance properly

    // Return a confirmation response
    header('Content-type: application/json');
    echo json_encode([
        "message" => "Student with ID $id has been deleted.",
        "status" => "success"
    ]);
}

