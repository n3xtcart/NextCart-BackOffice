package org.example.dto;

public class RichiestaLoginDTO {
    private String email;
    private String password;

    public RichiestaLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
