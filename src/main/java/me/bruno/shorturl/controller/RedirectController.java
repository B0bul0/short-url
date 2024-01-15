package me.bruno.shorturl.controller;

import lombok.extern.log4j.Log4j2;
import me.bruno.shorturl.entity.ShortURLEntity;
import me.bruno.shorturl.service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Main controller for redirecting by code
 */
@Log4j2
@RestController
@RequestMapping("/")
public class RedirectController {

    @Autowired
    private ShortURLService shortURLService;

    @GetMapping("/{code}")
    public RedirectView redirectByCode(@PathVariable("code") String code) {
        log.debug("Processing code: " + code);

        if (!StringUtils.hasText(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is empty");
        }

        ShortURLEntity shortURLEntity = shortURLService.getByCode(code);

        if (shortURLEntity == null || shortURLEntity.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "code not found");
        }

        return new RedirectView(shortURLEntity.getRedirectUrl());
    }

}
