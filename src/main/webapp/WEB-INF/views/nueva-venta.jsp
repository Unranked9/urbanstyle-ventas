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
		<div class="page-body" id="appVenta" data-igv="${igvPorcentaje}" data-limite-dni="${montoBoletaDni}">
			<%@ include file="layout/flash.jspf" %>
			<div class="row g-3">

				<%-- Columna izquierda: búsqueda de productos --%>
				<div class="col-lg-7">
					<div class="card-panel p-3 h-100">
						<div class="input-group mb-3">
							<span class="input-group-text form-control-dark"><i class="bi bi-search"></i></span>
							<input class="form-control form-control-dark" id="buscarProducto" autocomplete="off"
								placeholder="Buscar por producto, color, talla, categoría o marca...">
						</div>
						<div class="resultados-busqueda card-panel2" id="resultados">
							<div class="carrito-vacio"><i class="bi bi-hourglass-split"></i> Cargando productos...</div>
						</div>
						<small class="text-muted-2 d-block mt-2">Haz clic en una variante para agregarla. Se muestran hasta 40 resultados.</small>
					</div>
				</div>

				<%-- Columna derecha: cliente, comprobante, carrito y totales --%>
				<div class="col-lg-5">
					<div class="card-panel p-3 mb-3">
						<div class="d-flex justify-content-between align-items-center mb-2">
							<h6 class="fw-semibold mb-0"><i class="bi bi-person me-1"></i> Cliente</h6>
							<button class="btn btn-sm btn-outline-light" data-bs-toggle="modal" data-bs-target="#modalClienteRapido"><i class="bi bi-person-plus"></i> Nuevo</button>
						</div>
						<div id="clienteSeleccionado" class="d-none card-panel2 p-2 d-flex justify-content-between align-items-center">
							<div>
								<div class="fw-semibold" id="clienteNombre"></div>
								<small class="text-muted-2" id="clienteInfo"></small>
							</div>
							<button class="btn btn-sm btn-outline-danger" id="quitarCliente" title="Quitar cliente"><i class="bi bi-x-lg"></i></button>
						</div>
						<div class="position-relative" id="cajaBuscarCliente">
							<input class="form-control form-control-dark" id="buscarCliente" autocomplete="off" placeholder="Buscar cliente por nombre o DNI (opcional)">
							<div class="lista-clientes card-panel2 d-none" id="listaClientes"></div>
						</div>
					</div>

					<div class="card-panel p-3 mb-3">
						<h6 class="fw-semibold mb-2"><i class="bi bi-receipt me-1"></i> Comprobante</h6>
						<div class="d-flex gap-2 mb-2">
							<input type="radio" class="btn-check" name="tipoComprobante" id="tipoBoleta" value="BOLETA" checked>
							<label class="btn btn-outline-tipo flex-fill" for="tipoBoleta">Boleta</label>
							<input type="radio" class="btn-check" name="tipoComprobante" id="tipoFactura" value="FACTURA">
							<label class="btn btn-outline-tipo flex-fill" for="tipoFactura">Factura</label>
						</div>
						<input class="form-control form-control-dark" id="documento" inputmode="numeric" maxlength="8" placeholder="DNI del cliente (opcional)">
						<small class="text-muted-2" id="ayudaDocumento">Obligatorio si el total supera S/ <fmt:formatNumber value="${montoBoletaDni}" pattern="#,##0.00" />.</small>
					</div>

					<div class="card-panel p-3">
						<h6 class="fw-semibold mb-2"><i class="bi bi-cart3 me-1"></i> Carrito</h6>
						<div id="carrito"></div>
						<div class="totales mt-2">
							<div class="fila"><span class="text-muted-2">Subtotal</span><span id="subtotal">S/ 0.00</span></div>
							<div class="fila"><span class="text-muted-2">IGV (${igvPorcentaje}%)</span><span id="igv">S/ 0.00</span></div>
							<div class="fila total"><span>Total</span><span id="total">S/ 0.00</span></div>
						</div>
						<div class="d-flex gap-2 mt-3">
							<button class="btn btn-outline-light" id="vaciar"><i class="bi bi-trash"></i> Vaciar</button>
							<button class="btn btn-accent flex-fill" id="cobrar" disabled><i class="bi bi-check2-circle me-1"></i> Registrar venta</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="modal fade modal-dark" id="modalClienteRapido" tabindex="-1" aria-hidden="true">
	<div class="modal-dialog">
		<form class="modal-content" id="formClienteRapido">
			<div class="modal-header">
				<h5 class="modal-title">Registrar cliente</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
			</div>
			<div class="modal-body">
				<div class="row g-2">
					<div class="col-6"><label class="form-label">Nombres *</label><input class="form-control form-control-dark" name="nombres" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">Apellidos *</label><input class="form-control form-control-dark" name="apellidos" maxlength="60" required></div>
					<div class="col-6"><label class="form-label">DNI</label><input class="form-control form-control-dark" name="dni" maxlength="8" pattern="\d{8}" inputmode="numeric"></div>
					<div class="col-6"><label class="form-label">Teléfono</label><input class="form-control form-control-dark" name="telefono" maxlength="15"></div>
					<div class="col-12"><label class="form-label">Correo *</label><input type="email" class="form-control form-control-dark" name="email" maxlength="50" required></div>
					<div class="col-12">
						<div class="form-check form-switch mt-1">
							<input class="form-check-input" type="checkbox" name="mayorista" id="rapMayorista">
							<label class="form-check-label" for="rapMayorista">Cliente mayorista</label>
						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancelar</button>
				<button class="btn btn-accent"><i class="bi bi-save me-1"></i> Registrar</button>
			</div>
		</form>
	</div>
</div>
<%@ include file="layout/scripts.jspf" %>
<script src="${ctx}/assets/js/venta.js"></script>
</body>
</html>
