<?php

use service\EtudiantService;

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    loadAll();
}

function loadAll() {
    $es = new EtudiantService();
    header('Content-type: application/json');

    // Fetch all data
    $data = $es->findAllApi();

    // Check if data is empty
    if (empty($data)) {
        echo json_encode(['error' => 'No data found']);
        return;
    }

    // Attempt to encode the data as JSON
    $json = json_encode($data);

    // Check for JSON encoding errors
    if (json_last_error() !== JSON_ERROR_NONE) {
        echo json_encode(['error' => 'JSON encoding error: ' . json_last_error_msg()]);
        return;
    }

    echo $json; // Output the JSON-encoded data
}

