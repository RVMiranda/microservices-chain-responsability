import React from 'react';
import { NavLink } from 'react-router-dom';

export default function Navbar() {
  const tabs = [
    { path: '/productos', label: 'Productos' },
    { path: '/ordenes', label: 'Órdenes' },
    { path: '/pagos/registrar', label: 'Registrar Pago' },
    { path: '/pagos/historial', label: 'Historial de Pagos' },
    { path: '/envios', label: 'Envíos Programados' },
  ];

  return (
    <nav className="header-nav">
      {tabs.map(tab => (
        <NavLink
          key={tab.path}
          to={tab.path}
          className={({ isActive }) => (isActive ? 'active' : '')}
        >
          {tab.label}
        </NavLink>
      ))}
    </nav>
  );
}
