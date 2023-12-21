package com.contractar.microserviciousuario;

import org.locationtech.jts.geom.Point;
import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.contractar.microserviciocommons.usuarios.UbicacionDeserializer;
import com.contractar.microserviciocommons.usuarios.UbicacionSerializer;
import com.contractar.microserviciocommons.usuarios.serialization.UserDetailsDeserializer;
import com.contractar.microserviciousuario.models.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MicroservicioUsuarioConfiguration {
    @Bean
    public JtsModule registerJtsModule() {
        ObjectMapper objectMapper = new ObjectMapper();
        JtsModule jtsModule = new JtsModule();
        jtsModule.addSerializer(Point.class, new UbicacionSerializer());
        jtsModule.addDeserializer(Point.class, new UbicacionDeserializer());
        jtsModule.addDeserializer(Usuario.class, new UserDetailsDeserializer());
        objectMapper.registerModule(jtsModule);
        return jtsModule;
    }
}
