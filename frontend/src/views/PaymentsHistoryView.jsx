import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function PaymentsHistoryView({ showMsg }) {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(false);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;

  // Payment Search states
  const [searchPaymentId, setSearchPaymentId] = useState('');
  const [searchPaymentOrderId, setSearchPaymentOrderId] = useState('');
  const [searchPaymentsActive, setSearchPaymentsActive] = useState(false);
  const [searchPaymentsResultEmpty, setSearchPaymentsResultEmpty] = useState(false);
  const [searchPaymentsEmptyMessage, setSearchPaymentsEmptyMessage] = useState('');

  // Selected Payment
  const [selectedPayment, setSelectedPayment] = useState(null);

  // Load data
  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    try {
      const res = await fetch(`${API_BASE}/pagos`);
      const data = await res.json();
      if (data.status === 'SUCCESS' || data.data) {
        setPayments(data.data || []);
      }
    } catch (err) {
      console.error('Error fetching payments:', err);
    }
  };

  const handleSearchPaymentById = async (e) => {
    e.preventDefault();
    if (!searchPaymentId.trim()) {
      showMsg('Ingresa un ID de pago válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchPaymentsResultEmpty(false);
    setSearchPaymentsEmptyMessage('');
    setCurrentPage(1);
    try {
      const res = await fetch(`${API_BASE}/pagos/${searchPaymentId.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        setPayments([data.data]);
        setSearchPaymentsActive(true);
        showMsg('Pago encontrado.');
      } else {
        setPayments([]);
        setSearchPaymentsActive(true);
        setSearchPaymentsResultEmpty(true);
        setSearchPaymentsEmptyMessage('Pago no encontrado.');
        showMsg('Pago no encontrado.', 'error');
      }
    } catch (err) {
      setPayments([]);
      setSearchPaymentsActive(true);
      setSearchPaymentsResultEmpty(true);
      setSearchPaymentsEmptyMessage('Pago no encontrado.');
      showMsg('Pago no encontrado.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchPaymentByOrderId = async (e) => {
    e.preventDefault();
    if (!searchPaymentOrderId.trim()) {
      showMsg('Ingresa un ID de orden válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchPaymentsResultEmpty(false);
    setSearchPaymentsEmptyMessage('');
    setCurrentPage(1);
    try {
      const res = await fetch(`${API_BASE}/pagos/orden/${searchPaymentOrderId.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        const list = data.data || [];
        setPayments(list);
        setSearchPaymentsActive(true);
        if (list.length === 0) {
          setSearchPaymentsResultEmpty(true);
          setSearchPaymentsEmptyMessage('No hay pagos registrados para esta orden.');
        }
        showMsg(`Búsqueda completada. Encontrados: ${list.length}`);
      } else {
        setPayments([]);
        setSearchPaymentsActive(true);
        setSearchPaymentsResultEmpty(true);
        setSearchPaymentsEmptyMessage('No hay pagos registrados para esta orden.');
        showMsg('No se encontraron pagos.', 'error');
      }
    } catch (err) {
      setPayments([]);
      setSearchPaymentsActive(true);
      setSearchPaymentsResultEmpty(true);
      setSearchPaymentsEmptyMessage('No hay pagos registrados para esta orden.');
      showMsg('No se encontraron pagos.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleClearPaymentsSearch = () => {
    setSearchPaymentId('');
    setSearchPaymentOrderId('');
    setSearchPaymentsActive(false);
    setSearchPaymentsResultEmpty(false);
    setSearchPaymentsEmptyMessage('');
    setCurrentPage(1);
    fetchPayments();
  };

  const handleRefundPayment = async (id) => {
    if (!confirm('¿Estás seguro de solicitar el reembolso de este pago?')) return;
    setLoading(true);
    setCurrentPage(1);
    try {
      // Verificar si la orden ya fue enviada
      const resEnvio = await fetch(`${API_BASE}/envios/orden/${selectedPayment.orderId}`);
      const dataEnvio = await resEnvio.json();
      if (resEnvio.ok && dataEnvio.status !== 'ERROR' && dataEnvio.data && dataEnvio.data.length > 0) {
        const shipping = dataEnvio.data[0];
        if (shipping.status && shipping.status.toUpperCase() === 'SHIPPED') {
          showMsg('Lo sentimos, tu orden ya fue enviada y no puede ser reembolsada', 'error');
          setLoading(false);
          return;
        }
      }
    } catch (err) {
      console.error('Error al verificar el envío:', err);
    }

    try {
      const res = await fetch(`${API_BASE}/pagos/${id}/reembolso`, {
        method: 'PUT'
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        showMsg('Reembolso procesado exitosamente.');
        setSelectedPayment(data.data);
        fetchPayments();
      } else {
        showMsg(data.message || 'Error al procesar el reembolso.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const sortedPayments = payments.slice().sort((a, b) => new Date(b.processedAt) - new Date(a.processedAt));
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentPayments = sortedPayments.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(sortedPayments.length / itemsPerPage);

  return (
    <div className="tab-content-wrapper fade-in">
      {selectedPayment ? (
        // ── PAYMENT DETAIL VIEW ──
        <div className="detail-view-container">
          <button 
            onClick={() => { setSelectedPayment(null); }} 
            className="btn btn-secondary btn-back"
          >
            ← Volver al Historial
          </button>
          
          <div className="detail-card">
            <h2>Detalle del Pago</h2>
            <div className="detail-info">
              <div className="info-group">
                <label>ID del Pago</label>
                <p className="info-value code-id">{selectedPayment.id}</p>
              </div>
              <div className="info-group">
                <label>ID de la Orden</label>
                <p className="info-value code-id">{selectedPayment.orderId}</p>
              </div>
              <div className="info-group">
                <label>Email de Usuario</label>
                <p className="info-value text-secondary">{selectedPayment.userEmail}</p>
              </div>
              <div className="info-group">
                <label>Monto</label>
                <p className="info-value price-tag">${selectedPayment.amount.toFixed(2)}</p>
              </div>
              <div className="info-group">
                <label>Método de Pago</label>
                <p className="info-value bold">{selectedPayment.paymentMethod}</p>
              </div>
              <div className="info-group">
                <label>Estado del Pago</label>
                <div className="info-value">
                  <span className={`status-badge ${selectedPayment.status.toLowerCase()}`}>
                    {selectedPayment.status}
                  </span>
                </div>
              </div>
              <div className="info-group">
                <label>Procesado El</label>
                <p className="info-value text-secondary">{new Date(selectedPayment.processedAt).toLocaleString()}</p>
              </div>
              
              <div className="detail-actions">
                <button 
                  onClick={() => handleRefundPayment(selectedPayment.id)} 
                  className="btn btn-danger"
                  disabled={loading || selectedPayment.status === 'REEMBOLSADO'}
                >
                  {selectedPayment.status === 'REEMBOLSADO' ? 'Pago Reembolsado' : 'Solicitar Reembolso'}
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        // ── MAIN PAYMENTS LIST VIEW ──
        <div className="tab-content-full">
          <section className="table-section">
            <h2>Historial de Pagos</h2>

            {/* ── DOUBLE SEARCH BAR (PAYMENT ID & ORDER ID) ── */}
            <div className="search-bars-grid">
              <form onSubmit={handleSearchPaymentById} className="search-form">
                <input
                  type="text"
                  placeholder="Buscar por ID de Pago..."
                  value={searchPaymentId}
                  onChange={e => setSearchPaymentId(e.target.value)}
                />
                <button type="submit" className="btn btn-primary btn-search">Buscar Pago ID</button>
              </form>

              <form onSubmit={handleSearchPaymentByOrderId} className="search-form">
                <input
                  type="text"
                  placeholder="Buscar por ID de Orden..."
                  value={searchPaymentOrderId}
                  onChange={e => setSearchPaymentOrderId(e.target.value)}
                />
                <button type="submit" className="btn btn-primary btn-search">Buscar Orden ID</button>
              </form>

              {searchPaymentsActive && (
                <button type="button" onClick={handleClearPaymentsSearch} className="btn btn-secondary btn-clear-wide">
                  Limpiar Filtros
                </button>
              )}
            </div>

            <div className="table-wrapper">
              <table className="custom-table table-clickable">
                <thead>
                  <tr>
                    <th>Pago ID</th>
                    <th>Orden ID</th>
                    <th>Email</th>
                    <th>Monto</th>
                    <th>Método</th>
                    <th>Estado</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  {sortedPayments.length === 0 ? (
                    <tr>
                      <td colSpan="7" className="empty-row">
                        {searchPaymentsResultEmpty ? searchPaymentsEmptyMessage : 'No hay pagos registrados.'}
                      </td>
                    </tr>
                  ) : (
                    currentPayments.map(p => (
                      <tr 
                        key={p.id}
                        onClick={() => setSelectedPayment(p)}
                        title="Haga click para ver detalles y gestionar reembolso"
                      >
                        <td className="code-id">{p.id.substring(0, 8)}...</td>
                        <td className="code-id">{p.orderId.substring(0, 8)}...</td>
                        <td className="text-secondary">{p.userEmail}</td>
                        <td className="price-tag">${p.amount.toFixed(2)}</td>
                        <td>{p.paymentMethod}</td>
                        <td>
                          <span className={`status-badge ${p.status.toLowerCase()}`}>
                            {p.status}
                          </span>
                        </td>
                        <td className="text-secondary" style={{ fontSize: '0.8rem' }}>
                          {new Date(p.processedAt).toLocaleString()}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination Controls */}
            {totalPages > 1 && (
              <div className="pagination-container">
                <span className="pagination-info">
                  Mostrando {indexOfFirstItem + 1} - {Math.min(indexOfLastItem, sortedPayments.length)} de {sortedPayments.length} pagos
                </span>
                <div className="pagination-buttons">
                  <button 
                    onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))} 
                    disabled={currentPage === 1}
                    className="btn-page"
                  >
                    Anterior
                  </button>
                  {Array.from({ length: totalPages }, (_, i) => i + 1).map(pageNumber => (
                    <button
                      key={pageNumber}
                      onClick={() => setCurrentPage(pageNumber)}
                      className={`btn-page ${currentPage === pageNumber ? 'active' : ''}`}
                    >
                      {pageNumber}
                    </button>
                  ))}
                  <button 
                    onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))} 
                    disabled={currentPage === totalPages}
                    className="btn-page"
                  >
                    Siguiente
                  </button>
                </div>
              </div>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
