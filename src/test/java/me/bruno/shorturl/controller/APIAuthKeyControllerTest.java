package me.bruno.shorturl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.bruno.shorturl.dto.APIAuthKeyDTO;
import me.bruno.shorturl.entity.APIAuthKeyEntity;
import me.bruno.shorturl.service.APIAuthKeyService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class APIAuthKeyControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    APIAuthKeyService apiAuthKeyService;

    // json mapper
    ObjectMapper mapper = new ObjectMapper();

    APIAuthKeyEntity apiAuthKeyEntity;

    APIAuthKeyDTO apiAuthKeyDTO;

    @BeforeEach
    void setUp() {
        apiAuthKeyEntity = APIAuthKeyEntity.builder()
                .key("123")
                .name("Key")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        apiAuthKeyDTO = APIAuthKeyDTO.builder()
                .key("123")
                .name("Key")
                .build();
    }

    @Test
    void testAuth_Successfull_ReturnBadRequest() throws Exception {
        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        post("/api/v1/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("")
                                .header("API-Key", "password")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNoAuth_Error_ReturnForbidden() throws Exception {
        when(apiAuthKeyService.validateKey(eq("password-1")))  // invalid key
                .thenReturn(true);

        mvc.perform(
                        post("/api/v1/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("")
                                .header("API-Key", "password-2")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateAndSaveAPIAuthKey_Successfull_ReturnCreated() throws Exception {
        String key = apiAuthKeyEntity.getKey();
        apiAuthKeyDTO.setKey(null);

        when(apiAuthKeyService.save(eq(apiAuthKeyDTO)))
                .thenReturn(apiAuthKeyEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        post("/api/v1/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(apiAuthKeyDTO))
                                .header("API-Key", "password")
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/auth/" + key));

        ArgumentCaptor<APIAuthKeyDTO> captor = ArgumentCaptor.forClass(APIAuthKeyDTO.class);
        verify(apiAuthKeyService).save(captor.capture());

        APIAuthKeyDTO capturedAPIAuthKeyDTO = captor.getValue();

        assertNull(capturedAPIAuthKeyDTO.getKey());
        assertEquals(apiAuthKeyDTO.getKey(), capturedAPIAuthKeyDTO.getKey());
        assertEquals(apiAuthKeyDTO.getName(), capturedAPIAuthKeyDTO.getName());
    }

    @Test
    void testUpdateByIdAPIAuthKey_Successfull_ReturnAPIAuthKey() throws Exception {
        String key = apiAuthKeyEntity.getKey();

        when(apiAuthKeyService.getById(eq(key)))
                .thenReturn(apiAuthKeyEntity);

        when(apiAuthKeyService.save(any()))
                .thenReturn(apiAuthKeyEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        put("/api/v1/auth/" + key)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(apiAuthKeyDTO))
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key", Matchers.is(apiAuthKeyEntity.getKey())))
                .andExpect(jsonPath("$.name", Matchers.is(apiAuthKeyEntity.getName())));
    }

    @Test
    void testDeleteByIdAPIAuthKey_Successfull_ReturnAPIAuthKey() throws Exception {
        String key = apiAuthKeyEntity.getKey();

        when(apiAuthKeyService.getById(eq(key)))
                .thenReturn(apiAuthKeyEntity);

        doNothing().when(apiAuthKeyService)
                .removeById(eq(key));

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        delete("/api/v1/auth/" + key)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key", Matchers.is(apiAuthKeyEntity.getKey())))
                .andExpect(jsonPath("$.name", Matchers.is(apiAuthKeyEntity.getName())));

        verify(apiAuthKeyService, times(1)).removeById(eq(key));
    }

    @Test
    void testListAPIAuthKey_Successfull_ReturnListAPIAuthKey() throws Exception {
        when(apiAuthKeyService.getAll())
                .thenReturn(List.of(apiAuthKeyEntity));

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        get("/api/v1/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key", Matchers.is(apiAuthKeyEntity.getKey())))
                .andExpect(jsonPath("$[0].name", Matchers.is(apiAuthKeyEntity.getName())));
    }

}
