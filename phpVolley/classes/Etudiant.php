<?php
namespace classes;

class Etudiant {
    private $id;
    private $nom;
    private $prenom;
    private $ville;
    private $sexe;
    private $image;

    // Constructor
    public function __construct($id, $nom, $prenom, $ville, $sexe, $image) {
        $this->id = $id;
        $this->nom = $nom;
        $this->prenom = $prenom;
        $this->ville = $ville;
        $this->sexe = $sexe;
        $this->image = $image; // Expecting raw binary data
    }

    // Getters
    public function getId() {
        return $this->id;
    }

    public function getNom() {
        return $this->nom;
    }

    public function getPrenom() {
        return $this->prenom;
    }

    public function getVille() {
        return $this->ville;
    }

    public function getSexe() {
        return $this->sexe;
    }

    public function getImage() {
        return $this->image;
    }
}
