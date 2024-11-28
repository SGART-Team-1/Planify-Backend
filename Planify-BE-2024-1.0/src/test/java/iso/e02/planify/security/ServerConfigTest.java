package iso.e02.planify.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import static org.mockito.Mockito.*;

class ServerConfigTest {

    private ServerConfig serverConfig;

    @BeforeEach
    void setUp() {
        serverConfig = new ServerConfig();
    }

    @Test
    void testServletContainer() {
        // Given
        TomcatServletWebServerFactory factory = mock(TomcatServletWebServerFactory.class);
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer = serverConfig.servletContainer();

        // When
        customizer.customize(factory);

        // Then
        verify(factory, times(1)).addConnectorCustomizers(any());
    }

    
}
