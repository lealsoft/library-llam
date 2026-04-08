import { useParams, useNavigate } from 'react-router-dom';
import { BOOKS } from '../data/mockBooks';
import './LivroDetalhe.css';

export default function LivroDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const book = BOOKS.find(b => b.id === Number(id));

  if (!book) {
    return <div style={{padding: '40px'}}>Livro não encontrado</div>;
  }

  const handleLoginClick = () => {
    navigate('/login');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', width: '100vw', height: '100vh', overflow: 'hidden' }}>
      <header className="pub-header">
        <div className="pub-logo" onClick={() => navigate('/')}>
          <div className="pub-logo-icon">L</div>
          LLAM <span>&nbsp;Biblioteca</span>
        </div>
        <nav className="pub-nav">
          <div className="pub-nav-item active" onClick={() => navigate('/')}>Catálogo</div>
          <div className="pub-nav-item" onClick={handleLoginClick}>Minha Biblioteca</div>
        </nav>
        <div className="pub-header-right">
          <button className="btn-login" onClick={handleLoginClick}>Criar conta</button>
          <button className="btn-entrar" onClick={handleLoginClick}>Entrar</button>
        </div>
      </header>

      <div className="detail-wrap">
        <div className="breadcrumb">
          <a onClick={() => navigate('/')}>Catálogo</a>
          <span className="breadcrumb-sep">›</span>
          <span>{book.title}</span>
        </div>

        <div className="detail-hero">
          <div className="detail-cover" style={{ background: book.cover }}>
            <div className="detail-cover-title">{book.title}</div>
          </div>
          
          <div className="detail-meta">
            <div className="detail-category">{book.category}</div>
            <div className="detail-title">{book.title}</div>
            <div className="detail-author">{book.author}</div>

            <div className="meta-grid">
              <div className="meta-item"><label>ISBN</label><span>{book.isbn}</span></div>
              <div className="meta-item"><label>Editora</label><span>{book.editora}</span></div>
              <div className="meta-item"><label>Ano</label><span>{book.ano}</span></div>
              <div className="meta-item"><label>Páginas</label><span>{book.paginas}</span></div>
              <div className="meta-item"><label>Idioma</label><span>{book.idioma}</span></div>
              <div className="meta-item"><label>Edição</label><span>{book.edicao}</span></div>
            </div>

            <div className="avail-box">
              <div>
                <div className="avail-box-num" style={{ color: book.avail > 0 ? 'var(--green)' : 'var(--red)' }}>
                  {book.avail > 0 ? book.avail : '0'}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '13px', fontWeight: 500, color: 'var(--text0)' }}>exemplares disponíveis</div>
                <div className="avail-box-desc">
                  {book.avail > 0 ? `de ${book.total} totais no acervo` : `todos os ${book.total} exemplares estão emprestados`}
                </div>
              </div>
              {book.digital && (
                 <div style={{ marginLeft: 'auto', textAlign: 'right' }}>
                   <div style={{ fontSize: '10px', color: 'var(--text4)', textTransform: 'uppercase', letterSpacing: '.1em', marginBottom: '2px' }}>Versão digital</div>
                   <div style={{ fontSize: '16px', fontWeight: 500, color: 'var(--blue)' }}>{book.price}</div>
                 </div>
              )}
            </div>

            <div className="detail-actions">
               {book.avail > 0 ? (
                 <button className="da da-borrow" onClick={handleLoginClick}>Emprestar este livro</button>
               ) : (
                 <button className="da da-reserve" onClick={handleLoginClick}>Entrar na fila de reserva</button>
               )}
               {book.digital && (
                 <button className="da da-pdf" onClick={handleLoginClick}>Comprar versão PDF · {book.price}</button>
               )}
            </div>
            <div className="da-login-hint">Faça login para emprestar, reservar ou comprar.</div>
          </div>
        </div>

        <div className="detail-body">
          <div className="detail-section-title">Sinopse</div>
          <div className="sinopse">{book.sinopse}</div>

          <div className="detail-section-title">Exemplares no acervo</div>
          <div className="exemplares-list">
            {book.exemplares?.map((e, idx) => (
              <div key={idx} className="exemplar-row">
                <div className="exemplar-code">{e.code}</div>
                <div className="exemplar-loc">{e.loc}</div>
                <span className={`exemplar-status ${e.status === 'ok' ? 'es-ok' : e.status === 'emp' ? 'es-emp' : 'es-proc'}`}>{e.label}</span>
              </div>
            ))}
            {(!book.exemplares || book.exemplares.length === 0) && (
              <div style={{ fontSize: '12px', color: 'var(--text3)' }}>Informação detalhada de exemplares não disponível no protótipo.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
