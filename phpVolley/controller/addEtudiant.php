<?php

use service\EtudiantService;
use classes\Etudiant;

include_once '../racine.php';
include_once '../service/EtudiantService.php';
extract($_GET);

$es = new EtudiantService();
$es->create(new Etudiant(1, $nom, $prenom, $ville, $sexe, $imageBase64));

header("location:../index.php");
