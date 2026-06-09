import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function App() {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [orderBalances, setOrderBalances] = useState({}); // orderId -> balance
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ text: '', type: '' }); // type: success | error

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
        // Fetch balances for each order
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
      if (res.ok && data.status !== 'ERROR') {
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
        showMsg(data.message || 'El producto no puede ser eliminado.', 'error');
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
        
        // Refresh orders and balance checks
        setTimeout(() => {
          fetchOrders();
        }, 1500); // Small timeout to ensure Kafka consumers updated MongoDB
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
          <button className={activeTab === 'products' ? 'active' : ''} onClick={() => setActiveTab('products')}>Productos</button>
          <button className={activeTab === 'orders' ? 'active' : ''} onClick={() => setActiveTab('orders')}>Órdenes</button>
          <button className={activeTab === 'payments' ? 'active' : ''} onClick={() => setActiveTab('payments')}>Registrar Pago</button>
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
          <div className="tab-content fade-in">
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
              <div className="table-wrapper">
                <table className="custom-table">
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
                        <td colSpan="5" className="empty-row">No hay productos registrados en el catálogo.</td>
                      </tr>
                    ) : (
                      products.map(p => (
                        <tr key={p.id}>
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
                              onClick={() => handleDeleteProduct(p.id, p.name)}
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

        {activeTab === 'orders' && (
          <div className="tab-content fade-in">
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
              <div className="table-wrapper">
                <table className="custom-table">
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
                        <td colSpan="6" className="empty-row">No hay órdenes registradas.</td>
                      </tr>
                    ) : (
                      orders.map(o => {
                        const balance = orderBalances[o.id];
                        return (
                          <tr key={o.id}>
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

        {activeTab === 'payments' && (
          <div className="tab-content fade-in">
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
