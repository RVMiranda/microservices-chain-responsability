import React, { useState } from 'react';
import { HashRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './templates/Header';
import Footer from './templates/Footer';
import ProductsView from './views/ProductsView';
import OrdersView from './views/OrdersView';
import RegisterPaymentView from './views/RegisterPaymentView';
import PaymentsHistoryView from './views/PaymentsHistoryView';
import ShippingsHistoryView from './views/ShippingsHistoryView';

export default function App() {
  const [message, setMessage] = useState({ text: '', type: '' }); // type: success | error

  const showMsg = (text, type = 'success') => {
    setMessage({ text, type });
    setTimeout(() => setMessage({ text: '', type: '' }), 5000);
  };

  return (
    <HashRouter>
      <div className="app-container">
        <Header />

        {message.text && (
          <div className={`toast-notification ${message.type}`}>
            <span className="toast-icon">{message.type === 'error' ? '⚠️' : '✓'}</span>
            <span className="toast-text">{message.text}</span>
          </div>
        )}

        <main className="app-main">
          <Routes>
            <Route path="/" element={<Navigate to="/productos" replace />} />
            <Route path="/productos" element={<ProductsView showMsg={showMsg} />} />
            <Route path="/ordenes" element={<OrdersView showMsg={showMsg} />} />
            <Route path="/pagos/registrar" element={<RegisterPaymentView showMsg={showMsg} />} />
            <Route path="/pagos/historial" element={<PaymentsHistoryView showMsg={showMsg} />} />
            <Route path="/envios" element={<ShippingsHistoryView showMsg={showMsg} />} />
            <Route path="*" element={<Navigate to="/productos" replace />} />
          </Routes>
        </main>

        <Footer />
      </div>
    </HashRouter>
  );
}
