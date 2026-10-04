/* Utilidades comunes del panel UrbanStyle */

function toggleSidebar() {
	document.getElementById('sidebar')?.classList.toggle('open');
}

(function reloj() {
	const el = document.getElementById('relojTopbar');
	if (!el) return;
	const pintar = () => {
		el.textContent = new Date().toLocaleDateString('es-PE', {
			weekday: 'short', day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
		});
	};
	pintar();
	setInterval(pintar, 60000);
})();

const SWAL_TEMA = { background: '#0f3460', color: '#eaeaea', confirmButtonColor: '#e94560', cancelButtonColor: '#6c757d' };

function alerta(icono, titulo, texto) {
	return Swal.fire({ ...SWAL_TEMA, icon: icono, title: titulo, text: texto });
}

/**
 * Formularios con data-confirmar="mensaje": piden confirmación antes de enviarse.
 * Se usa en desactivar, anular, eliminar...
 */
document.addEventListener('submit', (e) => {
	const form = e.target;
	const mensaje = form.dataset.confirmar;
	if (!mensaje || form.dataset.confirmado === '1') return;
	e.preventDefault();
	Swal.fire({
		...SWAL_TEMA, icon: 'warning', title: '¿Estás seguro?', text: mensaje,
		showCancelButton: true, confirmButtonText: 'Sí, continuar', cancelButtonText: 'Cancelar'
	}).then((r) => {
		if (r.isConfirmed) {
			form.dataset.confirmado = '1';
			form.submit();
		}
	});
});

/** Formatea un número como moneda peruana: 1234.5 -> "S/ 1,234.50" */
function soles(valor) {
	return 'S/ ' + Number(valor || 0).toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

/** Escapa texto antes de insertarlo como HTML. */
function esc(texto) {
	return String(texto ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

/** URL de imagen guardada (image_url = "uploads/archivo.webp"). */
function urlImagen(ruta) {
	return ruta ? `${window.APP_CTX}/${ruta}` : null;
}
