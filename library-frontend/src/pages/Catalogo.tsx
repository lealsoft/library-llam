import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BOOKS } from '../data/mockBooks';
import './Catalogo.css';

export default function Catalogo() {
  const navigate = useNavigate();
  const [onlyAvailable, setOnlyAvailable] = useState(false);

  const handleLoginClick = () => {
    navigate('/login');
  };

  const toggleCheck = () => {
    setOnlyAvailable(!onlyAvailable);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', width: '100vw', height: '100vh', overflow: 'hidden' }}>
      <header className="pub-header">
        <div className="pub-logo">
          <div className="pub-logo-icon">L</div>
          LLAM <span>&nbsp;Biblioteca</span>
        </div>
        <nav className="pub-nav">
          <div className="pub-nav-item active">Catálogo</div>
          <div className="pub-nav-item" onClick={handleLoginClick}>Minha Biblioteca</div>
        </nav>
        <div className="pub-header-right">
          <button className="btn-login" onClick={handleLoginClick}>Criar conta</button>
          <button className="btn-entrar" onClick={handleLoginClick}>Entrar</button>
        </div>
      </header>

      <div className="catalog-page">
        <div className="hero-banner">
          <div className="hero-tag">Instituto LLAM</div>
          <div className="hero-title">Explore nosso <em>acervo</em></div>
          <div className="hero-sub">1.847 títulos disponíveis para empréstimo, reserva e compra digital. Encontre seu próximo livro.</div>
          <div className="hero-stats">
            <div><div className="hero-stat-num">1.847</div><div className="hero-stat-lbl">Títulos</div></div>
            <div><div className="hero-stat-num">4.291</div><div className="hero-stat-lbl">Exemplares</div></div>
            <div><div className="hero-stat-num">12</div><div className="hero-stat-lbl">Categorias</div></div>
            <div><div className="hero-stat-num">248</div><div className="hero-stat-lbl">Versões digitais</div></div>
          </div>
        </div>

        <div className="catalog-toolbar">
          <div className="search-wrap">
            <input type="text" placeholder="Buscar por título, autor, ISBN…" />
            <span className="search-icon">
              <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                <circle cx="6" cy="6" r="4.5" stroke="currentColor" strokeWidth="1.3"/>
                <path d="M9.5 9.5L12.5 12.5" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round"/>
              </svg>
            </span>
          </div>
          <select>
            <option>Todas as categorias</option>
            <option>Tecnologia</option>
            <option>Literatura Brasileira</option>
          </select>
          <select>
            <option>Popularidade</option>
            <option>Título A–Z</option>
            <option>Autor A–Z</option>
          </select>
          <label className="filter-check" onClick={toggleCheck}>
            <div className={`cb ${onlyAvailable ? 'on' : ''}`}></div>
            Apenas disponíveis
          </label>
          <div className="results-count">10 de 1.847 títulos</div>
        </div>

        <div className="catalog-wrap">
          <div className="book-grid">
            {BOOKS.map(b => (
              <div key={b.id} className="book-card" onClick={() => navigate(`/livro/${b.id}`)}>
                <div className="book-cover" style={{background: b.cover}}>
                  {b.avail > 0 ? (
                    <span className="avail-pill avail-ok">{b.avail} disp.</span>
                  ) : b.digital ? (
                    <span className="avail-pill avail-digital">Só PDF</span>
                  ) : (
                    <span className="avail-pill avail-none">Indispon.</span>
                  )}
                  <div className="book-cover-title">{b.title}</div>
                </div>
                <div className="book-info">
                  <div className="book-title">{b.title}</div>
                  <div className="book-author">{b.author}</div>
                  <div className="book-category">{b.category}</div>
                  <div className="book-actions">
                    {b.avail > 0 ? (
                      <>
                        <button className="ba ba-borrow" onClick={(e) => { e.stopPropagation(); handleLoginClick(); }}>Emprestar</button>
                        {b.digital && <button className="ba ba-pdf" onClick={(e) => { e.stopPropagation(); handleLoginClick(); }}>PDF</button>}
                      </>
                    ) : b.digital ? (
                      <button className="ba ba-pdf" onClick={(e) => { e.stopPropagation(); handleLoginClick(); }}>Comprar PDF</button>
                    ) : (
                      <button className="ba ba-reserve" onClick={(e) => { e.stopPropagation(); handleLoginClick(); }}>Reservar</button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
          
          <div className="pagination" style={{marginTop:'8px'}}>
            <button className="pg-btn">‹</button>
            <button className="pg-btn active">1</button>
            <button className="pg-btn">2</button>
            <button className="pg-btn">3</button>
            <span style={{color:'var(--text4)', fontSize:'13px', padding:'0 4px'}}>…</span>
            <button className="pg-btn">185</button>
            <button className="pg-btn">›</button>
            <span style={{fontSize:'11px', color:'var(--text3)', marginLeft:'10px'}}>10 por página</span>
          </div>
        </div>
      </div>
    </div>
  );
}
