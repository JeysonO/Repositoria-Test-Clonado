package pe.com.amsac.tramite.api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {

                DecodedJWT decoded = JWT.decode(jwt);
                String tokenData = new String(Base64.getDecoder().decode(decoded.getPayload()));
                ObjectMapper mapper = new ObjectMapper();
                DatosToken datosToken = (DatosToken) mapper.readValue(tokenData, DatosToken.class);
                datosToken.setToken(jwt);
                if(org.apache.commons.lang3.StringUtils.isBlank(datosToken.getIdUser())){
                    //Si no hay idUser, entonces obtenemos los datos del usuario de la bd para complementaer el dato
                    String uri = env.getProperty("app.url.seguridad") + "/usuarios/obtener-usuario-by-id";
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", String.format("%s %s", "Bearer", jwt));
                    HttpEntity entity = new HttpEntity<>(null, headers);
                    ResponseEntity<CommonResponse> responseConsultaUsuario = restTemplate.exchange(uri, HttpMethod.GET,entity, new ParameterizedTypeReference<CommonResponse>() {});
                    if(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("id")!=null)
                        datosToken.setIdUser(((LinkedHashMap)responseConsultaUsuario.getBody().getData()).get("id").toString());
                }

                /*
                UserPrincipal userDetails = new UserPrincipal();
                //userDetails.setId(userId);
                userDetails.setToken(jwt);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                */

                //Agregamos el cargoId
                String dependenciaId = request.getHeader("dependenciaId");
                String cargoId = request.getHeader("cargoId");
                datosToken.setDependenciaId(dependenciaId);
                datosToken.setCargoId(cargoId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(datosToken, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

}
