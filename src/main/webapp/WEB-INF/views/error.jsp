<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ include file="layout/taglibs.jspf" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
<!DOCTYPE html>
<html lang="es">
<head>
<c:set var="tituloPagina" value="Error" />
<%@ include file="layout/head.jspf" %>
</head>
<body>
	<div class="login-page">
		<div class="card-panel p-4 text-center" style="max-width: 460px">
			<i class="bi ${codigo == 404 ? 'bi-signpost-split' : 'bi-bug'}" style="font-size: 2.6rem; color: var(--accent)"></i>
			<h4 class="mt-2">
				<c:choose>
					<c:when test="${codigo == 404}">Página no encontrada</c:when>
					<c:otherwise>Ocurrió un error</c:otherwise>
				</c:choose>
			</h4>
			<p class="text-muted-2">
				<c:choose>
					<c:when test="${codigo == 404}">La dirección que buscas no existe.</c:when>
					<c:otherwise>No se pudo completar la operación. El detalle quedó registrado en la consola de Tomcat.</c:otherwise>
				</c:choose>
			</p>
			<a class="btn btn-accent" href="${ctx}/dashboard"><i class="bi bi-house me-1"></i> Volver al inicio</a>
		</div>
	</div>
</body>
</html>
