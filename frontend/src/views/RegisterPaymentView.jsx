import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function RegisterPaymentView({ showMsg }) {
  const [orders, setOrders] = useState([]);
  const [orderBalances, setOrderBalances] = useState({});
  const [loading, setLoading] = useState(false);

  // Form states
  const [paymentForm, setPaymentForm] = useState({ orderId: '', amount: '', paymentMethod: 'CREDIT_CARD', userEmail: '' });

  // Load data
  useEffect(() => {
    fetchOrders();
  }, []);

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

  const isValidEmail = (email) => {
    if (!email.includes('@')) return false;
    const suffixes = ['.com', '.mx', '.xy', '.ck', '.net', '.org', '.edu', '.co', '.info'];
    return suffixes.some(suffix => email.toLowerCase().endsWith(suffix));
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

  return (
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
  );
}
