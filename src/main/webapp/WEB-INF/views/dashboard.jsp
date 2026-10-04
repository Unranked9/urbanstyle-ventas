<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head><%@ include file="layout/head.jspf" %></head>
<body>
<div class="layout-wrapper">
	<%@ include file="layout/sidebar.jspf" %>
	<div class="main-content">
		<%@ include file="layout/topbar.jspf" %>
		<div class="page-body">
			<%@ include file="layout/flash.jspf" %>
			<div class="card-panel p-4">
				<h4 class="mb-1">Bienvenido, <c:out value="${us.nombres}" /></h4>
				<p class="text-muted-2 mb-0">
					Iniciaste sesión como <b><c:out value="${us.rolEtiqueta}" /></b>.
					Usa el menú de la izquierda para ir a cada módulo.
				</p>
			</div>
			<p class="text-muted-2 small mt-3 mb-0">
				Dashboard provisional: las estadísticas de ventas se agregan en la última fase del proyecto.
			</p>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
