import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function App() {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [orderBalances, setOrderBalances] = useState({}); // orderId -> balance
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ text: '', type: '' }); // type: success | error

  // Product Search states
  const [searchId, setSearchId] = useState('');
  const [searchActive, setSearchActive] = useState(false);
  const [searchResultEmpty, setSearchResultEmpty] = useState(false);

  // Product Detail & Edit states
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ name: '', description: '', price: '', stock: '' });

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
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', stock: '' });
  const [orderForm, setOrderForm] = useState({ productId: '', quantity: '', userEmail: '' });
  const [paymentForm, setPaymentForm] = useState({ orderId: '', amount: '', paymentMethod: 'CREDIT_CARD', userEmail: '' });

  // Load data
  useEffect(() => {
    fetchProducts();
    fetchOrders();
  }, []);

  const showMsg = (text, type = 'success') => {
    setMessage({ text, type });
    setTimeout(() => setMessage({ text: '', type: '' }), 5000);
  };

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

  // Product Search functions
  const handleSearchById = async (e) => {
    e.preventDefault();
    if (!searchId.trim()) {
      showMsg('Ingresa un ID válido para buscar.', 'error');
      return;
    }
    setLoading(true);
    setSearchResultEmpty(false);
    try {
      const res = await fetch(`${API_BASE}/productos/${searchId.trim()}`);
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        setProducts([data.data]);
        setSearchActive(true);
        showMsg('Producto encontrado.');
      } else {
        setProducts([]);
        setSearchActive(true);
        setSearchResultEmpty(true);
        showMsg('Producto no encontrado.', 'error');
      }
    } catch (err) {
      setProducts([]);
      setSearchActive(true);
      setSearchResultEmpty(true);
      showMsg('Producto no encontrado.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleClearSearch = () => {
    setSearchId('');
    setSearchActive(false);
    setSearchResultEmpty(false);
    fetchProducts();
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

  // Email Validation helper
  const isValidEmail = (email) => {
    if (!email.includes('@')) return false;
    const suffixes = ['.com', '.mx', '.xy', '.ck', '.net', '.org', '.edu', '.co', '.info'];
    return suffixes.some(suffix => email.toLowerCase().endsWith(suffix));
  };

  // Product Actions
  const handleProductSubmit = async (e) => {
    e.preventDefault();
    const stockVal = parseInt(productForm.stock, 10);
    const priceVal = parseFloat(productForm.price);

    if (isNaN(stockVal) || stockVal <= 0) {
      showMsg('El stock debe ser mayor a cero.', 'error');
      return;
    }
    if (isNaN(priceVal) || priceVal <= 0) {
      showMsg('El precio debe ser mayor a cero.', 'error');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/productos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: productForm.name,
          description: productForm.description,
          price: priceVal,
          stock: stockVal
        })
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR' && data.data) {
        showMsg(`Producto "${productForm.name}" creado con éxito.`);
        setProductForm({ name: '', description: '', price: '', stock: '' });
        fetchProducts();
      } else {
        showMsg(data.message || 'Error al crear el producto.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteProduct = async (id, name) => {
    if (!confirm(`¿Estás seguro de eliminar el producto "${name}"?`)) return;
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/productos/${id}`, { method: 'DELETE' });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg(`Producto "${name}" eliminado exitosamente.`);
        fetchProducts();
      } else {
        showMsg(data.message || 'El producto no puede ser eliminado por estar asociado a una orden.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    const stockVal = parseInt(editForm.stock, 10);
    const priceVal = parseFloat(editForm.price);

    if (isNaN(stockVal) || stockVal <= 0) {
      showMsg('El stock debe ser mayor a cero.', 'error');
      return;
    }
    if (isNaN(priceVal) || priceVal <= 0) {
      showMsg('El precio debe ser mayor a cero.', 'error');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/productos/${selectedProduct.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: editForm.name,
          description: editForm.description,
          price: priceVal,
          stock: stockVal
        })
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg('Producto actualizado correctamente.');
        setIsEditing(false);
        const updated = data.data || { ...selectedProduct, name: editForm.name, description: editForm.description, price: priceVal, stock: stockVal };
        setSelectedProduct(updated);
        fetchProducts();
      } else {
        showMsg(data.message || 'Error al actualizar el producto.', 'error');
      }
    } catch (err) {
      showMsg('Error al conectar con el servidor.', 'error');
    } finally {
      setLoading(false);
    }
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
        fetchProducts(); // Refresh stock in products list
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

  // Payment Actions
  const handlePaymentSubmit = async (e) => {
    e.preventDefault();
    const amountVal = parseFloat(paymentForm.amount);

    if (!paymentForm.orderId) {
      showMsg('Selecciona una orden de la lista.', 'error');
      return;
    }
    if (isNaN(amountVal) || amountVal <= 0) {
      showMsg('El monto del pago debe ser mayor a cero.', 'error');
      return;
    }
    if (!isValidEmail(paymentForm.userEmail)) {
      showMsg('El correo debe contener "@" y terminar en una extensión válida (.com, .mx, etc.).', 'error');
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(`${API_BASE}/pagos/procesar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          orderId: paymentForm.orderId,
          amount: amountVal,
          paymentMethod: paymentForm.paymentMethod,
          userEmail: paymentForm.userEmail
        })
      });
      const data = await res.json();
      if (res.ok && data.status !== 'ERROR') {
        showMsg('Pago procesado con éxito.');
        setPaymentForm({ orderId: '', amount: '', paymentMethod: 'CREDIT_CARD', userEmail: '' });
        
        setTimeout(() => {
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
    <div className="app-container">
      <header className="app-header">
        <div className="header-logo">
          <span className="logo-icon">⚡</span>
          <h1>Microservices Chain Control Center</h1>
        </div>
        <nav className="header-nav">
          <button className={activeTab === 'products' ? 'active' : ''} onClick={() => { setActiveTab('products'); setSelectedProduct(null); setSelectedOrder(null); setIsEditing(false); }}>Productos</button>
          <button className={activeTab === 'orders' ? 'active' : ''} onClick={() => { setActiveTab('orders'); setSelectedProduct(null); setSelectedOrder(null); setIsEditingOrderStatus(false); }}>Órdenes</button>
          <button className={activeTab === 'payments' ? 'active' : ''} onClick={() => { setActiveTab('payments'); setSelectedProduct(null); setSelectedOrder(null); }}>Registrar Pago</button>
        </nav>
      </header>

      {message.text && (
        <div className={`toast-notification ${message.type}`}>
          <span className="toast-icon">{message.type === 'error' ? '⚠️' : '✓'}</span>
          <span className="toast-text">{message.text}</span>
        </div>
      )}

      <main className="app-main">
        {activeTab === 'products' && (
          <div className="tab-content-wrapper fade-in">
            {selectedProduct ? (
              // ── PRODUCT DETAIL VIEW ──
              <div className="detail-view-container">
                <button 
                  onClick={() => { setSelectedProduct(null); setIsEditing(false); }} 
                  className="btn btn-secondary btn-back"
                >
                  ← Volver al Catálogo
                </button>
                
                <div className="detail-card">
                  <h2>Detalle del Producto</h2>
                  {!isEditing ? (
                    <div className="detail-info">
                      <div className="info-group">
                        <label>ID del Producto</label>
                        <p className="info-value code-id">{selectedProduct.id}</p>
                      </div>
                      <div className="info-group">
                        <label>Nombre</label>
                        <p className="info-value bold">{selectedProduct.name}</p>
                      </div>
                      <div className="info-group">
                        <label>Descripción</label>
                        <p className="info-value text-secondary">{selectedProduct.description}</p>
                      </div>
                      <div className="info-group">
                        <label>Precio</label>
                        <p className="info-value price-tag">${selectedProduct.price.toFixed(2)}</p>
                      </div>
                      <div className="info-group">
                        <label>Stock Disponible</label>
                        <p className="info-value">
                          <span className={`badge ${selectedProduct.stock > 0 ? 'stock-ok' : 'stock-empty'}`}>
                            {selectedProduct.stock} unidades
                          </span>
                        </p>
                      </div>
                      
                      <div className="detail-actions">
                        <button 
                          onClick={() => {
                            setIsEditing(true);
                            setEditForm({
                              name: selectedProduct.name,
                              description: selectedProduct.description,
                              price: selectedProduct.price.toString(),
                              stock: selectedProduct.stock.toString()
                            });
                          }} 
                          className="btn btn-primary"
                        >
                          Habilitar Edición
                        </button>
                        <button 
                          onClick={() => {
                            handleDeleteProduct(selectedProduct.id, selectedProduct.name);
                            setSelectedProduct(null);
                          }} 
                          className="btn btn-danger"
                          disabled={loading}
                        >
                          Eliminar Producto
                        </button>
                      </div>
                    </div>
                  ) : (
                    // ── PRODUCT EDIT FORM ──
                    <form onSubmit={handleEditSubmit} className="minimal-form">
                      <div className="form-group">
                        <label>Nombre del Producto</label>
                        <input
                          type="text"
                          required
                          value={editForm.name}
                          onChange={e => setEditForm({ ...editForm, name: e.target.value })}
                        />
                      </div>
                      <div className="form-group">
                        <label>Descripción</label>
                        <input
                          type="text"
                          required
                          value={editForm.description}
                          onChange={e => setEditForm({ ...editForm, description: e.target.value })}
                        />
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Precio ($)</label>
                          <input
                            type="number"
                            step="0.01"
                            required
                            value={editForm.price}
                            onChange={e => setEditForm({ ...editForm, price: e.target.value })}
                          />
                        </div>
                        <div className="form-group">
                          <label>Stock</label>
                          <input
                            type="number"
                            required
                            value={editForm.stock}
                            onChange={e => setEditForm({ ...editForm, stock: e.target.value })}
                          />
                        </div>
                      </div>
                      
                      <div className="detail-actions">
                        <button type="submit" disabled={loading} className="btn btn-primary">
                          {loading ? 'Guardando...' : 'Guardar Cambios'}
                        </button>
                        <button 
                          type="button" 
                          onClick={() => setIsEditing(false)} 
                          className="btn btn-secondary"
                        >
                          Cancelar
                        </button>
                      </div>
                    </form>
                  )}
                </div>
              </div>
            ) : (
              // ── MAIN CATALOGUE VIEW ──
              <div className="tab-content">
                <section className="form-section">
                  <h2>Añadir Producto</h2>
                  <form onSubmit={handleProductSubmit} className="minimal-form">
                    <div className="form-group">
                      <label>Nombre del Producto</label>
                      <input
                        type="text"
                        required
                        placeholder="Ej. iPhone 15 Pro"
                        value={productForm.name}
                        onChange={e => setProductForm({ ...productForm, name: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Descripción</label>
                      <input
                        type="text"
                        required
                        placeholder="Ej. Color titanio natural, 256GB"
                        value={productForm.description}
                        onChange={e => setProductForm({ ...productForm, description: e.target.value })}
                      />
                    </div>
                    <div className="form-row">
                      <div className="form-group">
                        <label>Precio ($)</label>
                        <input
                          type="number"
                          step="0.01"
                          required
                          placeholder="999.99"
                          value={productForm.price}
                          onChange={e => setProductForm({ ...productForm, price: e.target.value })}
                        />
                      </div>
                      <div className="form-group">
                        <label>Stock Inicial</label>
                        <input
                          type="number"
                          required
                          placeholder="10"
                          value={productForm.stock}
                          onChange={e => setProductForm({ ...productForm, stock: e.target.value })}
                        />
                      </div>
                    </div>
                    <button type="submit" disabled={loading} className="btn btn-primary">
                      {loading ? 'Creando...' : 'Crear Producto'}
                    </button>
                  </form>
                </section>

                <section className="table-section">
                  <h2>Catálogo de Productos</h2>

                  <div className="search-bar-container">
                    <form onSubmit={handleSearchById} className="search-form">
                      <input
                        type="text"
                        placeholder="Buscar producto por ID..."
                        value={searchId}
                        onChange={e => setSearchId(e.target.value)}
                      />
                      <button type="submit" className="btn btn-primary btn-search">Buscar</button>
                      {searchActive && (
                        <button type="button" onClick={handleClearSearch} className="btn btn-secondary btn-clear">Limpiar</button>
                      )}
                    </form>
                  </div>

                  <div className="table-wrapper">
                    <table className="custom-table table-clickable">
                      <thead>
                        <tr>
                          <th>Nombre</th>
                          <th>Descripción</th>
                          <th>Precio</th>
                          <th>Stock</th>
                          <th>Acciones</th>
                        </tr>
                      </thead>
                      <tbody>
                        {products.length === 0 ? (
                          <tr>
                            <td colSpan="5" className="empty-row">
                              {searchResultEmpty ? 'Producto no encontrado.' : 'No hay productos registrados en el catálogo.'}
                            </td>
                          </tr>
                        ) : (
                          products.map(p => (
                            <tr 
                              key={p.id} 
                              onClick={() => {
                                setSelectedProduct(p);
                                setIsEditing(false);
                              }}
                              title="Haga click para ver detalles y editar"
                            >
                              <td className="bold">{p.name}</td>
                              <td className="text-secondary">{p.description}</td>
                              <td className="price-tag">${p.price.toFixed(2)}</td>
                              <td>
                                <span className={`badge ${p.stock > 0 ? 'stock-ok' : 'stock-empty'}`}>
                                  {p.stock} u.
                                </span>
                              </td>
                              <td>
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleDeleteProduct(p.id, p.name);
                                  }}
                                  className="btn btn-danger btn-sm"
                                  disabled={loading}
                                >
                                  Eliminar
                                </button>
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>
                </section>
              </div>
            )}
          </div>
        )}

        {activeTab === 'orders' && (
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
        )}

        {activeTab === 'payments' && (
          <div className="tab-content-wrapper fade-in">
            <section className="form-section central-form">
              <h2>Registrar Pago para Orden</h2>
              <form onSubmit={handlePaymentSubmit} className="minimal-form">
                <div className="form-group">
                  <label>Seleccionar Orden de Compra</label>
                  <select
                    required
                    value={paymentForm.orderId}
                    onChange={e => {
                      const oid = e.target.value;
                      const ord = orders.find(o => o.id === oid);
                      setPaymentForm({
                        ...paymentForm,
                        orderId: oid,
                        userEmail: ord ? ord.userEmail : ''
                      });
                    }}
                  >
                    <option value="">-- Selecciona una Orden --</option>
                    {orders
                      .filter(o => o.status !== 'PAGADA' && o.status !== 'CANCELADA')
                      .map(o => {
                        const bal = orderBalances[o.id];
                        return (
                          <option key={o.id} value={o.id}>
                            {o.productName} (${o.totalPrice.toFixed(2)}) - Saldo: {bal !== undefined ? `$${bal}` : 'Cargando'} (ID: {o.id.substring(0, 8)})
                          </option>
                        );
                      })}
                  </select>
                </div>
                <div className="form-group">
                  <label>Método de Pago</label>
                  <select
                    required
                    value={paymentForm.paymentMethod}
                    onChange={e => setPaymentForm({ ...paymentForm, paymentMethod: e.target.value })}
                  >
                    <option value="CREDIT_CARD">Tarjeta de Crédito</option>
                    <option value="DEBIT_CARD">Tarjeta de Débito</option>
                    <option value="PAYPAL">PayPal</option>
                    <option value="CASH">Efectivo</option>
                  </select>
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>Monto a Pagar ($)</label>
                    <input
                      type="number"
                      step="0.01"
                      required
                      placeholder="Monto"
                      value={paymentForm.amount}
                      onChange={e => setPaymentForm({ ...paymentForm, amount: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Confirmar Email</label>
                    <input
                      type="text"
                      required
                      placeholder="cliente@correo.com"
                      value={paymentForm.userEmail}
                      onChange={e => setPaymentForm({ ...paymentForm, userEmail: e.target.value })}
                    />
                  </div>
                </div>
                <button type="submit" disabled={loading} className="btn btn-primary">
                  {loading ? 'Procesando...' : 'Aplicar Pago'}
                </button>
              </form>
            </section>
          </div>
        )}
      </main>

      <footer className="app-footer">
        <p>Administrador de Servicios Distribuidos • React & Spring Cloud Gateway</p>
      </footer>
    </div>
  );
}
