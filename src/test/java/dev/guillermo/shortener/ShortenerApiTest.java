package dev.guillermo.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShortenerApiTest {
    @Autowired MockMvc mvc;

    private String shorten(String url) throws Exception {
        String body = mvc.perform(post("/api/shorten").contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"" + url + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return body.replaceAll(".*\"code\":\"([0-9a-zA-Z]+)\".*", "$1");
    }

    @Test
    void shortenRedirectAndCountClicks() throws Exception {
        String code = shorten("https://example.com/some/long/path?x=1");
        mvc.perform(get("/" + code)).andExpect(status().isFound())
           .andExpect(header().string("Location", "https://example.com/some/long/path?x=1"));
        mvc.perform(get("/" + code));
        mvc.perform(get("/api/stats/" + code)).andExpect(status().isOk()).andExpect(jsonPath("$.clicks").value(2));
    }

    @Test
    void rejectsInvalidUrls() throws Exception {
        for (String bad : new String[]{"not a url", "ftp://example.com/file", "javascript:alert(1)"}) {
            mvc.perform(post("/api/shorten").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"url\":\"" + bad + "\"}")).andExpect(status().isBadRequest());
        }
    }

    @Test
    void unknownCodeIs404() throws Exception {
        mvc.perform(get("/zzzzzz")).andExpect(status().isNotFound());
    }

    @Test
    void validatorAcceptsOnlyHttp() {
        assertTrue(ShortenerService.isValidUrl("https://github.com"));
        assertFalse(ShortenerService.isValidUrl("mailto:me@example.com"));
    }
}
