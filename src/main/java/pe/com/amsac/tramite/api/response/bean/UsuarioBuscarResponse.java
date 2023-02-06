package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class UsuarioBuscarResponse {

    private String id;
    private String usuario;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String email;
    private PersonaResponse persona;
    private String dependenciaNombre;
    private String dependenciaId;
    private String cargoNombre;
    private String cargoId;
    private String dependenciaSigla;
    private String tipoUsuario;

    @Mapping("usuario.id")
    public String getId(){return id;}
    @Mapping("usuario.usuario")
    public String getUsuario(){return usuario;}
    @Mapping("usuario.nombre")
    public String getNombre(){return nombre;}
    @Mapping("usuario.apePaterno")
    public String getApePaterno(){return apePaterno;}
    @Mapping("usuario.apeMaterno")
    public String getApeMaterno(){return apeMaterno;}
    @Mapping("usuario.email")
    public String getEmail(){return email;}
    @Mapping("usuario.persona")
    public PersonaResponse getPersona(){return persona;}
    @Mapping("usuario.tipoUsuario")
    public String getTipoUsuario(){return tipoUsuario;}
    @Mapping("cargo.dependencia.nombre")
    public String getDependenciaNombre(){return dependenciaNombre;}
    @Mapping("cargo.dependencia.id")
    public String getDependenciaId(){return dependenciaId;}
    @Mapping("cargo.nombre")
    public String getCargoNombre(){return cargoNombre;}
    @Mapping("cargo.id")
    public String getCargoId(){return cargoId;}
    @Mapping("cargo.dependencia.sigla")
    public String getDependenciaSigla(){return dependenciaSigla;}


}
