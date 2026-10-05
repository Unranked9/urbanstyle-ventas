package com.urbanstyle.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.urbanstyle.util.ImagenUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Sirve las imágenes guardadas fuera del proyecto: /uploads/{archivo}. */
@WebServlet("/uploads/*")
public class ImagenController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String info = req.getPathInfo();
        Path ruta = info == null ? null : ImagenUtil.resolver(info.substring(1));
        if (ruta == null || !Files.isRegularFile(ruta)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        res.setContentType(ImagenUtil.tipoContenido(ruta));
        res.setContentLengthLong(Files.size(ruta));
        res.setHeader("Cache-Control", "private, max-age=86400");
        Files.copy(ruta, res.getOutputStream());
    }
}
