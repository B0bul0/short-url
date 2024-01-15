package me.bruno.shorturl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.bruno.shorturl.dto.ShortURLDTO;
import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.service.APIAuthKeyService;
import me.bruno.shorturl.service.ShortURLService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ShortURLControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ShortURLService shortURLService;

    @MockBean
    APIAuthKeyService apiAuthKeyService;

    // json mapper
    ObjectMapper mapper = new ObjectMapper();

    ShortURLEntity shortURLEntity;

    ShortURLDTO shortURLDTO;

    @BeforeEach
    void setUp() {
        shortURLEntity = ShortURLEntity.builder()
                .id(UUID.randomUUID())
                .code("123")
                .redirectUrl("http://localhost")
                .note("note")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .deletedAt(null)
                .build();

        shortURLDTO = ShortURLDTO.builder()
                .id(null)
                .code("123")
                .redirectUrl("http://localhost")
                .note("note")
                .build();
    }

    @Test
    void testAuth_Successfull_ReturnBadRequest() throws Exception {
        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        post("/api/v1/shorturl")
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
                        post("/api/v1/shorturl")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("")
                                .header("API-Key", "password-2")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateAndSaveShortURL_Successfull_ReturnCreated() throws Exception {
        UUID uuid = shortURLEntity.getId();

        when(shortURLService.save(any()))
                .thenReturn(shortURLEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        post("/api/v1/shorturl")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(shortURLDTO))
                                .header("API-Key", "password")
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/shorturl/" + uuid));

        ArgumentCaptor<ShortURLDTO> captor = ArgumentCaptor.forClass(ShortURLDTO.class);
        verify(shortURLService).save(captor.capture());

        ShortURLDTO capturedShortURLDTO = captor.getValue();

        assertNull(capturedShortURLDTO.getId());
        assertEquals(shortURLDTO.getCode(), capturedShortURLDTO.getCode());
        assertEquals(shortURLDTO.getRedirectUrl(), capturedShortURLDTO.getRedirectUrl());
        assertEquals(shortURLDTO.getNote(), capturedShortURLDTO.getNote());
    }

    @Test
    void testGetById_Successfull_ReturnShortURL() throws Exception {
        UUID uuid = shortURLEntity.getId();

        when(shortURLService.getById(eq(uuid)))
                .thenReturn(shortURLEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        get("/api/v1/shorturl/" + uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(shortURLEntity.getId().toString())))
                .andExpect(jsonPath("$.code", Matchers.is(shortURLEntity.getCode())))
                .andExpect(jsonPath("$.redirectUrl", Matchers.is(shortURLEntity.getRedirectUrl())))
                .andExpect(jsonPath("$.note", Matchers.is(shortURLEntity.getNote())));
    }

    @Test
    void testGetByCode_Successfull_ReturnShortURL() throws Exception {
        String code = shortURLEntity.getCode();

        when(shortURLService.getByCode(eq(code)))
                .thenReturn(shortURLEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        get("/api/v1/shorturl/code/" + code)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(shortURLEntity.getId().toString())))
                .andExpect(jsonPath("$.code", Matchers.is(shortURLEntity.getCode())))
                .andExpect(jsonPath("$.redirectUrl", Matchers.is(shortURLEntity.getRedirectUrl())))
                .andExpect(jsonPath("$.note", Matchers.is(shortURLEntity.getNote())));
    }

    @Test
    void testUpdateByIdShortURL_Successfull_ReturnShortURL() throws Exception {
        UUID uuid = shortURLEntity.getId();

        when(shortURLService.getById(eq(uuid)))
                .thenReturn(shortURLEntity);

        when(shortURLService.save(any()))
                .thenReturn(shortURLEntity);

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        put("/api/v1/shorturl/" + uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(shortURLDTO))
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(shortURLEntity.getId().toString())))
                .andExpect(jsonPath("$.code", Matchers.is(shortURLEntity.getCode())))
                .andExpect(jsonPath("$.redirectUrl", Matchers.is(shortURLEntity.getRedirectUrl())))
                .andExpect(jsonPath("$.note", Matchers.is(shortURLEntity.getNote())));
    }

    @Test
    void testDeleteByIdShortURL_Successfull_ReturnShortURL() throws Exception {
        UUID uuid = shortURLEntity.getId();

        when(shortURLService.getById(eq(uuid)))
                .thenReturn(shortURLEntity);

        doNothing().when(shortURLService)
                .removeById(eq(uuid));

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        delete("/api/v1/shorturl/" + uuid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(shortURLEntity.getId().toString())))
                .andExpect(jsonPath("$.code", Matchers.is(shortURLEntity.getCode())))
                .andExpect(jsonPath("$.redirectUrl", Matchers.is(shortURLEntity.getRedirectUrl())))
                .andExpect(jsonPath("$.note", Matchers.is(shortURLEntity.getNote())))
        ;

        verify(shortURLService, times(1)).removeById(eq(uuid));
    }

    @Test
    void testListShortURL_Successfull_ReturnListShortURL() throws Exception {
        when(shortURLService.getAll())
                .thenReturn(List.of(shortURLEntity));

        when(apiAuthKeyService.validateKey(eq("password")))
                .thenReturn(true);

        mvc.perform(
                        get("/api/v1/shorturl")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("API-Key", "password")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(shortURLEntity.getId().toString())))
                .andExpect(jsonPath("$[0].code", Matchers.is(shortURLEntity.getCode())))
                .andExpect(jsonPath("$[0].redirectUrl", Matchers.is(shortURLEntity.getRedirectUrl())))
                .andExpect(jsonPath("$[0].note", Matchers.is(shortURLEntity.getNote())));
    }

}
