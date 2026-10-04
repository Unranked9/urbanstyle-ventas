<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/taglibs.jspf" %>
<!DOCTYPE html>
<html lang="es">
<head>
<c:set var="tituloPagina" value="Iniciar sesión" />
<%@ include file="layout/head.jspf" %>
</head>
<body>
	<div class="login-page">
		<div class="login-card">
			<div class="text-center mb-4">
				<i class="bi bi-bag-heart-fill" style="font-size: 2.6rem; color: var(--accent)"></i>
				<h3 class="fw-bold mt-2 mb-0" style="letter-spacing: 2px">URBANSTYLE</h3>
				<div class="text-muted-2 small">Sistema de ventas</div>
			</div>

			<div class="card-panel p-4">
				<c:if test="${not empty error}">
					<div class="alert alert-danger py-2 small"><i class="bi bi-exclamation-triangle me-1"></i><c:out value="${error}" /></div>
				</c:if>
				<form method="post" action="${ctx}/login" autocomplete="off">
					<div class="mb-3">
						<label class="form-label" for="username">Usuario</label>
						<div class="input-group">
							<span class="input-group-text form-control-dark"><i class="bi bi-person"></i></span>
							<input class="form-control form-control-dark" id="username" name="username" maxlength="50"
								value="<c:out value='${username}' />" required autofocus>
						</div>
					</div>
					<div class="mb-4">
						<label class="form-label" for="password">Contraseña</label>
						<div class="input-group">
							<span class="input-group-text form-control-dark"><i class="bi bi-lock"></i></span>
							<input type="password" class="form-control form-control-dark" id="password" name="password" maxlength="72" required>
						</div>
					</div>
					<button class="btn btn-accent w-100" type="submit"><i class="bi bi-box-arrow-in-right me-1"></i> Ingresar</button>
				</form>
			</div>
			<p class="text-center text-muted-2 small mt-3 mb-0">UrbanStyle v2 · Maven + JPA</p>
		</div>
	</div>
</body>
</html>
