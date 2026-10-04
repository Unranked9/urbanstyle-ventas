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
			<c:set var="u" value="${perfil}" />
			<div class="row g-3">
				<div class="col-lg-4">
					<div class="card-panel p-4 text-center h-100">
						<div class="avatar-circle lg mx-auto mb-3"><c:out value="${us.iniciales}" /></div>
						<h5 class="mb-1"><c:out value="${u.nombreCompleto}" /></h5>
						<div class="text-muted-2 mb-2">@<c:out value="${u.username}" /></div>
						<span class="badge badge-rol">${u.rol.etiqueta}</span>
						<hr style="border-color: var(--border)">
						<div class="text-start small">
							<div class="d-flex justify-content-between py-1"><span class="text-muted-2">DNI</span><span>${u.dni}</span></div>
							<div class="d-flex justify-content-between py-1"><span class="text-muted-2">Registrado</span><span>${u.fechaRegistroTexto}</span></div>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<form class="card-panel p-3 h-100" method="post" action="${ctx}/perfil/actualizar">
						<h6 class="fw-semibold mb-3">Mis datos</h6>
						<div class="mb-3"><label class="form-label">Nombres</label><input class="form-control form-control-dark" name="nombres" maxlength="60" required value="<c:out value='${u.nombres}' />"></div>
						<div class="mb-3"><label class="form-label">Apellidos</label><input class="form-control form-control-dark" name="apellidos" maxlength="60" required value="<c:out value='${u.apellidos}' />"></div>
						<div class="mb-3"><label class="form-label">Teléfono</label><input class="form-control form-control-dark" name="telefono" maxlength="15" value="<c:out value='${u.telefono}' />"></div>
						<small class="text-muted-2 d-block mb-3">El DNI, usuario y rol solo los cambia un administrador.</small>
						<button class="btn btn-accent w-100"><i class="bi bi-save me-1"></i> Guardar</button>
					</form>
				</div>
				<div class="col-lg-4">
					<form class="card-panel p-3 h-100" method="post" action="${ctx}/perfil/password">
						<h6 class="fw-semibold mb-3">Cambiar contraseña</h6>
						<div class="mb-3"><label class="form-label">Contraseña actual</label><input type="password" class="form-control form-control-dark" name="actual" required></div>
						<div class="mb-3"><label class="form-label">Nueva contraseña</label><input type="password" class="form-control form-control-dark" name="nueva" minlength="6" maxlength="72" required></div>
						<div class="mb-3"><label class="form-label">Confirmar nueva contraseña</label><input type="password" class="form-control form-control-dark" name="confirmacion" minlength="6" maxlength="72" required></div>
						<button class="btn btn-accent w-100"><i class="bi bi-key me-1"></i> Actualizar contraseña</button>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
</body>
</html>
