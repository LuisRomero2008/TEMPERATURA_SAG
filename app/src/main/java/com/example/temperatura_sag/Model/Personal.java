package com.example.temperatura_sag.Model;

public class Personal {
    public String DNI;
    public String Nombre;
    public String Apellido;
    public String Area;
    public String Direccion;
    public String Telefono;

    public Personal(String DNI, String nombre, String apellido, String area, String direccion, String telefono) {
        this.DNI = DNI;
        Nombre = nombre;
        Apellido = apellido;
        Area = area;
        Direccion = direccion;
        Telefono = telefono;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }
}
