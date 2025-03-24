package com.escaperoom.escaperoom.constant;

//Classe qui contient les permissions (authorities) associées aux rôles.
public class Authority {

 // Permissions pour un utilisateur basique : peut lire des données
 public static final String[] USER_AUTHORITIES = { "user:read" };

 // Ressources humaines : peut lire et mettre à jour
 public static final String[] HR_AUTHORITIES = { "user:read", "user:update" };

 // Manager : mêmes permissions que HR
 public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" };

 // Admin : peut lire, mettre à jour et créer
 public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create" };

 // Super admin : a tous les droits, y compris la suppression
 public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:update", "user:create", "user:delete" };
}