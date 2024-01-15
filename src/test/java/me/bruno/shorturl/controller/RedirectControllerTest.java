package me.bruno.shorturl.controller;

import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.service.ShortURLService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RedirectControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ShortURLService shortURLService;

    ShortURLEntity shortURLEntity;

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
    }

    @Test
    void testGetRedirectByCode_Successfull_ReturnRedirect() throws Exception {
        String code = "123";

        when(shortURLService.getByCode(eq(shortURLEntity.getCode())))
                .thenReturn(shortURLEntity);

        mvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(shortURLEntity.getRedirectUrl()));
    }

    @Test
    void testGetRedirectByCode_NotFound_ReturnNotFound() throws Exception {
        String code = "code"; // invalid code

        when(shortURLService.getByCode(eq(shortURLEntity.getCode())))
                .thenReturn(shortURLEntity);

        mvc.perform(get("/" + code))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRedirectByCode_EmptyText_ReturnNotFound() throws Exception {
        String code = ""; // empty code

        when(shortURLService.getByCode(eq(shortURLEntity.getCode())))
                .thenReturn(shortURLEntity);

        mvc.perform(get("/" + code))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRedirectByCode_Deleted_ReturnNotFound() throws Exception {
        String code = shortURLEntity.getCode();

        shortURLEntity.setDeletedAt(OffsetDateTime.now()); // deleted

        when(shortURLService.getByCode(eq(shortURLEntity.getCode())))
                .thenReturn(shortURLEntity);

        mvc.perform(get("/" + code))
                .andExpect(status().isNotFound());
    }

}
