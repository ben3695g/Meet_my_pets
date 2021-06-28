package com.example.meetmypets.model;

import java.util.List;

public class User {
      String name;
      String email;
      String userImage;
      String petName;
      String petImage;

      public User() {
      }

      public User(String name, String email, String userImage, String petName, String petImage) {
            this.name = name;
            this.email = email;
            this.userImage = userImage;
            this.petName = petName;
            this.petImage = petImage;
      }

      public String getName() {
            return name;
      }

      public String getEmail() {
            return email;
      }

      public String getUserImage() {
            return userImage;
      }

      public String getPetName() {
            return petName;
      }

      public String getPetImage() {
            return petImage;
      }
}
