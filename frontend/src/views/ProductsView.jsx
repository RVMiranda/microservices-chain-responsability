import React, { useState, useEffect } from 'react';

const API_BASE = 'http://localhost:8089';

export default function ProductsView({ showMsg }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);

  // Product Search states
  const [searchId, setSearchId] = useState('');
  const [searchActive, setSearchActive] = useState(false);
  const [searchResultEmpty, setSearchResultEmpty] = useState(false);

  // Product Detail & Edit states
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ name: '', description: '', price: '', stock: '' });

  // Form states
  const [productForm, setProductForm] = useState({ name: '', description: '', price: '', stock: '' });

  // Load data
  useEffect(() => {
    fetchProducts();
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

  return (
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
  );
}
