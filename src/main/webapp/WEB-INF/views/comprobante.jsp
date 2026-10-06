<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="layout/taglibs.jspf" %>
<c:set var="v" value="${venta}" />
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>${v.comprobante.numeroCompleto} · UrbanStyle</title>
<style>
	body { font-family: 'Courier New', monospace; background: #eee; margin: 0; padding: 20px; color: #111; }
	.ticket { background: #fff; width: 320px; margin: 0 auto; padding: 18px 16px; box-shadow: 0 2px 10px rgba(0,0,0,.15); }
	.centro { text-align: center; }
	.linea { border-top: 1px dashed #555; margin: 8px 0; }
	table { width: 100%; border-collapse: collapse; font-size: 12px; }
	td { padding: 2px 0; vertical-align: top; }
	.der { text-align: right; }
	.small { font-size: 11px; }
	.total { font-size: 15px; font-weight: bold; }
	.anulada { color: #c00; font-weight: bold; text-align: center; border: 2px solid #c00; padding: 4px; margin: 6px 0; }
	.acciones { text-align: center; margin-top: 14px; }
	.acciones button { font-family: inherit; padding: 6px 14px; cursor: pointer; }
	@media print { body { background: #fff; padding: 0; } .ticket { box-shadow: none; } .acciones { display: none; } }
</style>
</head>
<body>
	<div class="ticket">
		<div class="centro">
			<div style="font-size: 18px; font-weight: bold; letter-spacing: 2px">URBANSTYLE</div>
			<div class="small">Ropa urbana</div>
			<div class="linea"></div>
			<div style="font-weight: bold">${fn:toUpperCase(v.comprobante.tipo.etiqueta)}</div>
			<div style="font-weight: bold">${v.comprobante.numeroCompleto}</div>
		</div>
		<c:if test="${v.anulada}"><div class="anulada">VENTA ANULADA</div></c:if>
		<div class="linea"></div>
		<table class="small">
			<tr><td>Fecha:</td><td class="der">${v.comprobante.fechaEmisionTexto}</td></tr>
			<tr><td>Cliente:</td><td class="der"><c:out value="${empty v.cliente ? 'CLIENTE VARIOS' : v.cliente.nombreCompleto}" /></td></tr>
			<c:if test="${not empty v.comprobante.dniCliente}"><tr><td>DNI:</td><td class="der">${v.comprobante.dniCliente}</td></tr></c:if>
			<c:if test="${not empty v.comprobante.rucCliente}"><tr><td>RUC:</td><td class="der">${v.comprobante.rucCliente}</td></tr></c:if>
			<tr><td>Atendió:</td><td class="der"><c:out value="${v.usuario.nombreCompleto}" /></td></tr>
		</table>
		<div class="linea"></div>
		<table>
			<c:forEach var="d" items="${v.detalles}">
				<tr><td colspan="2"><c:out value="${d.variante.producto.nombre}" /> <span class="small">(<c:out value="${d.variante.descripcionCorta}" />)</span></td></tr>
				<tr><td class="small">${d.cantidad} x <fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00" /></td>
					<td class="der"><fmt:formatNumber value="${d.totalLinea}" pattern="#,##0.00" /></td></tr>
			</c:forEach>
		</table>
		<div class="linea"></div>
		<table>
			<tr><td>OP. GRAVADA</td><td class="der">S/ <fmt:formatNumber value="${v.subtotal}" pattern="#,##0.00" /></td></tr>
			<tr><td>IGV (${igvPorcentaje}%)</td><td class="der">S/ <fmt:formatNumber value="${v.igv}" pattern="#,##0.00" /></td></tr>
			<tr class="total"><td>TOTAL</td><td class="der">S/ <fmt:formatNumber value="${v.total}" pattern="#,##0.00" /></td></tr>
		</table>
		<div class="linea"></div>
		<div class="centro small">${v.totalPrendas} prenda(s) · Venta #${v.id}<br>¡Gracias por tu compra!</div>
	</div>
	<div class="acciones">
		<button onclick="window.print()">Imprimir</button>
		<button onclick="window.close()">Cerrar</button>
	</div>
</body>
</html>
