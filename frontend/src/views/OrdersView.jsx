import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function OrdersView({ showMsg }) {
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [orderBalances, setOrderBalances] = useState({}); // orderId -> balance
  const [loading, setLoading] = useState(false);

  // Order Search states
  const [searchOrderId, setSearchOrderId] = useState('');
  const [searchOrderEmail, setSearchOrderEmail] = useState('');
  const [searchOrdersActive, setSearchOrdersActive] = useState(false);
  const [searchOrdersResultEmpty, setSearchOrdersResultEmpty] = useState(false);
  const [searchOrdersEmptyMessage, setSearchOrdersEmptyMessage] = useState('');

  // Order Detail, Edit Status & Inline Payment states
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isEditingOrderStatus, setIsEditingOrderStatus] = useState(false);
  const [newOrderStatus, setNewOrderStatus] = useState('CREADA');
  const [inlinePayment, setInlinePayment] = useState({ amount: '', paymentMethod: 'CREDIT_CARD' });

  // Form states
  const [orderForm, setOrderForm] = useState({ productId: '', quantity: '', userEmail: '' });

  // Load data
  useEffect(() => {
    fetchProducts();
    fetchOrders();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await fetch(`${API_BASE}/productos`);
      const data = await res.json();
      if (data.status === 'SUCCESS' || data.data) {
        setProducts(data.data || []);
      }
    } catch (err) {
      console.error('Error fetching products:', err);
    }
  };

  const fetchOrders = async () => {
    try {
      const res = await fetch(`${API_BASE}/ordenes`);
      const data = await res.json();
      if (data.status === 'SUCCESS' || data.data) {
        const orderList = data.data || [];
        setOrders(orderList);
        orderList.forEach(order => {
          fetchOrderBalance(order.id);
        });
      }
    } catch (err) {
      console.error('Error fetching orders:', err);
    }
  };

  const fetchOrderBalance = async (orderId) => {
    try {
      const res = await fetch(`${API_BASE}/ordenes/${orderId}/saldo-restante`);
      const data = await res.json();
      if (data.data !== undefined) {
        setOrderBalances(prev => ({ ...prev, [orderId]: data.data }));
      }
    } catch (err) {
      console.error(`Error fetching balance for order ${orderId}:`, err);
    }
  };

  // Email Validation helper
  const isValidEmail = (email) => {
    if (!email.includes('@')) return false;
    const suffixes = ['.com', '.mx', '.xy', '.ck', '.net', '.org', '.edu', '.co', '.info'];
    return suffixes.some(suffix => email.toLowerCase().endsWith(suffix));
  };

  // Order Search functions
  const handleSearchOrderById = async (e) => {
    e.preventDefault();
    if (!searchOrderId.trim()) {
      showMsg('Ingresa un ID de orden válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchOrdersResultEmpty(false);
    setSearchOrdersEmptyMessage('');
    try {
      const res = await fetch(`${API_BASE}/ordenes/${searchOrderId.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        setOrders([data.data]);
        setSearchOrdersActive(true);
        fetchOrderBalance(data.data.id);
        showMsg('Orden encontrada.');
      } else {
        setOrders([]);
        setSearchOrdersActive(true);
        setSearchOrdersResultEmpty(true);
        setSearchOrdersEmptyMessage('Orden no encontrada.');
        showMsg('Orden no encontrada.', 'error');
      }
    } catch (err) {
      setOrders([]);
      setSearchOrdersActive(true);
      setSearchOrdersResultEmpty(true);
      setSearchOrdersEmptyMessage('Orden no encontrada.');
      showMsg('Orden no encontrada.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchOrderByEmail = async (e) => {
    e.preventDefault();
    if (!searchOrderEmail.trim()) {
      showMsg('Ingresa un correo de usuario.', 'error');
      return;
    }
    if (!isValidEmail(searchOrderEmail)) {
      showMsg('Formato de correo no válido.', 'error');
      return;
    }
    setLoading(true);
    setSearchOrdersResultEmpty(false);
    setSearchOrdersEmptyMessage('');
    try {
      const res = await fetch(`${API_BASE}/ordenes/usuario/${searchOrderEmail.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        const list = data.data || [];
        setOrders(list);
        setSearchOrdersActive(true);
        if (list.length === 0) {
          setSearchOrdersResultEmpty(true);
          setSearchOrdersEmptyMessage('No hay órdenes disponibles para este correo.');
        } else {
          list.forEach(o => fetchOrderBalance(o.id));
        }
        showMsg(`Búsqueda completada. Encontradas: ${list.length}`);
      } else {
        setOrders([]);
        setSearchOrdersActive(true);
        setSearchOrdersResultEmpty(true);
        setSearchOrdersEmptyMessage('No hay órdenes disponibles para este correo.');
        showMsg('No se encontraron órdenes.', 'error');
      }
    } catch (err) {
      setOrders([]);
      setSearchOrdersActive(true);
      setSearchOrdersResultEmpty(true);
      setSearchOrdersEmptyMessage('No hay órdenes disponibles para este correo.');
      showMsg('No se encontraron órdenes.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleClearOrdersSearch = () => {
    setSearchOrderId('');
    setSearchOrderEmail('');
    setSearchOrdersActive(false);
    setSearchOrdersResultEmpty(false);
    setSearchOrdersEmptyMessage('');
    fetchOrders();
  };

  // Order Actions
  const handleOrderSubmit = async (e) => {
    e.preventDefault();
    const quantityVal = parseInt(orderForm.quantity, 10);

    if (!orderForm.productId) {
      showMsg('Selecciona un producto.', 'error');
      return;
    }
    if (isNaN(quantityVal) || quantityVal <= 0) {
      showMsg('La cantidad de productos debe ser mayor a cero.', 'error');
      return;
    }
    if (!isValidEmail(orderForm.userEmail)) {
      showMsg('El correo debe contener "@" y terminar en una extensión válida (.com, .mx, etc.).', 'error');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/ordenes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          productId: orderForm.productId,
          quantity: quantityVal,
          userEmail: orderForm.userEmail
        })
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg('Orden creada exitosamente.');
        setOrderForm({ productId: '', quantity: '', userEmail: '' });
        fetchOrders();
        fetchProducts(); // Refresh stock in dropdown
      } else {
        showMsg(data.message || 'Error al crear la orden.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateOrderStatus = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/ordenes/${selectedOrder.id}/update-status?status=${newOrderStatus}`, {
        method: 'PUT'
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg(`Estado de la orden actualizado a ${newOrderStatus}.`);
        setIsEditingOrderStatus(false);
        const updated = { ...selectedOrder, status: newOrderStatus };
        setSelectedOrder(updated);
        
        fetchOrders();
        fetchProducts(); // Stock might have restored if order was CANCELADA
      } else {
        showMsg(data.message || 'Error al actualizar el estado de la orden.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleInlinePaymentSubmit = async (e) => {
    e.preventDefault();
    const amountVal = parseFloat(inlinePayment.amount);

    if (isNaN(amountVal) || amountVal <= 0) {
      showMsg('El monto del pago debe ser mayor a cero.', 'error');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/pagos/procesar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          orderId: selectedOrder.id,
          amount: amountVal,
          paymentMethod: inlinePayment.paymentMethod,
          userEmail: selectedOrder.userEmail
        })
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg('Pago procesado con éxito.');
        setInlinePayment({ amount: '', paymentMethod: 'CREDIT_CARD' });
        
        setTimeout(async () => {
          try {
            const orderRes = await fetch(`${API_BASE}/ordenes/${selectedOrder.id}`);
            const orderData = await orderRes.json();
            if (orderData.data) {
              setSelectedOrder(orderData.data);
            }
          } catch (e) {
            console.error('Error refreshing selected order:', e);
          }
          fetchOrderBalance(selectedOrder.id);
          fetchOrders();
        }, 1500);
      } else {
        showMsg(data.message || 'Error al procesar el pago.', 'error');
      }
    } catch (err) {
      showMsg('Error de red al procesar el pago.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-content-wrapper fade-in">
      {selectedOrder ? (
        // ── ORDER DETAIL VIEW ──
        <div className="detail-view-container">
          <button 
            onClick={() => { setSelectedOrder(null); setIsEditingOrderStatus(false); }} 
            className="btn btn-secondary btn-back"
          >
            ← Volver al Historial
          </button>
          
          <div className="detail-grid">
            <div className="detail-card">
              <h2>Detalle de la Orden</h2>
              <div className="detail-info">
                <div className="info-group">
                  <label>ID de la Orden</label>
                  <p className="info-value code-id">{selectedOrder.id}</p>
                </div>
                <div className="info-group">
                  <label>Producto</label>
                  <p className="info-value bold">{selectedOrder.productName} <span className="text-secondary">(ID: {selectedOrder.productId})</span></p>
                </div>
                <div className="info-group">
                  <label>Cantidad</label>
                  <p className="info-value">{selectedOrder.quantity} unidades</p>
                </div>
                <div className="info-group">
                  <label>Total de la Orden</label>
                  <p className="info-value price-tag">${selectedOrder.totalPrice.toFixed(2)}</p>
                </div>
                <div className="info-group">
                  <label>Usuario / Email</label>
                  <p className="info-value text-secondary">{selectedOrder.userEmail}</p>
                </div>
                <div className="info-group">
                  <label>Estado de la Orden</label>
                  <div className="info-value">
                    <span className={`status-badge ${selectedOrder.status.toLowerCase()}`}>
                      {selectedOrder.status}
                    </span>
                  </div>
                </div>
                <div className="info-group">
                  <label>Saldo Restante</label>
                  <p className="info-value price-tag bold">
                    {orderBalances[selectedOrder.id] !== undefined ? `$${orderBalances[selectedOrder.id].toFixed(2)}` : 'Cargando...'}
                  </p>
                </div>

                {/* Manual Status Modification */}
                {!isEditingOrderStatus ? (
                  <button 
                    onClick={() => {
                      setIsEditingOrderStatus(true);
                      setNewOrderStatus(selectedOrder.status);
                    }} 
                    className="btn btn-secondary"
                  >
                    Modificar Estado
                  </button>
                ) : (
                  <form onSubmit={handleUpdateOrderStatus} className="minimal-form inline-status-form">
                    <div className="form-group">
                      <label>Nuevo Estado</label>
                      <select
                        value={newOrderStatus}
                        onChange={e => setNewOrderStatus(e.target.value)}
                      >
                        <option value="CREADA">CREADA</option>
                        <option value="PAGO_PARCIAL">PAGO_PARCIAL</option>
                        <option value="PAGADA">PAGADA</option>
                        <option value="CANCELADA">CANCELADA</option>
                        <option value="REEMBOLSADA">REEMBOLSADA</option>
                      </select>
                    </div>
                    <div className="detail-actions">
                      <button type="submit" disabled={loading} className="btn btn-primary btn-sm">Actualizar</button>
                      <button type="button" onClick={() => setIsEditingOrderStatus(false)} className="btn btn-secondary btn-sm">Cancelar</button>
                    </div>
                  </form>
                )}
              </div>
            </div>

            {/* Inline Direct Payment card */}
            {selectedOrder.status !== 'PAGADA' && selectedOrder.status !== 'CANCELADA' && (
              <div className="detail-card payment-card">
                <h2>Realizar Pago Directo</h2>
                <form onSubmit={handleInlinePaymentSubmit} className="minimal-form">
                  <div className="form-group">
                    <label>Método de Pago</label>
                    <select
                      value={inlinePayment.paymentMethod}
                      onChange={e => setInlinePayment({ ...inlinePayment, paymentMethod: e.target.value })}
                    >
                      <option value="CREDIT_CARD">Tarjeta de Crédito</option>
                      <option value="DEBIT_CARD">Tarjeta de Débito</option>
                      <option value="PAYPAL">PayPal</option>
                      <option value="CASH">Efectivo</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Monto a Pagar ($)</label>
                    <input
                      type="number"
                      step="0.01"
                      required
                      placeholder="Monto"
                      value={inlinePayment.amount}
                      onChange={e => setInlinePayment({ ...inlinePayment, amount: e.target.value })}
                    />
                  </div>
                  <div className="info-group">
                    <label>Email Destinatario</label>
                    <p className="info-value text-secondary">{selectedOrder.userEmail}</p>
                  </div>
                  <button type="submit" disabled={loading} className="btn btn-primary">
                    {loading ? 'Procesando...' : 'Aplicar Pago'}
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>
      ) : (
        // ── MAIN ORDERS LIST VIEW ──
        <div className="tab-content">
          <section className="form-section">
            <h2>Registrar Nueva Orden</h2>
            <form onSubmit={handleOrderSubmit} className="minimal-form">
              <div className="form-group">
                <label>Seleccionar Producto</label>
                <select
                  required
                  value={orderForm.productId}
                  onChange={e => setOrderForm({ ...orderForm, productId: e.target.value })}
                >
                  <option value="">-- Selecciona un Producto --</option>
                  {products.map(p => (
                    <option key={p.id} value={p.id}>
                      {p.name} - ${p.price.toFixed(2)} (Stock: {p.stock})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Cantidad</label>
                  <input
                    type="number"
                    required
                    placeholder="1"
                    value={orderForm.quantity}
                    onChange={e => setOrderForm({ ...orderForm, quantity: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Email de Usuario</label>
                  <input
                    type="text"
                    required
                    placeholder="cliente@correo.com"
                    value={orderForm.userEmail}
                    onChange={e => setOrderForm({ ...orderForm, userEmail: e.target.value })}
                  />
                </div>
              </div>
              <button type="submit" disabled={loading} className="btn btn-primary">
                {loading ? 'Creando...' : 'Crear Orden'}
              </button>
            </form>
          </section>

          <section className="table-section">
            <h2>Historial de Órdenes</h2>

            {/* ── DOUBLE SEARCH BAR (ID & EMAIL) ── */}
            <div className="search-bars-grid">
              <form onSubmit={handleSearchOrderById} className="search-form">
                <input
                  type="text"
                  placeholder="Buscar por ID de Orden..."
                  value={searchOrderId}
                  onChange={e => setSearchOrderId(e.target.value)}
                />
                <button type="submit" className="btn btn-primary btn-search">Buscar ID</button>
              </form>

              <form onSubmit={handleSearchOrderByEmail} className="search-form">
                <input
                  type="text"
                  placeholder="Buscar por Email..."
                  value={searchOrderEmail}
                  onChange={e => setSearchOrderEmail(e.target.value)}
                />
                <button type="submit" className="btn btn-primary btn-search">Buscar Email</button>
              </form>

              {searchOrdersActive && (
                <button type="button" onClick={handleClearOrdersSearch} className="btn btn-secondary btn-clear-wide">
                  Limpiar Filtros
                </button>
              )}
            </div>

            <div className="table-wrapper">
              <table className="custom-table table-clickable">
                <thead>
                  <tr>
                    <th>Orden ID</th>
                    <th>Producto</th>
                    <th>Cant.</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Saldo Restante</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.length === 0 ? (
                    <tr>
                      <td colSpan="6" className="empty-row">
                        {searchOrdersResultEmpty ? searchOrdersEmptyMessage : 'No hay órdenes registradas.'}
                      </td>
                    </tr>
                  ) : (
                    orders.map(o => {
                      const balance = orderBalances[o.id];
                      return (
                        <tr 
                          key={o.id}
                          onClick={() => {
                            setSelectedOrder(o);
                            setIsEditingOrderStatus(false);
                          }}
                          title="Haga click para ver detalles y gestionar"
                        >
                          <td className="code-id">{o.id.substring(0, 8)}...</td>
                          <td className="bold">{o.productName}</td>
                          <td>{o.quantity}</td>
                          <td className="price-tag">${o.totalPrice.toFixed(2)}</td>
                          <td>
                            <span className={`status-badge ${o.status.toLowerCase()}`}>
                              {o.status}
                            </span>
                          </td>
                          <td className="price-tag bold">
                            {balance !== undefined ? `$${balance.toFixed(2)}` : 'Cargando...'}
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
