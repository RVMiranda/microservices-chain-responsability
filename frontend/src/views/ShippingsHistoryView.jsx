import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function ShippingsHistoryView({ showMsg }) {
  const [shippings, setShippings] = useState([]);
  const [loading, setLoading] = useState(false);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 15;

  // Search states
  const [searchShippingOrderId, setSearchShippingOrderId] = useState('');
  const [searchShippingEmail, setSearchShippingEmail] = useState('');
  const [searchShippingsActive, setSearchShippingsActive] = useState(false);
  const [searchShippingsResultEmpty, setSearchShippingsResultEmpty] = useState(false);
  const [searchShippingsEmptyMessage, setSearchShippingsEmptyMessage] = useState('');

  useEffect(() => {
    fetchShippings();
  }, []);

  const fetchShippings = async () => {
    try {
      const res = await fetch(`${API_BASE}/envios`);
      const data = await res.json();
      if (data.status === 'SUCCESS' || data.data) {
        setShippings(data.data || []);
      }
    } catch (err) {
      console.error('Error fetching shippings:', err);
    }
  };

  // Email validation helper
  const isValidEmail = (email) => {
    if (!email.includes('@')) return false;
    const suffixes = ['.com', '.mx', '.xy', '.ck', '.net', '.org', '.edu', '.co', '.info'];
    return suffixes.some(suffix => email.toLowerCase().endsWith(suffix));
  };

  const handleSearchShippingByOrderId = async (e) => {
    e.preventDefault();
    if (!searchShippingOrderId.trim()) {
      showMsg('Ingresa un ID de orden válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchShippingsResultEmpty(false);
    setSearchShippingsEmptyMessage('');
    setCurrentPage(1);
    try {
      const res = await fetch(`${API_BASE}/envios/orden/${searchShippingOrderId.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        const list = data.data || [];
        setShippings(list);
        setSearchShippingsActive(true);
        if (list.length === 0) {
          setSearchShippingsResultEmpty(true);
          setSearchShippingsEmptyMessage('No hay envíos registrados para esta orden.');
        }
        showMsg(`Búsqueda completada. Encontrados: ${list.length}`);
      } else {
        setShippings([]);
        setSearchShippingsActive(true);
        setSearchShippingsResultEmpty(true);
        setSearchShippingsEmptyMessage('No hay envíos registrados para esta orden.');
        showMsg('No se encontraron envíos.', 'error');
      }
    } catch (err) {
      setShippings([]);
      setSearchShippingsActive(true);
      setSearchShippingsResultEmpty(true);
      setSearchShippingsEmptyMessage('No hay envíos registrados para esta orden.');
      showMsg('No se encontraron envíos.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchShippingByEmail = async (e) => {
    e.preventDefault();
    if (!searchShippingEmail.trim()) {
      showMsg('Ingresa un correo de usuario.', 'error');
      return;
    }
    if (!isValidEmail(searchShippingEmail)) {
      showMsg('Formato de correo no válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchShippingsResultEmpty(false);
    setSearchShippingsEmptyMessage('');
    setCurrentPage(1);
    try {
      // 1. Get user orders
      const orderRes = await fetch(`${API_BASE}/ordenes/usuario/${searchShippingEmail.trim()}`);
      const orderData = await orderRes.json();
      const userOrders = orderData.data || [];
      
      if (userOrders.length === 0) {
        setShippings([]);
        setSearchShippingsActive(true);
        setSearchShippingsResultEmpty(true);
        setSearchShippingsEmptyMessage('No hay envíos registrados para este correo.');
        showMsg('No se encontraron órdenes para este correo.', 'error');
        return;
      }
      
      const orderIds = userOrders.map(o => o.id);
      
      // 2. Fetch all shippings from postgres
      const shipRes = await fetch(`${API_BASE}/envios`);
      const shipData = await shipRes.json();
      const allShippings = shipData.data || [];
      
      // 3. Filter shippings in memory
      const filtered = allShippings.filter(s => orderIds.includes(s.orderId));
      
      setShippings(filtered);
      setSearchShippingsActive(true);
      if (filtered.length === 0) {
        setSearchShippingsResultEmpty(true);
        setSearchShippingsEmptyMessage('No hay envíos registrados para este correo.');
      }
      showMsg(`Búsqueda completada. Encontrados: ${filtered.length}`);
    } catch (err) {
      setShippings([]);
      setSearchShippingsActive(true);
      setSearchShippingsResultEmpty(true);
      setSearchShippingsEmptyMessage('No hay envíos registrados para este correo.');
      showMsg('Error al buscar envíos por correo.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleClearShippingsSearch = () => {
    setSearchShippingOrderId('');
    setSearchShippingEmail('');
    setSearchShippingsActive(false);
    setSearchShippingsResultEmpty(false);
    setSearchShippingsEmptyMessage('');
    setCurrentPage(1);
    fetchShippings();
  };

  const sortedShippings = shippings.slice().sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentShippings = sortedShippings.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(sortedShippings.length / itemsPerPage);

  return (
    <div className="tab-content-wrapper fade-in">
      <div className="tab-content-full">
        <section className="table-section">
          <h2>Envíos Programados (Postgres)</h2>

          {/* ── DOUBLE SEARCH BAR (ORDER ID & EMAIL) ── */}
          <div className="search-bars-grid">
            <form onSubmit={handleSearchShippingByOrderId} className="search-form">
              <input
                type="text"
                placeholder="Buscar por ID de Orden..."
                value={searchShippingOrderId}
                onChange={e => setSearchShippingOrderId(e.target.value)}
              />
              <button type="submit" disabled={loading} className="btn btn-primary btn-search">Buscar Orden ID</button>
            </form>

            <form onSubmit={handleSearchShippingByEmail} className="search-form">
              <input
                type="text"
                placeholder="Buscar por Email de Usuario..."
                value={searchShippingEmail}
                onChange={e => setSearchShippingEmail(e.target.value)}
              />
              <button type="submit" disabled={loading} className="btn btn-primary btn-search">Buscar Email</button>
            </form>

            {searchShippingsActive && (
              <button type="button" onClick={handleClearShippingsSearch} className="btn btn-secondary btn-clear-wide">
                Limpiar Filtros
              </button>
            )}
          </div>

          <div className="table-wrapper">
            <table className="custom-table">
              <thead>
                <tr>
                  <th>Envío ID</th>
                  <th>Orden ID</th>
                  <th>Estado</th>
                  <th>Fecha de Creación</th>
                  <th>Fecha de Envío</th>
                </tr>
              </thead>
              <tbody>
                {sortedShippings.length === 0 ? (
                  <tr>
                    <td colSpan="5" className="empty-row">
                      {searchShippingsResultEmpty ? searchShippingsEmptyMessage : 'No hay envíos programados registrados.'}
                    </td>
                  </tr>
                ) : (
                  currentShippings.map(s => (
                    <tr key={s.id}>
                      <td className="code-id">{s.id.substring(0, 8)}...</td>
                      <td className="code-id">{s.orderId.substring(0, 8)}...</td>
                      <td>
                        <span className={`status-badge ${s.status.toLowerCase()}`}>
                          {s.status}
                        </span>
                      </td>
                      <td className="text-secondary" style={{ fontSize: '0.8rem' }}>
                        {s.createdAt ? new Date(s.createdAt).toLocaleString() : 'N/A'}
                      </td>
                      <td className="text-secondary" style={{ fontSize: '0.8rem' }}>
                        {s.processedAt ? new Date(s.processedAt).toLocaleString() : 'Pendiente de Procesamiento'}
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
                Mostrando {indexOfFirstItem + 1} - {Math.min(indexOfLastItem, sortedShippings.length)} de {sortedShippings.length} envíos
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
    </div>
  );
}
