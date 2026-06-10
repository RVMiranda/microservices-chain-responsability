import React from 'react';
import Navbar from './Navbar';

export default function Header() {
  return (
    <header className="app-header">
      <div className="header-logo">
        <span className="logo-icon">⚡</span>
        <h1>Microservices Chain Control Center</h1>
      </div>
      <Navbar />
    </header>
  );
}
