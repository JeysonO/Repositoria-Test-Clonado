package pe.com.amsac.tramite.api.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class UserPrincipal {

	private static final long serialVersionUID = 269050903881546680L;

	private String id;

    private String nombre;

    private String usuario;

    @JsonIgnore
    private String apePaterno;
    
    @JsonIgnore
    private String apeMaterno;

    //@JsonIgnore
    //private String password;
    
    @JsonIgnore
    private String token;

    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal() {
    	super();
    }
    
    public UserPrincipal(String id, String nombre, String usuario, String apePaterno, String apeMaterno, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.apePaterno = apePaterno;
        this.apeMaterno = apeMaterno;
        //this.password = password;
        this.authorities = authorities;
    }

    
}
