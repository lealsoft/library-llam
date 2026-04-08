import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {
    if (username === 'admin' && password === 'admin') {
      navigate('/dashboard');
    } else {
      alert('Credenciais inválidas. Tente admin / admin');
    }
  };

  return (
    <div className="login-page">
      <div className="left">
        <div className="brand">
          <div className="brand-icon">L</div>
          <div className="brand-name">LLAM <span>Biblioteca</span></div>
        </div>

        <div className="hero">
          <div className="hero-tag">Plataforma Multi-Tenant White Label</div>
          <div className="hero-title">
            Conhecimento<br/>sem <em>fronteiras</em>
          </div>
          <div className="hero-sub">
            Gerencie acervo, leitores e operações com rastreabilidade completa.
            Uma plataforma para bibliotecas que pensam além do convencional.
          </div>
        </div>

        <div className="stats">
          <div>
            <div className="stat-num">24+</div>
            <div className="stat-label">Módulos</div>
          </div>
          <div>
            <div className="stat-num">∞</div>
            <div className="stat-label">Exemplares</div>
          </div>
          <div>
            <div className="stat-num">100%</div>
            <div className="stat-label">White Label</div>
          </div>
          <div>
            <div className="stat-num">Multi</div>
            <div className="stat-label">Tenant</div>
          </div>
        </div>
      </div>

      <div className="right">
        {/* Decorative rings */}
        <div className="deco-ring" style={{ width: '480px', height: '480px', top: '-160px', right: '-180px' }}></div>
        <div className="deco-ring" style={{ width: '280px', height: '280px', top: '-40px', right: '-60px' }}></div>
        <div className="deco-ring" style={{ width: '140px', height: '140px', top: '60px', right: '40px', borderColor: '#C9A84C', opacity: .15 }}></div>

        <div className="form-card">
          <div className="form-title">Entrar</div>
          <div className="form-sub">Acesse com suas credenciais institucionais</div>

          <form onSubmit={(e) => { e.preventDefault(); handleLogin(); }}>
            <div className="field">
              <label>Login ou e-mail</label>
              <div className="input-wrap">
                <input 
                  type="text" 
                  placeholder="admin"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                />
                <span className="input-icon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="5.5" r="2.5" stroke="currentColor" strokeWidth="1.2"/>
                    <path d="M2.5 13.5c0-3 1.5-5 5.5-5s5.5 2 5.5 5" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" fill="none"/>
                  </svg>
                </span>
              </div>
            </div>

            <div className="field">
              <label>Senha</label>
              <div className="input-wrap">
                <input 
                  type="password" 
                  placeholder="admin"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <span className="input-icon">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <rect x="3" y="7" width="10" height="7" rx="2" stroke="currentColor" strokeWidth="1.2"/>
                    <path d="M5.5 7V5a2.5 2.5 0 015 0v2" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" fill="none"/>
                  </svg>
                </span>
              </div>
            </div>

            <div className="row-opts">
              <label className="remember">
                <div className="checkbox"></div>
                Manter conectado
              </label>
              <a href="#" className="forgot">Esqueci a senha</a>
            </div>

            <button type="submit" className="btn-primary">Acessar plataforma</button>
          </form>

          <div className="divider">
            <div className="divider-line"></div>
            <div className="divider-text">ou</div>
            <div className="divider-line"></div>
          </div>

          <button className="btn-sso" type="button">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <rect x="1.5" y="1.5" width="13" height="13" rx="3" stroke="currentColor" strokeWidth="1.2"/>
              <path d="M8 4v8M4 8h8" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round"/>
            </svg>
            Entrar via SSO institucional (Keycloak)
          </button>
        </div>

        <div className="tenant-note">Powered by LLAM Biblioteca · Instituto LLAM</div>
      </div>
    </div>
  );
}
